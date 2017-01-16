package com.doit.calibrate;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.TermCriteria;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Size;
import org.opencv.core.CvType;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.calib3d.Calib3d;

public class CamCalibrate {
	private static CamCalibrate instance = null;
	private ArrayList<Mat> objectPoints = new ArrayList<Mat>();
	private ArrayList<Mat> imagePoints = new ArrayList<Mat>();
	private boolean mustInitUndistort = true;
	private Mat cameraMatrix = Mat.eye(3, 3, CvType.CV_64F);
	private Mat disCoeffs = Mat.zeros(8, 1, CvType.CV_64F);
	private int mFlags = Calib3d.CALIB_FIX_PRINCIPAL_POINT + 
            Calib3d.CALIB_ZERO_TANGENT_DIST + 
            Calib3d.CALIB_FIX_ASPECT_RATIO + 
            Calib3d.CALIB_FIX_K4 + 
            Calib3d.CALIB_FIX_K5;
	private Mat map1 = new Mat();
	private Mat map2 = new Mat();
	
	static public CamCalibrate shareInstance() {
		if (instance == null) {
			instance = new CamCalibrate();
		}
		return instance;
	}
	
	private void addPoints(MatOfPoint2f imageCorners, MatOfPoint3f objectCorners) {
		imagePoints.add(imageCorners);
		objectPoints.add(objectCorners);
	}
	
	public int addChessboardPoints(Vector<String> fileList, Size boardSize) {
		MatOfPoint2f imageCorners = new MatOfPoint2f();
		MatOfPoint3f objectCorners = new MatOfPoint3f();
		double squareSize = 1;
        Point3[] vp = new Point3[(int) (boardSize.height * boardSize.width)];
        
        int cnt = 0;
		for (int i = 0; i < boardSize.height; i++) {
			for (int j = 0; j < boardSize.width; j++) {
				vp[cnt] = new Point3(j * squareSize, i * squareSize, 0.0d);
				cnt++;
			}
		}
		objectCorners.fromArray(vp);
		
		int success = 0;
		Mat gray = new Mat();
		for (int i = 0; i < fileList.size(); i++) {
			Mat image = Highgui.imread(fileList.elementAt(i));
			gray.empty();
			Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
			boolean found = Calib3d.findChessboardCorners(gray, boardSize, imageCorners);
			Imgproc.cornerSubPix(
					gray, 
					imageCorners, 
					new Size(11, 11), 
					new Size(-1, -1), 
					new TermCriteria(TermCriteria.MAX_ITER + TermCriteria.EPS, 30, 0.1));
			
			if (imageCorners.total() == boardSize.area()) {
				addPoints(imageCorners, objectCorners);
				success++;
			}
			
			Calib3d.drawChessboardCorners(image, boardSize, imageCorners, found);
			//Highgui.imwrite("D:\\w\\workspace\\JavaOpenCV\\img\\res" + i + ".jpg", image);
		}
		
		return 0;
	}
	
	public double calibrate(Size imageSize) {
		mustInitUndistort = true;
		ArrayList<Mat> rvecs = new ArrayList<Mat>();
		ArrayList<Mat> tvecs = new ArrayList<Mat>();
		return Calib3d.calibrateCamera(objectPoints, imagePoints, imageSize, cameraMatrix, 
				disCoeffs, rvecs, tvecs, mFlags);
	}
	
	public Mat remap(Mat image) {
		Mat undistorted = new Mat();
		if (mustInitUndistort) {
			Imgproc.initUndistortRectifyMap(cameraMatrix, disCoeffs, new Mat(), new Mat(), 
					image.size(), CvType.CV_32FC1, map1, map2);
			mustInitUndistort = false;
		}
		
		Imgproc.remap(image, undistorted, map1, map2, Imgproc.INTER_LINEAR);
		return undistorted;
	}
	
	public Mat getCameraMatrix() {
		return cameraMatrix;
	}
	
	public Mat getDistCoeffs() {
		return disCoeffs;
	}
	
}
