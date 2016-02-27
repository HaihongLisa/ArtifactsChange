package UI;

import java.awt.*;
import java.awt.List;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import javax.swing.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import JIRA.LinkByDate;
import JIRA.LinkByJiraID;
import JIRA.LinkByKeyWord;
import SourceFile.FetchCommits;
import SourceFile.FetchJiraIssues;
 
/**
 * Enable/disable the checkbox by parsing Policies.xml
 * The related policies will be called based on dataset and selected checkbox
 * If mulitple checkbox are selected, it will call each function first then merge the result
 * @author Haihong Luo
 */
public class TextButton extends JFrame{
	//Project name used in JIRA
	private String token;
	//Earlier version
	private String version1;
	//Latest version
	private String version2;
	//Location where the link result should be stored
	private String location;
	//dataSet to scrape the bug information
	private String dataSet;
	private HashMap<String, ArrayList<String>> policyMap;
	private boolean isByJiraID;
	private boolean isByDate;
	private boolean isByKeyWord;
	private boolean isByTopic;
	private ArrayList<String> mergedResult;
	private ArrayList<String> byJIRAIDResult;
	private ArrayList<String> byDateResult;
	private ArrayList<String> byKeyWordResult;
	
	public TextButton(String token, String version1, String version2, String dataSet, HashMap<String, ArrayList<String>> policyMap, String location) {
		this.token = token;
		this.version1 = version1;
		this.version2 = version2;
		this.location = location;
		this.dataSet = dataSet;
		this.policyMap = policyMap;

		setTitle("Policies");
		setSize(500, 500);
		setLocation(500, 300);
		setLayout(new FlowLayout());
	
		JCheckBox chkByJiraID = new JCheckBox("ByJiraID");
	    JCheckBox chkByDate = new JCheckBox("ByDate");
	    JCheckBox chkByKeyWord = new JCheckBox("ByKeyWord");
	    JCheckBox chkByTopic = new JCheckBox("ByTopic");

	    chkByJiraID.setMnemonic(KeyEvent.VK_C);
	    chkByDate.setMnemonic(KeyEvent.VK_M);
	    chkByKeyWord.setMnemonic(KeyEvent.VK_P);
	    chkByTopic.setMnemonic(KeyEvent.VK_O);
	    
	    chkByJiraID.addItemListener(new ItemListener() {
	    	public void itemStateChanged(ItemEvent e) {         
	        	isByJiraID = e.getStateChange() == 1 ?  true : false;
	        }           
	     });

	     chkByDate.addItemListener(new ItemListener() {
	        public void itemStateChanged(ItemEvent e) {             
	        	isByDate = e.getStateChange() == 1 ?  true : false;
	        }           
	     });

	     chkByKeyWord.addItemListener(new ItemListener() {
	        public void itemStateChanged(ItemEvent e) {             
	        	isByKeyWord = e.getStateChange() == 1 ?  true : false;
	        }           
	     });
	      
	     chkByTopic.addItemListener(new ItemListener() {
	        public void itemStateChanged(ItemEvent e) {             
	        	isByTopic = e.getStateChange() == 1 ?  true : false;
	        }           
	     });

	     add(chkByJiraID);
	     add(chkByDate);
	     add(chkByKeyWord); 
	     add(chkByTopic);
	
	     if(policyMap.containsKey(dataSet)) {
	    	 ArrayList<String> policyList = policyMap.get(dataSet);	    	 
	    		 if(!policyList.contains("ByJiraID")) {
	    		    	chkByJiraID.setEnabled(false);
	    		    }
	    		    
	    		    if(!policyList.contains("ByDate")) {
	    		    	chkByDate.setEnabled(false);
	    	        }
	    		    
	    		    if(!policyList.contains("ByKeyWord")) {
	    		    	chkByKeyWord.setEnabled(false);
	    	        }
	    		    
	    		    if(!policyList.contains("ByTopic")) {
	    		    	chkByTopic.setEnabled(false);
	    	       }
	     } else {
		    	chkByJiraID.setEnabled(false);
		    	chkByDate.setEnabled(false);
		    	chkByKeyWord.setEnabled(false);
		    	chkByTopic.setEnabled(false);
	     }
	    
	    JTextArea jta = new JTextArea(4,40);
		jta.setText("Select the policies you want!" + "\n" + "The data will merge(filter) if more than 2 policies are selected!");
		jta.setEditable(false);
		add(jta);
		
        JButton btn1 = new JButton("Confirm");
        
		btn1.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			String ret = "";
			mergedResult = new ArrayList<String>();
			byJIRAIDResult = new ArrayList<String>();
			byDateResult = new ArrayList<String>();
			byKeyWordResult = new ArrayList<String>();
			
			if(dataSet.equals("JIRA")) {
				try {
						 FetchCommits commits = new FetchCommits(token , version1, version2);
						 commits.fillExcelWithCommits();
						 
						 FetchJiraIssues issues = new FetchJiraIssues(token, version1, version2);
						 issues.fillExcelWithJIRA();
						 
						if(chkByJiraID.isSelected()) {
							 LinkByJiraID links = new LinkByJiraID();
							 links.setLinkByJiraID(location);
							 byJIRAIDResult = links.getResultLink(); 
						}
						
						if(chkByDate.isSelected()) {
							 LinkByDate links = new LinkByDate();
							 links.setLinkByDate(location);
							 byDateResult = links.getResultLink();						
						}
			
						if(chkByKeyWord.isSelected()) {
							 LinkByKeyWord links = new LinkByKeyWord();
							 links.setLinkByKeyWord(location);
							 byKeyWordResult = links.getResultLink();
						}
						mergeResult();
						ret = parseResult();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
			}
			setJFrame(ret);
			}
		});	
		
        btn1.setActionCommand("Open");
        add(btn1);
        
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	 }

	 private void setJFrame(String str){
		 JFrame frame = new JFrame ("Links");
		 frame.setLocation(520, 350);
         frame.getContentPane().add (new MyPanel2(str));
         frame.pack();
         frame.setVisible (true);
	 }
	 
	 private void mergeResult() throws FileNotFoundException, IOException, ParseException {
		 if(!byJIRAIDResult.isEmpty()) {
			 mergedResult = byJIRAIDResult;
			 if(!byKeyWordResult.isEmpty()) {
				 merge(mergedResult, byKeyWordResult);
			 }
			 
			 if(!byDateResult.isEmpty()) {
				 merge(mergedResult,byDateResult);
			 }
		 }else if(!byKeyWordResult.isEmpty()) {
			 mergedResult = byKeyWordResult;
			 if(!byJIRAIDResult.isEmpty()) {
				 merge(mergedResult, byJIRAIDResult);
			 }
			 
			 if(!byDateResult.isEmpty()) {
				 merge(mergedResult,byDateResult);
			 }
		 } else if(!byDateResult.isEmpty()) {
			 mergedResult = byDateResult;
			 if(!byJIRAIDResult.isEmpty()) {
				 merge(mergedResult, byJIRAIDResult);
			 }
			 
			 if(!byKeyWordResult.isEmpty()) {
				 merge(mergedResult,byKeyWordResult);
			 }
		 }
	 }
	 
	 /**
	  * Merge the policy result
	  */
	 private void merge(ArrayList<String> policy1, ArrayList<String> policy2) throws FileNotFoundException, IOException, ParseException {
		 for(int i = 0; i < policy1.size(); i++) {
			 JSONParser parser = new JSONParser();
			 JSONArray array1 = (JSONArray)parser.parse(new FileReader(policy1.get(i)));
			 JSONArray array2 = (JSONArray)parser.parse(new FileReader(policy2.get(i)));
			 JSONArray arrayResult = new JSONArray();
			 for(Object object1 : array1) {
				 JSONObject links1 = (JSONObject) object1;
				 String issue1 = (String) links1.get("Issue_ID");
				 JSONArray commits1 = (JSONArray) links1.get("Links");	 
				 for(Object object2 : array2) {
					 JSONObject links2 = (JSONObject)object2;
					 String issue2 = (String) links2.get("Issue_ID");
					 JSONArray commits2 = (JSONArray) links2.get("Links");
					 JSONObject linkResult = new JSONObject();
					 JSONArray commitsResult = new JSONArray();
					 if(issue1.equals(issue2)) {
						 for(Object commit1 : commits1) {
							 JSONObject commitLink1 = (JSONObject) commit1;
							 String commitID1 = commitLink1.get("Commit").toString();
							 for(Object commit2 : commits2) {
								 JSONObject commitLink2 = (JSONObject) commit2;
								 String commitID2 = commitLink2.get("Commit").toString();
								 if(commitID1.equals(commitID2)) {
									 JSONObject objResult = new JSONObject();
									 objResult.put("Commit", commitLink1.get("Commit"));
									 objResult.put("Files", commitLink1.get("Files"));
									 commitsResult.add(objResult);
								 }
							 }
						 }
						 linkResult.put("Links", commitsResult);
						 linkResult.put("Issue_ID", links1.get("Issue_ID"));
						 arrayResult.add(linkResult);
					}
				}
			}
			FileWriter file = new FileWriter(policy1.get(i));
			file.write(arrayResult.toJSONString());
			file.flush();
			file.close();
		}
	}
	 
	 /**
	  * Parse the result file into UI
	  * @return
	  */
	 private String parseResult() {
		 StringBuilder ret = new StringBuilder();
		 for(String file : mergedResult) {
			 ret.append(file);
			 ret.append("\n");
			 ret.append(parseJson(file));
			 ret.append("\n");
			 ret.append("\n");
		 }
		 return ret.toString();
		 
	 }
	 
	 
	public String parseJson(String file) {
		StringBuilder ret = new StringBuilder();
		JSONParser parser = new JSONParser();
		try {
			JSONArray array = (JSONArray)parser.parse(new FileReader(file));
			
			for(Object object : array) {
				JSONObject issue = (JSONObject) object;
				String issueID = (String) issue.get("Issue_ID");
				ret.append(issueID);
				ret.append("\n");
				ret.append("Links");
				ret.append("\n");
				
				JSONArray links = (JSONArray) issue.get("Links");
				for(Object link : links) {
					JSONObject trace = (JSONObject) link;
					String commitID = (String) trace.get("Commit");
					ret.append("    Commit: " + commitID);
					ret.append("\n");
					JSONArray files = (JSONArray) trace.get("Files");
					ret.append("    " + "Files");
					ret.append("\n");
					for(Object o : files) {
						ret.append("          " + o.toString());
						ret.append("\n");
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return ret.toString();
	}
}