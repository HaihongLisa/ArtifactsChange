package SourceFile;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

/**
 * Create JIRA txt files, each jira id has its own jira file
 * The txt naming rules: jiraid.txt
 * The txt content: only jira summary and jira description without jira id
 * @author Haihong Luo
 */
public class CreateJIRAFiles {
	private List<String> issueFiles;
	private List<String> issueText;
	private String location;
	public CreateJIRAFiles(String token, String version1, String version2, String location) throws Exception {
		FetchJiraIssues fetchJIRAIsuses = new FetchJiraIssues(token, version1, version2);
		fetchJIRAIsuses.fillExcelWithJIRA();
		issueFiles = FetchJiraIssues.issueFiles;
		this.location = location;
	}
	
	public List<String> getIssueText() {
		return issueText;
	}
	
	/**
	 * Read each issue file
	 * Create a issue txt file for each row, only column 2(summary) and column 3(description) will be included in the file
	 * @throws IOException 
	 */
	public void createJiraFiles() throws IOException {
		issueText = new ArrayList<String>();
		for(String str : issueFiles) {
			//Get issue file
			FileInputStream fileInputStream = new FileInputStream(str);
			HSSFWorkbook issueBook = new HSSFWorkbook(fileInputStream);
			HSSFSheet issueSheet = issueBook.getSheetAt(0);
			
			//Iterate all rows
			for(int i = 1; i <= issueSheet.getLastRowNum(); i++) {
				//Append string which will be stored in the txt file
				Row row = issueSheet.getRow(i);
				StringBuilder sb = new StringBuilder();
				String summary = row.getCell(2).toString();
				sb.append(summary);
				sb.append("\n");
				String descr = row.getCell(3).toString();
				sb.append(descr);
				String id = row.getCell(0).toString();

				//Create a text file and store the text
				String fileName = location + id + ".txt";
				issueText.add(fileName);
				BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));
				bufferedWriter.write(sb.toString());
				bufferedWriter.close();
			}
		}
	}
}
