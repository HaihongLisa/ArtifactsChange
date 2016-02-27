package JIRA;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import Policies.ByKeyWord;
import Preprocess.StopWordsMgr;
import SourceFile.FetchCommits;
import SourceFile.FetchJiraIssues;

public class LinkByKeyWord implements ByKeyWord {
	private List<String> issuesFiles;
	private ArrayList<String> byKeyWordResult;
	public LinkByKeyWord() {
		issuesFiles = FetchJiraIssues.issueFiles;
	}
	
	public ArrayList<String> getResultLink() {
		return byKeyWordResult;
	}
	
	@Override
	public void setLinkByKeyWord(String location) throws Exception {
		byKeyWordResult = new ArrayList<String>();
		for(String str : issuesFiles) {
			//Get issue file
			FileInputStream fileInputStream = new FileInputStream(str);
			HSSFWorkbook issueBook = new HSSFWorkbook(fileInputStream);
			HSSFSheet issueSheet = issueBook.getSheetAt(0);
			
			//Get commits file
			String[] commits = str.split("_");
			String commitFile = "CommitsFiles/Commits_" + commits[1];
			FileInputStream inputStream = new FileInputStream(commitFile);
			HSSFWorkbook commitBook = new HSSFWorkbook(inputStream);
			HSSFSheet commitSheet = commitBook.getSheetAt(0);
			JSONArray links = new JSONArray();
			//Iterate rows
			for(int i = 1; i <= issueSheet.getLastRowNum(); i++) {
				Row row = issueSheet.getRow(i);
				//Get updated time of issue
				String key = row.getCell(0).toString();
				String jiraDescription = row.getCell(3).toString();
				
				JSONObject obj = new JSONObject();
				obj.put("Issue_ID", key);
				//To store links -- if commits keyword can be found in the jira description
				JSONArray array = new JSONArray();
				//Iterate the whole commit list and find whether there is any match
				for(int j = 1; j <= commitSheet.getLastRowNum(); j++) {
					Row commitRow = commitSheet.getRow(j);
					//Fetch commit description
					String commitDescription = commitRow.getCell(3).toString();
					commitDescription = StopWordsMgr.processText(commitDescription);
					String[] commitsKeyWords = commitDescription.split(" ");
					boolean isAdded = false;
					for(String string : commitsKeyWords) {
						if(isAdded) {
							break;
						}
						
						if(jiraDescription.contains(string)) {
							JSONObject com = new JSONObject();
							//Fetch commitID
							String commitID = commitRow.getCell(0).toString();
							
							//Fetch changed files
							String changedFiles = commitRow.getCell(4).toString();
							String[] files = changedFiles.split("\n");
							JSONArray filesArray = new JSONArray();
							for(String s : files) {
								filesArray.add(s);
							}
							com.put("Commit", commitID);
							com.put("Files", filesArray);
							array.add(com);
							isAdded = true;
							}
						}
					}
					obj.put("Links", array);
					links.add(obj);
				}
				int len = commits[1].length();
				//Create json file named as "Links_version.json"
				String linkFile = "Links_ByKeyWord_" + commits[1].substring(0, len - 4) + ".json";
				byKeyWordResult.add(linkFile);
				FileWriter file = new FileWriter(linkFile);
				file.write(links.toString());
				file.flush();
				file.close();
			}
	}

	/**
	 * Only for debug purpose, it will parse the existing json file in the console if you call this function
	 * So that you can check whether it the result is correct or not
	 * Only need to changed the string jsonFile into the file you want to parse
	 */
	public void parseJson(String fileName) {
		JSONParser parser = new JSONParser();
		try {
			JSONArray array = (JSONArray)parser.parse(new FileReader(fileName));
			for(Object object : array) {
				JSONObject issue = (JSONObject) object;
				String issueID = (String) issue.get("Issue_ID");
				System.out.println(issueID);
				System.out.println("Links");
				
				JSONArray links = (JSONArray) issue.get("Links");
				for(Object link : links) {
					JSONObject trace = (JSONObject) link;
					String commitID = (String) trace.get("Commit");
					System.out.println("    Commit:" + commitID);
					JSONArray files = (JSONArray) trace.get("Files");
					System.out.println("    " + "Files");
					for(Object file : files) {
						System.out.println("          " + file.toString());
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
