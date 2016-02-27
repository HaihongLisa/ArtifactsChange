package Preprocess;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.simple.*;

public class Lemmatizer {
	
	public static void main(String[] args) { 
        // Create a document. No computation is done yet.
        Document doc = new Document("counts thererfore branch stats rulelist emtpy calculate diagnostic element ar starting ars2 rerurn sub conference freq association itemset increments example sup found temperature attribute mechanism variables ars sets looping loops procedure inserts teetotal supported antecedent generates rules ttree realloc loop adding generate pre count check finding constructors support child frequentsets sequential provided rule update generating recursively updates duration ruleinto length ttree2 calculating outputing existing nodes step time called generation item superintendent proceed method reconvert index exist system combinations field contents partial assumed operates outputting recursive local continues iset reverse produce elements stored exists statistics outputs nul data screen manner reference shown describing complement commences combination counting level created tree format sofar purposes note line link frequent insert storage total initiates itemsets ind process requirements processes determine confidence follow command calculates structure minute elemnt processed contained bytes valued determining arguments");
        for (Sentence sent : doc.sentences()) {  // Will iterate over two sentences
            // We're only asking for words -- no need to load any models yet
           // System.out.println("The second word of the sentence '" + sent + "' is " + sent.word(1));
           
        	// When we ask for the lemma, it will load and run the part of speech tagger
            System.out.println("The third lemma of the sentence '" +  "' is " + sent.lemmas());
           
           
           
        }
    }
	
	/**
	 * This method use stanford nlp API to lemmatize the text. It basically replaces a word 
	 * with it's correct dictionary root
	 * @param content         unstructured text from source code files
	 * @return				  lemmatized text	
	 */
	public static String lemmas(String content){
		
		String lemmatizedContent = "";
		
		// Create a document
		Document doc = new Document(content);
		
		for (Sentence sent : doc.sentences()) {
			
			List<String> lemmas = sent.lemmas();
			
			for(String lemma : lemmas){
				
				lemmatizedContent = lemmatizedContent + " " + lemma;
			}
			
		}
		
		return lemmatizedContent;
		
	}

}
