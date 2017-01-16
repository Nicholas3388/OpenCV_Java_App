package com.doit.opencv;

import java.awt.Color;
import javax.swing.JEditorPane;

import com.doit.common.Constants;

@SuppressWarnings("serial")
public class DebugWindow extends BaseWindow {
	private static DebugWindow instance = null;
	private JEditorPane editorPane;
	
	public static DebugWindow shareInstance() {
		if (instance == null) {
			instance = new DebugWindow("Debug Window", Constants.DEF_DBG_FRAME_WIDTH, Constants.DEF_DBG_FRAME_HEIGHT);
		}
		return instance;
	}
	
	public DebugWindow(String name, int width, int height) {
		super(name, width, height);
		// TODO Auto-generated constructor stub
		setBounds(10, 10, width, height);
		setResizable(false);
		
		windowLayout();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setVisible(true);
	}

	private void windowLayout() {
		editorPane = new JEditorPane();
		editorPane.setBackground(Color.WHITE);
		getContentPane().add(editorPane);
	}
	
	public void println(String str) {
		if (!Config.DEBUG_ENABLE) { return; }
		String content = editorPane.getText().toString();
		editorPane.setText(content + str + "\r\n");
	}
	
	public void println(long num) {
		if (!Config.DEBUG_ENABLE) { return; }
		String content = editorPane.getText().toString();
		editorPane.setText(content + String.valueOf(num) + "\r\n");
	}
}
