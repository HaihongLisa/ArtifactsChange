package SourceFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

public class LemmatizerForIssueFiles {
	private List<String> issueText;;
	public LemmatizerForIssueFiles(String token, String version1, String version2, String location) throws Exception {
		DeleteKeyWordsForIssueFiles deleteKeyword = new DeleteKeyWordsForIssueFiles(token, version1, version2, location);
		deleteKeyword.deleteKeyWords();
		issueText = deleteKeyword.getIssueText();
	}
	
	public void lemmatizerForIssueFiles() throws IOException {
		for(String str : issueText) {
			String line = null;
			FileReader fileReader = new FileReader(str);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuilder sb = new StringBuilder();
			while((line = bufferedReader.readLine()) != null) {
				if(line.length() == 0) {
					continue;
				}
				
				Document doc = new Document(line);
				for (Sentence sent : doc.sentences()) {
					for(int i = 0; i < sent.lemmas().size() ; i++) {
						sb.append(sent.lemma(i));
						sb.append(" ");
					}
				}
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(str));
			bw.write(sb.toString());
			bw.close();
		}
	}


	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String location = "/Users/luoluo/Desktop/JIRAIssues/";
		LemmatizerForIssueFiles test = new LemmatizerForIssueFiles("CASSANDRA", "2.1.10", "2.1.12",location);
		test.lemmatizerForIssueFiles();
	}
}
