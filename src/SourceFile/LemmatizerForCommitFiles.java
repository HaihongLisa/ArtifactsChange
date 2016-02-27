package SourceFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

public class LemmatizerForCommitFiles {
	private List<String> commitText;
	public LemmatizerForCommitFiles(String token, String version1, String version2, String location) throws Exception {
		DeleteKeyWordsForCommitFiles deleteKeyword = new DeleteKeyWordsForCommitFiles(token, version1, version2, location);
		deleteKeyword.deleteKeyWords();
		commitText = deleteKeyword.getCommitText();
	}
	
	public void lemmatizerForCommitFiles() throws IOException {
		for(String str : commitText) {
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
		String location = "/Users/luoluo/Desktop/Commits/";
		LemmatizerForCommitFiles test = new LemmatizerForCommitFiles("CASSANDRA", "2.1.10", "2.1.12",location);
		test.lemmatizerForCommitFiles();
	}

}
