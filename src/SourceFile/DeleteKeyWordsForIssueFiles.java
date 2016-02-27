package SourceFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import Preprocess.StopWordsMgr;

public class DeleteKeyWordsForIssueFiles {
	private List<String> issueText;
	public DeleteKeyWordsForIssueFiles(String token, String version1, String version2, String location) throws Exception {
		CreateJIRAFiles createJiraFiles = new CreateJIRAFiles(token, version1, version2, location);
		createJiraFiles.createJiraFiles();
		issueText = createJiraFiles.getIssueText();
	}
	
	public List<String> getIssueText() {
		return issueText;
	}
	
	public void deleteKeyWords() throws IOException {
		for(String str : issueText) {
			String line = null;
			FileReader fileReader = new FileReader(str);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuilder sb = new StringBuilder();
			while((line = bufferedReader.readLine()) != null) {
				if(line.length() == 0) {
					continue;
				}
				sb.append(StopWordsMgr.processText(line));
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(str));
			bw.write(sb.toString());
			bw.close();
		}
	}
}
