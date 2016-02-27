package Preprocess;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;

import edu.stanford.nlp.simple.Sentence;


/**
 * This class loads file containing the stop words or the words that need to be eliminated from the
 * analysis. It checks if input word is a stop word or not
 * @author namitadave
 *
 */
public class StopWordsMgr {
	
	static HashMap<String, Long> javaKeys;
	
	/**
	 * Loads the stopwords file
	 * 
	 * @param s
	 */
	public static void init(String s) {

		javaKeys = new HashMap<String, Long>();

		try {
			File f = new File(s);
			BufferedReader br = new BufferedReader(new FileReader(f));
			String key;
			while ((key = br.readLine()) != null) {
				key = key.toLowerCase(); 
				javaKeys.put(key.trim(), new Long(0));
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks if the given word is a stop word
	 * @param word					input word
	 * @return true/false
	 */
	public static boolean isStopWord(String word) {
		
		init("stopwords.txt");
		
		if(javaKeys.containsKey(word.trim())){
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * Removes stop words from split text
	 */
	public static String processText(String text) {

		String processedText = "";
		StopWordsMgr stopWordsMgr = new StopWordsMgr();

		Sentence sentence = new Sentence(text);
		List<String> wordList = sentence.words();

		for (String word : wordList) {
			if (!stopWordsMgr.isStopWord(word) && !word.contains(" - ") && !word.contains("=") && !word.contains("--"))
				processedText = processedText + " " + word;
		}

		return processedText;
	}

}
