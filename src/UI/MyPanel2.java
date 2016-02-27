package UI;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class MyPanel2 extends JPanel {

    public MyPanel2(String result) {
    	/*
        //construct components
    	JTextField jcomp4 = new JTextField (8);

        //adjust size and set layout
        setPreferredSize (new Dimension (400, 200));
        setLayout (null);
        
        jcomp4.setText(result);
        
        //set component bounds (only needed by Absolute Positioning)
        jcomp4.setBounds (5, 5, 100, 50);
        */
    	
        JTextArea jta = new JTextArea(25,50);
        jta.setText(result);
        jta.setEditable(false);
        
        JScrollPane sp = new JScrollPane(jta);
        add (sp);   
    }
}
