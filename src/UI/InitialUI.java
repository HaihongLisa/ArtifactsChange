package UI;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

/**
 * Initialize the UI
 * @author Haihong Luo
 *
 */
public class InitialUI extends JFrame{
	private HashMap<String, ArrayList<String>> policiesMap;
    public InitialUI() throws Exception
    {
        super("Enter infomation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setTitle("Filter Information");
		setSize(500, 500);
		setLocation(500, 300);
		setLayout(new FlowLayout());
		
		JTextArea jta = new JTextArea(2,40);
		jta.setText("Please Enter versions:");
		jta.setEditable(false);
		add(jta);
		
		JLabel version1Label = new JLabel("Version1");
		final JTextField version1Text = new JTextField(10);
		add(version1Label);
		add(version1Text);

		JLabel version2Label = new JLabel("Version2");
		final JTextField version2Text = new JTextField(10);
		add(version2Label);
		add(version2Text);
		
		JTextArea jta1 = new JTextArea(2,40);
		jta1.setText("Please dataset: such as JIRA or ALM");
		jta1.setEditable(false);
		add(jta1);
		
		JLabel dataSet = new JLabel("DataSet");
		final JTextField dataSetText = new JTextField(10);
		add(dataSet);
		add(dataSetText);
		
		JTextArea jta2 = new JTextArea(2,40);
		jta2.setText("Please enter project name: such as CASSANDRA");
		jta2.setEditable(false);
		add(jta2);
		  
		JLabel projectLabel = new JLabel("Project Name");
		final JTextField projectText = new JTextField(20);
		add(projectLabel);
		add(projectText);
		
		JLabel locationLabel = new JLabel("Location to store Link Json");
		final JTextField locationText = new JTextField(20);
		add(locationLabel);
		add(locationText);
        
        JButton btn = new JButton("Confirm");
        
        btn.addActionListener(new ActionListener() {
 		   @Override
 		   public void actionPerformed(ActionEvent e) {
 			  String cmd = e.getActionCommand();
	            if(cmd.equals("Open"))
	            {
	                dispose();
	                try {
						String token = projectText.getText();
						String version1 = version1Text.getText();
						String version2 = version2Text.getText();
						readXMLFile();
						String location = locationText.getText();
						String dataSet = dataSetText.getText();
						new TextButton(token, version1, version2, dataSet, policiesMap, location);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
	            }
 		   }
 		  });
        
        btn.setActionCommand("Open");
        add(btn);
    }
    
    /**
     * Read Policies.xml file
     */
	public void readXMLFile() {
		policiesMap = new HashMap<String, ArrayList<String>>();
		try {
			
			File xmlFile = new File("Policies.xml");
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbfactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			
			doc.getDocumentElement().normalize();
			//Get list for the dataset
			NodeList dataset = doc.getElementsByTagName("dataset");
			for(int i = 0; i < dataset.getLength(); i++) {
				NodeList list = dataset.item(i).getChildNodes();
				ArrayList<String> lists = new ArrayList<String>();
				for(int j = 1; j < list.getLength(); j++) {
					String rule = list.item(j).getTextContent();
					lists.add(list.item(j).getTextContent());
				}
				String key = list.item(0).getTextContent().trim();
				policiesMap.put(key, lists);
			}		
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
					new InitialUI().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
        });
    }
}