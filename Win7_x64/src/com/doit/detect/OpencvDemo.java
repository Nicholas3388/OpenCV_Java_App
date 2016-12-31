package com.doit.detect;

import java.awt.Image;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

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
	
	public Image getGrayImg(String filePath) {
		Mat img = Highgui.imread(filePath);
		Mat gray = new Mat();
		Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
		return Utils.toBufferedImage(gray);
	}
	
	public Image getCannyImg(String filePath) {
		Mat img = Highgui.imread(filePath);
		Mat gray = new Mat();
		Mat res = new Mat();
		Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.Canny(img, res, 45, 170);
		return Utils.toBufferedImage(res);
	}
	
	public Image getBinaryImg(String filePath) {
		Mat img = Highgui.imread(filePath);
		Mat gray = new Mat();
		Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
		Mat res = new Mat();
		Imgproc.threshold(gray, res, 90, 255, Imgproc.THRESH_BINARY);
		return Utils.toBufferedImage(res);
	}
}
