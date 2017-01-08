package com.doit.opencv;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.doit.common.Constants;

@SuppressWarnings("serial")
public class BaseWindow extends JFrame {
	protected JPanel mainPane;
	
	public BaseWindow(String name, int width, int height) {
		super();		
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setTitle(name);
		setBounds(screenSize.width/2-width/2, screenSize.height/2-height/2, width, height);
	}

}
