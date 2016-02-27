package SourceFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import Preprocess.StopWordsMgr;

/**
 * Delete keywords for commits files and issues files
 * @author Haihong Luo
 */
public class DeleteKeyWordsForCommitFiles {
	private List<String> commitText;
	public DeleteKeyWordsForCommitFiles(String token, String version1, String version2, String location) throws Exception {
		CreateCommitFiles createCommitFiles = new CreateCommitFiles(token, version1, version2, location);
		createCommitFiles.createCommitFiles();
		commitText = createCommitFiles.getCommitText();
	}
	
	public List<String> getCommitText() {
		return commitText;
	}
	
	public void deleteKeyWords() throws IOException {
		for(String str : commitText) {
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
