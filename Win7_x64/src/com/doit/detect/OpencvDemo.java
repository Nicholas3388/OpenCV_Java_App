package com.doit.detect;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import com.doit.common.Utils;
import com.doit.opencv.DebugWindow;

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
	
	public Image getBinaryImg2(String filePath) {
		Mat img = Highgui.imread(filePath);
		img.convertTo(img, CvType.CV_32S);
		int channels = img.channels();
		int size = (int) (img.total() * channels);
		int[] pix = new int[size];
		img.get(0, 0, pix);
		for (int i=0; i<img.rows(); i++) {
			for (int j=0; j<img.cols(); j++) {
				int[] dat = {pix[(j+i*img.cols())*3]/2, pix[(j+i*img.cols())*3+1]/2, pix[(j+i*img.cols())*3+2]/2};
				img.put(i, j, dat);
			}
		}
		img.convertTo(img, CvType.CV_8UC1);

		return Utils.toBufferedImage(img);
	}
	
	public Image findCentroid(Mat img) {
		Mat gray = new Mat();
		Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
		Mat res = new Mat();
		Imgproc.threshold(gray, res, 90, 255, Imgproc.THRESH_BINARY);
		int i, j;
		int x0 = 0, y0 = 0, sum = 0;
		Point center = new Point();
		
		long startTime = System.currentTimeMillis();
		res.convertTo(res, CvType.CV_32S);
		int channels = res.channels();
		int size = (int) (res.total() * channels);
		int[] temp = new int[size];
		res.get(0, 0, temp);
		for (i = 0; i < res.rows(); i++) {
			for (j = 0; j < res.cols(); j++) {
				int val = temp[j+i*res.cols()];
				if (val > 0) {
					x0 += i;
					y0 += j;
					sum++;
				}
			}
		}
		center.x = x0 / sum;
		center.y = y0 / sum;
		long duration = System.currentTimeMillis() - startTime;
		DebugWindow.shareInstance().println("cost time: " + duration + " ms");
		
		Core.circle(img, center, 3, new Scalar(0,0,255));
		
		return Utils.toBufferedImage(img);
	}
}
