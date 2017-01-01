package com.doit.detect;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
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
		Mat temp = new Mat();
		Imgproc.pyrDown(gray, temp);
		Imgproc.pyrUp(temp, gray);
		Imgproc.Canny(gray, res, 45, 170);
		return Utils.toBufferedImage(res);
	}
	
	public Mat getHoughCircles(Mat src) {
		Mat gray = new Mat();
		Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
		Mat temp = new Mat();
		Imgproc.pyrDown(gray, temp);
		Imgproc.pyrUp(temp, gray);
		Mat circles = new Mat();
		Imgproc.HoughCircles(gray, circles, Imgproc.CV_HOUGH_GRADIENT, 2.0, gray.rows()/8, 200, 100, 0, 0);
		return circles;
	}
	
	public Image getHoughImg(String filePath) {
		Mat img = Highgui.imread(filePath);
		Mat circles = getHoughCircles(img);
		for (int i=0; i<circles.cols(); i++) {
			double vCircle[] = circles.get(0, i);
			if (vCircle == null) {
				break;
			}
			Point center = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
			int radius = (int)Math.round(vCircle[2]);
			Core.circle(img, center, radius, new Scalar(0, 0, 255), 2);
			DebugWindow.shareInstance().println("x=" + center.x + " y=" + center.y + " r=" + radius);
		}
		return Utils.toBufferedImage(img);
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
				// lower the light
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
		DebugWindow.shareInstance().println("center: x=" + center.x + " y=" + center.y);
		
		Core.circle(img, center, 3, new Scalar(0,0,255));
		
		// get radius
		ArrayList<Point> vec = new ArrayList<Point>();
		int startX = (int)(center.x-1);
		int startY = (int)(center.y-1);
		double[] temp2, temp3, temp4, temp5;
		int count2 = 0, count3 = 0, count4 = 0, count5 = 0;
		for (i = startX; (i >= 0 && startY-(startX-i) >= 0 && startY+(startX-i) <= img.rows()); i--) {
			temp2 = res.get(i, startY-(startX-i));
			if ((int)temp2[0] == 0) {
				count2++;
				if (count2 > 2) {
					vec.add(new Point(i-2, startY-(startX-i)-2));
				}
			} else {
				count2 = 0;
			}
			
			temp3 = res.get(i, startY+(startX-i));
			if ((int)temp3[0] == 0) {
				count3++;
				if (count3 > 2) {
					vec.add(new Point(i-2, startY+(startX-i)-2));
				}
			} else {
				count3 = 0;
			}
		}
		
		startX = (int)(center.x+1);
		startY = (int)(center.y+1);
		for (i = startX; (i < img.cols() && startY+(i-startX) <= img.rows() && startY-(i-startX) >= 0); i++) {
			temp4 = res.get(i, startY-(i-startX));
			if ((int)temp4[0] == 0) {
				count4++;
				if (count4 > 2) {
					vec.add(new Point(i-2, startY-(i-startX)-2));
				}
			} else {
				count4 = 0;
			}
			
			temp5 = res.get(i, startY+(i-startX));
			if ((int)temp5[0] == 0) {
				count5++;
				if (count5 > 2) {
					vec.add(new Point(i-2, startY+(i-startX)-2));
				}
			} else {
				count5 = 0;
			}
		}
		
		/*Mat circles = getHoughCircles(img);
		for (i=0; i<circles.cols(); i++) {
			double vCircle[] = circles.get(0, i);
			if (vCircle == null) {
				break;
			}
			center = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
			int radius = (int)Math.round(vCircle[2]);
			Core.circle(img, center, radius, new Scalar(0, 0, 255), 2);
			DebugWindow.shareInstance().println("x=" + center.x + " y=" + center.y + " r=" + radius);
		}*/
		
		return Utils.toBufferedImage(img);
	}

}
