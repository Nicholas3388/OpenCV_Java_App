package com.doit.detect;

import java.awt.Image;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import com.doit.common.Utils;

public class OpencvDemo {
	private static OpencvDemo instance = null;
	
	public static OpencvDemo shareInstance() {
		if (instance == null) {
			return new OpencvDemo();
		}
		return instance;
	}
	
	public Image getOriginImg(String filePath) {
		assert(filePath != null);
		Mat img = Highgui.imread(filePath);
		return Utils.toBufferedImage(img);
	}
}
