package SourceFile;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

/**
 * Create git commits txt files, each commit id has its own commit file
 * The txt naming rules: commitid.txt
 * The txt content: only commit description
 * @author Haihong Luo
 */
public class CreateCommitFiles {
	private List<String> commitFiles;
	private List<String> commitExcel;
	private List<String> commitText;
	private String location;
	public CreateCommitFiles(String token, String version1, String version2, String location) throws Exception {
		FetchCommits fetchCommits = new FetchCommits(token, version1, version2);
		fetchCommits.fillExcelWithCommits();
		commitFiles = fetchCommits.getCommitFiles();
		this.location = location;
	}
	
	public void fillCommitExcel() {
		commitExcel = new ArrayList<String>();
		for(String str : commitFiles) {
			String excelName = str.substring(0, str.length() - 4) + ".xls";
			commitExcel.add(excelName);
		}
	}
	
	public List<String> getCommitText() {
		return commitText;
	}
	/**
	 * Read each commit file
	 * Create a commit txt file for each row, only column 3(commit description) will be included in the file
	 * @throws Exception 
	 */
	public void createCommitFiles() throws Exception {
		fillCommitExcel();
		commitText = new ArrayList<String>();
		for(String str : commitExcel) {
			//Get commit file
			FileInputStream fileInputStream = new FileInputStream(str);
			HSSFWorkbook commitBook = new HSSFWorkbook(fileInputStream);
			HSSFSheet commitSheet = commitBook.getSheetAt(0);
			
			//Iterate all rows
			for(int i = 1; i <= commitSheet.getLastRowNum(); i++) {
				Row row = commitSheet.getRow(i);
				//Fetch commit description
				String description = row.getCell(3).toString();
				String id = row.getCell(0).toString();
				
				//Create a text file and store the text
				String fileName = location + id + ".txt";
				commitText.add(fileName);
				BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));
				bufferedWriter.write(description);
				bufferedWriter.close();
			}
		}
	}
}
