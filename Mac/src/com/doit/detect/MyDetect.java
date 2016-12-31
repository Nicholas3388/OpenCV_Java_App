package com.doit.detect;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.*;

import com.doit.common.Utils;

public class MyDetect extends BaseDetect {
	
	public MyDetect() {
		//System.out.println("My detect construct");
	}
	
	public void detect() {
		
	}
	
	public Image detect(String filePath) {
		// TODO Auto-generated method stub
		System.out.println("My detect");
		this.filePath = filePath;
		Mat img = Highgui.imread(filePath);
		Mat gray = new Mat();
		Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
		Image ret = Utils.toBufferedImage(gray);
		System.out.println(Utils.usedMemory());
		return ret;
	}

}
