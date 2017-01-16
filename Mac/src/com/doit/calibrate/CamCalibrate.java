package com.doit.calibrate;

import java.util.Vector;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class CamCalibrate {
	private Vector<Vector<Point3>> objectPoints;
	private Vector<Vector<Point>> imagePoints;
	
	private void addPoints(Vector<Point> imageCorners, Vector<Point3>objectCorners) {
		imagePoints.addElement(imageCorners);
		objectPoints.addElement(objectCorners);
	}
	
	public int addChessboardPoints(Vector<String> fileList, Size boardSize) {
		Vector<Point> imageCorners = new Vector<Point>();
		Vector<Point3> objectCorners = new Vector<Point3>();
		
		for (int i = 0; i < boardSize.height; i++) {
			for (int j = 0; j < boardSize.width; j++) {
				objectCorners.addElement(new Point3(i, j, 0));
			}
		}
		
		int success = 0;
		for (int i = 0; i < fileList.size(); i++) {
			Mat image = Highgui.imread(fileList.elementAt(i));
			//boolean found = findChessboardCorners();
		}
		
		return 0;
	}
}
