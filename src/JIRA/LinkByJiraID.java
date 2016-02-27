package JIRA;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import Policies.ByJiraID;
import SourceFile.FetchCommits;
import SourceFile.FetchJiraIssues;

/**
 * Default policy, if commit description contains JIRA id, create a link between JIRA and the commit
 * Output: json file with links
 * @author Haihong Luo
 */
public class LinkByJiraID implements ByJiraID {
	private List<String> issueFiles;
	private ArrayList<String> byJIRAIDResult;
	
	public LinkByJiraID() {
		issueFiles = FetchJiraIssues.issueFiles; 
	}
	
	public ArrayList<String> getResultLink() {
		return byJIRAIDResult;
	}
	
	/**
	 * Read each issue file and commit file between two version
	 * 	1. Iterate the issue file row by row
	 *  2. Iterate the commit file row by row
	 *  3. If the issue is found in the commit description
	 *  4. Setup the link between them
	 * Time complexity: o(n^2)
	 * Space complexity:o(1);
	 */
	@Override
	public void setLinkByJiraID(String location) throws Exception {
		byJIRAIDResult = new ArrayList<String>();
		for(String str : issueFiles) {
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
				//Get JIRA ID
				String key = row.getCell(0).toString();
				Iterator<Row> iterator = commitSheet.iterator();
				JSONObject obj = new JSONObject();
				obj.put("Issue_ID", key);
				JSONArray array = new JSONArray();
				//Iterate the whole commit list and find whether there is any match
				while(iterator.hasNext()) {
					Row commitRow = iterator.next();
					//Fetch commit description
					String commitDescr = commitRow.getCell(3).toString();
					if(commitDescr.toLowerCase().contains(key.toLowerCase())) {
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
					}
				}
				obj.put("Links", array);
				links.add(obj);
			}
			int len = commits[1].length();
			//Create json file named as "Links_version.json"
			String linkFile = location + "Links_ByJiraID_" + commits[1].substring(0, len - 4) + ".json";
			byJIRAIDResult.add(linkFile);
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
