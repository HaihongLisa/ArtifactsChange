package SourceFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * Convert git commits log into excel with format.
 * The excel has below columns:CommitID, Author, CommitTime, Description, ChangedFiles
 * Each version has one excel and the excel is named as Commits_version.xls
 * 
 * 	How to get git commits log between two versions: (using cassandra 2.1.13 for example)
 * 		git log --stat cassandra-2.1.12..cassandra-2.1.13 -->Commits_2.1.13.txt
 * @author Haihong Luo
 *
 */
public class FetchCommits {
	private HashMap<String, String> versions;
	private List<String> commitsFiles;
	private String fileName = "CommitsFiles/Commits_";
	private String version1;
	private String version2;
	
	/**
	 * Construct fetch commits
	 * @param token
	 * 	Project name, such as CASSANDRA
	 * @param version1
	 * 	The earlier version
	 * @param version2
	 * 	The latest version
	 * @throws Exception
	 */
	public FetchCommits(String token, String version1, String version2) throws Exception {
		commitsFiles = new ArrayList<String>();
		FetchJiraIssues issues = new FetchJiraIssues(token, version1, version2);
		issues.getLinks();
		versions = issues.getVersion();
		this.version1 = version1;
		this.version2 = version2;
	}
	
	public List<String> getCommitFiles() {
		return commitsFiles;
	}
	
	public void fillExcelWithCommits() {
		getCommitFileName();
		createCommitsVersionExcel();
		for(String key : commitsFiles) {
			try {
				//Read commits txt file
				String line = null;
				FileReader fileReader = new FileReader(key);
				BufferedReader br = new BufferedReader(fileReader);
				
				//Open commit xls file and fill it
				String file = key.substring(0, key.length() - 4) + ".xls";
				FileInputStream commitExcel = new FileInputStream(new File(file));
				//Get the workbook intance for xls file
				HSSFWorkbook workbook = new HSSFWorkbook(commitExcel);
				HSSFSheet sheet = workbook.getSheetAt(0);
				int count = 1;
				
				StringBuilder commit = new StringBuilder();
				while((line = br.readLine()) != null) {
					int commitTagIndex = line.indexOf("commit");
					if(commitTagIndex == 0 && commit.length() != 0) {
						Row row = sheet.createRow(count);
						Cell cell0 = row.createCell(0);
						Cell cell1 = row.createCell(1);
						Cell cell2 = row.createCell(2);
						Cell cell3 = row.createCell(3);
						Cell cell4 = row.createCell(4);
						String[] strs = commit.toString().split("\n");
						StringBuilder descr = new StringBuilder();
						StringBuilder changedFiles = new StringBuilder();
						for(String str : strs) {
							int commitIndex = str.indexOf("commit");
							if(commitIndex == 0) {
								cell0.setCellValue(str.substring(7));
							}
							
							int authorIndex = str.indexOf("Author");
							if(authorIndex == 0) {
								cell1.setCellValue(str.substring(8));
							}
							
							int dateIndex = str.indexOf("Date");
							if(dateIndex == 0) {
								cell2.setCellValue(str.substring(8, str.length() - 6));
							}
							
							int descrIndex = str.indexOf("    ");
							if(descrIndex == 0) {
								descr.append(str);
							}
							
							if(str.contains("|")) {
								int index = str.indexOf("|");
								changedFiles.append(str.substring(0, index - 1));
								changedFiles.append("\n");
							}	
						}	
							cell3.setCellValue(descr.toString());
							cell4.setCellValue(changedFiles.toString());
							count++;
							commit = new StringBuilder();
							commit.append(line);
							commit.append("\n");
					} else {
						commit.append(line);
						commit.append("\n");
					}
				}
				if(commit.length() != 0) {
					Row row = sheet.createRow(count);
					Cell cell0 = row.createCell(0);
					Cell cell1 = row.createCell(1);
					Cell cell2 = row.createCell(2);
					Cell cell3 = row.createCell(3);
					Cell cell4 = row.createCell(4);
					String[] strs = commit.toString().split("\n");
					StringBuilder descr = new StringBuilder();
					StringBuilder changedFiles = new StringBuilder();
					for(String str : strs) {
						if(str.contains("commit")) {
							cell0.setCellValue(str.substring(7));
						}
						
						if(str.contains("Author")) {
							cell1.setCellValue(str.substring(8));
						}
						
						if(str.contains("Date")) {
							cell2.setCellValue(str.substring(8, str.length() - 6));
						}
						
						if(str.contains("    ")) {
							descr.append(str);
						}
						
						if(str.contains("|")) {
							int index = str.indexOf("|");
							changedFiles.append(str.substring(0, index - 1));
							changedFiles.append("\n");
						}	
					}	
						cell3.setCellValue(descr.toString());
						cell4.setCellValue(changedFiles.toString());
						count++;
						commit = new StringBuilder();
						commit.append(line);
						commit.append("\n");
				}
				br.close();
				commitExcel.close();
				FileOutputStream out = new FileOutputStream(new File(file));
				workbook.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Get commits file name
	 */
	public void getCommitFileName() {
		for(String key : versions.keySet()) {
			if(compareVersion(key, version1) > 0 && compareVersion(key, version2) <= 0) {
				String file = fileName + key +".txt";
				commitsFiles.add(file);
			} 
		}
	}
	
	/**
	 * Loop commitsFile list and create the corresponding xls with below column
	 * 	CommitID, Author, CommitTime, Description, ChangedFiles
	 */
	public void createCommitsVersionExcel() {
		for(String key : commitsFiles) {
			try{
				HSSFWorkbook workbook = new HSSFWorkbook();
				HSSFSheet sheet = workbook.createSheet("commits");
				//Create a new row in current sheet
				Row row = sheet.createRow(0);
				//Create a new cell in current row;
				Cell cell0 = row.createCell(0);
				Cell cell1 = row.createCell(1);
				Cell cell2 = row.createCell(2);
				Cell cell3 = row.createCell(3);
				Cell cell4 = row.createCell(4);
				cell0.setCellValue("CommitID");
				cell1.setCellValue("Author");
				cell2.setCellValue("CommitTime");
				cell3.setCellValue("Description");
				cell4.setCellValue("ChangedFiles");
			
				String version = key.substring(0, key.length() - 4);
				String file = version + ".xls";
				FileOutputStream out = new FileOutputStream(new File(file));
				workbook.write(out);
				out.close();
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			} catch(IOException e) {
				e.printStackTrace();
			}
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
