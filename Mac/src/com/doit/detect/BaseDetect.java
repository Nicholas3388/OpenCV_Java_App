package com.doit.detect;

import java.awt.Image;

public class BaseDetect implements ObjectDetect {
	protected String filePath;
	
	public void detect() {
		// TODO Auto-generated method stub
	}
	
	public Image detect(String filePath) {
		this.filePath = filePath;
		return null;
	}
}
