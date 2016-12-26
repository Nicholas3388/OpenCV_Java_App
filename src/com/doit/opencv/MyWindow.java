package com.doit.opencv;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import com.doit.common.*;
import com.doit.detect.AlgorithmFactory;
import com.doit.detect.AlgorithmType;
import com.doit.detect.BaseDetect;

@SuppressWarnings("serial")
public class MyWindow extends BaseWindow {	
	protected JLabel imageBox;
	protected JLabel originBox;
	
	public MyWindow(String name, int width, int height) {
		super(name, width, height);
		setResizable(false);
		
		windowLayout();
		setVisible(true);
	}
	
	private void windowLayout() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.setToolTipText("File");
		JMenu aboutMenu = new JMenu("About");
		aboutMenu.setToolTipText("About");
		
		JMenuItem openItem = new JMenuItem("Open");
		fileMenu.add(openItem);
		fileMenu.addSeparator();
		JMenuItem quitItem = new JMenuItem("Quit");
		fileMenu.add(quitItem);
		
		JMenuItem verItem = new JMenuItem("Version");
		aboutMenu.add(verItem);
		JMenuItem doitItem = new JMenuItem("DOIT");
		aboutMenu.add(doitItem);
		
		menuBar.add(fileMenu);
		menuBar.add(aboutMenu);
		setJMenuBar(menuBar);
		
		openItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		});
		
		quitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
		
		verItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Utils.messageBox(null, "Version", Constants.VERSION);
			}
		});
		
		doitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		});
		
		mainPane = new JPanel(new CardLayout());
		mainPane.setBounds(0, 0, Constants.DEF_FRAME_WIDTH, Constants.DEF_FRAME_HEIGHT-Constants.DEF_STATUS_BAR_HEIGHT*4);
		getContentPane().add(mainPane, BorderLayout.CENTER);
		
		JPanel picPane = new JPanel();
		picPane.setBackground(Color.BLUE);
		picPane.setSize(640, 320);
		mainPane.add(picPane, "picPane");
		
		imageBox = new JLabel("Test");
		//add(imageBox, BorderLayout.CENTER);
		imageBox.setHorizontalAlignment(SwingConstants.CENTER);
		picPane.add(imageBox);
		
		originBox = new JLabel("origin");
		picPane.add(originBox);
		
		// status bar
		/*JPanel statusPane = new JPanel();
		JToolBar toolBar = new JToolBar();
		toolBar.add(new JLabel("state"));
		//toolBar.setFloatable(false);
		toolBar.setSize(Constants.DEF_FRAME_WIDTH, Constants.DEF_STATUS_BAR_HEIGHT);
		statusPane.add(toolBar, BorderLayout.NORTH);
		getContentPane().add(statusPane, BorderLayout.SOUTH);*/
		
		JPanel bottomPane = new JPanel();
		bottomPane.setLayout(new BorderLayout());
		JButton detectBtn = new JButton("Detect");
		bottomPane.add(detectBtn);
		getContentPane().add(bottomPane, BorderLayout.SOUTH);
		
		detectBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				BaseDetect method = AlgorithmFactory.create(AlgorithmType.MY_DETECT);
				Image img = method.detect("/Users/apple/Documents/workspace/OpenCV/img/iTunesArtwork.png");
				imageBox.setIcon(new ImageIcon(img));
				System.out.println("Detect start");
			}
			
		});
	}
}
