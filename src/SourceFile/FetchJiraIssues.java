package SourceFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Fetch jira issues for the version and store in excel.
 * It has issue key, Type, Summary, Description, Created, Updated
 * The content of the description is complete but the order of the code text may displayed in wrong line
 * @author Haihong Luo
 *
 */
public class FetchJiraIssues {
	private static HashMap<String, String> versionURL;
	private String baseURL = "https://issues.apache.org/jira/browse/";
	private String base = "https://issues.apache.org/jira/issues/?jql=";
	private static String token;
	private String version1;
	private String version2;
	public static List<String> issueFiles;
	
	public FetchJiraIssues(String token, String version1, String version2) {
		versionURL = new HashMap<String, String>();
		this.version1 = version1;
		this.version2 = version2;
		this.token = token;
		issueFiles = new ArrayList<String>();
	}
	
	public HashMap<String, String> getVersion() {
		return versionURL;
	}
	
	/**
	 * Get all versions list from Jira
	 * @param token
	 * 		project name, for example, CASSANDRA
	 * @throws Exception
	 */
	public void getLinks() throws Exception {
		String url = baseURL + token;
		//Get project jira link
		Document doc = Jsoup.connect(url).get();
		String versionLink = doc.select("a[id=com.atlassian.jira.jira-projects-plugin:versions-panel-panel]").first().absUrl("href") + "&subset=-1";
		
		//Get all versions
		Document allVersions = Jsoup.connect(versionLink).get();
		Elements versionList = allVersions.select("a[class=summary]");
		
		
		for(Element element : versionList) {
			String version = element.text();
			String link = element.attr("href");
			versionURL.put(version, link);
		}
	}
	
	/**
	 * Fetch all issues whose fixed version is greater than version1 and less or equals to version2
	 * @return
	 * 	Fetch excel file for each issue version
	 * 	file name: Issue_version.xls
	 */
	public void fillExcelWithJIRA() throws Exception {	
		getLinks();
		HashMap<String, HashMap<String, String>> issuesMap = new HashMap<String, HashMap<String, String>>();
		if(!versionURL.containsKey(version1) || !versionURL.containsKey(version2)) {
			throw new IllegalArgumentException("Cannot find related version in the Jira versions list, please select another version");
		}
		
		for(String version : versionURL.keySet()) {
			if(compareVersion(version,version1) > 0 && compareVersion(version, version2) <= 0) {
				createJiraIssueExcel(version);
				String fileName = "IssuesFiles/Issue_" + version + ".xls";
				FileInputStream file = new FileInputStream(new File(fileName));
				
				//Get the workbook instance for XLS file
				HSSFWorkbook workbook = new HSSFWorkbook(file);
				//Get the sheet from the workbook
				HSSFSheet sheet = workbook.getSheet("issues");
				int count = 1;
				HashSet<String> issues = new HashSet<String>();
				for(int i = 0; i < 6; i++) {
					int index = 50 * i;
					String page = base + "fixVersion+%3D+" + version + "+AND+project+%3D+" + token + "&startIndex=" + index;
					
					Document doc = Jsoup.connect(page).get();
					
					//For each page get issue list
					Elements links = doc.select("li[data-key]");
					for(Element element : links) {
						String key = element.attr("data-key");
						if(issues.contains(key)) {
							continue;
						} 
						issues.add(key);
						String type = element.select("img").first().attr("alt");
						String summary = element.attr("title");
						
						//Issue detail page
						String link = baseURL + key;
						//Visit issue detail page and get related infos
						Document issueDetail = Jsoup.connect(link).get();
						
						//Fetch description, the content is complete but lines is not as same original
						Elements description = issueDetail.select("div[id=description-val]").first().select("p");
						Elements codes =  issueDetail.select("div[id=description-val]").first().select("pre[class=code-java]");
						StringBuilder descr = new StringBuilder();
						for(Element d : description) {
							descr.append(d.text());
							descr.append("\n");
						}
						for(Element code : codes) {
							descr.append(code.text());
							descr.append("\n");
						}
						
						//Issue related dates
						Elements dates = issueDetail.select("dd[title]");
						String created = dates.get(0).attr("title");
						String updated = dates.get(1).attr("title");
						
						//Fill row
						Row row = sheet.createRow(count);
						//Fill cell0 with issue id
						Cell cell0 = row.createCell(0);
						cell0.setCellValue(key);
						
						//Fill cell1 with issue type
						Cell cell1 = row.createCell(1);
						cell1.setCellValue(type);
						
						//Fill cell2 with issue summary
						Cell cell2 = row.createCell(2);
						cell2.setCellValue(summary);
						
						//Fill cell3 with issue description
						Cell cell3 = row.createCell(3);
						cell3.setCellValue(descr.toString());
						
						//Fill cell4 with issue created time
						Cell cell4 = row.createCell(4);
						cell4.setCellValue(created);
						
						//Fill cell5 with issue updated time
						Cell cell5 = row.createCell(5);
						cell5.setCellValue(updated);
						
						count++;
					}
				}
				file.close();
				FileOutputStream out = new FileOutputStream(new File(fileName));
				workbook.write(out);
				out.close();
			}
		}
	}
	
    /**
     * Create Excel for fill data
     * @throws Exception 
     */
	private void createJiraIssueExcel(String version) throws Exception {
		try{
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Issues");
			//Create a new row in current sheet
			Row row = sheet.createRow(0);
			//Create a new cell in current row;
			Cell cell0 = row.createCell(0);
			Cell cell1 = row.createCell(1);
			Cell cell2 = row.createCell(2);
			Cell cell3 = row.createCell(3);
			Cell cell4 = row.createCell(4);
			Cell cell5 = row.createCell(5);
			cell0.setCellValue("Key");
			cell1.setCellValue("Type");
			cell2.setCellValue("Summary");
			cell3.setCellValue("Description");
			cell4.setCellValue("Created");
			cell5.setCellValue("Updated");
		
			String fileName = "IssuesFiles/Issue_" + version + ".xls";
			FileOutputStream out = new FileOutputStream(new File(fileName));
			workbook.write(out);
			out.close();
			issueFiles.add(fileName);
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/** Compare whether the two version is same or not
	 * If same, return 0;
	 * If first version is greater, return 1;
	 * If latter version is greater, return -1;
	 */
    public int compareVersion(String version1, String version2) {
        if(version1 == null || version2 == null || version1.equals(version2))    return 0;
        
        int index1 = 0;
        int index2 = 0;
        while(index1 < version1.length() || index1 < version2.length()) {
            int sum1 = 0;
            int sum2 = 0;
            while(index1 < version1.length() && version1.charAt(index1) != '.') {
                sum1 = sum1 * 10 + (version1.charAt(index1) - '0');
                index1++;
            }
            
            while(index2 < version2.length() && version2.charAt(index2) != '.') {
                sum2 = sum2 * 10 + (version2.charAt(index2) - '0');
                index2++;
            }
            
            if(sum1 > sum2) return 1;
            if(sum1 < sum2) return -1;
            index1++;
            index2++;
        }
        return 0;
    }
}
