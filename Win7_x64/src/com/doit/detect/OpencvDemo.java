package com.doit.detect;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import com.doit.common.Constants;
import com.doit.common.Utils;
import com.doit.opencv.DebugWindow;

public class OpencvDemo {
	private static OpencvDemo instance = null;
	private static int thinningIter = -1;
	
	public static OpencvDemo shareInstance() {
		if (instance == null) {
			instance = new OpencvDemo();
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

	public Mat getStichImage(Vector<String> fileList) {
		Mat res = new Mat();
		
		return res;
	}
	
	public Image getErodeImage(String filePath) {
		Mat img = Highgui.imread(filePath);
		Mat gray = new Mat();
		Mat binary = new Mat();
		Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.threshold(gray, binary, 90, 255, Imgproc.THRESH_BINARY);
		Imgproc.erode(binary, binary, new Mat());
		return Utils.toBufferedImage(binary);
	}
	
	public Image getDilateImage(String filePath) {
		Mat img = Highgui.imread(filePath);
		Mat gray = new Mat();
		Mat binary = new Mat();
		Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.threshold(gray, binary, 90, 255, Imgproc.THRESH_BINARY);
		Imgproc.dilate(binary, binary, new Mat());
		return Utils.toBufferedImage(binary);
	}
	
	public Image getEdgeImage(String filePath) {
		Mat img = Highgui.imread(filePath);
		Mat gray = new Mat();
		Mat binary = new Mat();
		Mat temp = new Mat();
		Mat dst = new Mat();
		Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.threshold(gray, binary, 120, 255, Imgproc.THRESH_BINARY);
		Imgproc.erode(binary, temp, new Mat());
		Imgproc.dilate(binary, dst, new Mat());
		Core.subtract(dst, temp, gray);
		return Utils.toBufferedImage(gray);
	}
	
	public Image getMorphImage(String filePath) {
		Mat img = Highgui.imread(filePath);
		Mat gray = new Mat();
		Mat binary = new Mat();
		Mat dst = new Mat();
		Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.threshold(gray, binary, 120, 255, Imgproc.THRESH_BINARY);
		Imgproc.morphologyEx(binary, dst, Imgproc.MORPH_OPEN, new Mat());
		//Imgproc.morphologyEx(binary, dst, Imgproc.MORPH_CLOSE, new Mat());
		return Utils.toBufferedImage(dst);
	}
	
	public Mat burrDetect(String filePath) {
		Mat img = Highgui.imread(filePath);
		Mat gray = new Mat();
		Mat binary = new Mat();
		Mat dst = new Mat();
		Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.threshold(gray, binary, 120, 255, Imgproc.THRESH_BINARY);
		Imgproc.erode(binary, binary, new Mat());
		//Imgproc.dilate(binary, binary, new Mat());
		Imgproc.dilate(binary, dst, new Mat());
		return dst;
	}
	
	private Mat reverse(Mat binary) {
		int size = (int) (binary.total() * binary.channels());
		int[] temp = new int[size];
		binary.get(0, 0, temp);
		for (int i = 0; i < binary.rows(); i++) {
			for (int j = 0; j < binary.cols(); j++) {
				int val = temp[j+i*binary.cols()];
				if (val == 255) {
					temp[j+i*binary.cols()] = 0;
				} else {
					temp[j+i*binary.cols()] = 255;
				}
			}
		}
		binary.put(0, 0, temp);
		return binary;
	}
	
	/**
		Thinning algorithm from paper: A fast parallel algorithm for thinning digital patterns
		Target is white.
	*/
	public Mat thinning(Mat src, int iterations, boolean reverse) {
		Mat gray = new Mat();
		Mat binary = new Mat();
		Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.threshold(gray, binary, 120, 255, Imgproc.THRESH_BINARY);
		//Imgproc.erode(binary, binary, new Mat());
		//Imgproc.dilate(binary, binary, new Mat());
		thinningIter = iterations;
		binary.convertTo(binary, CvType.CV_32S);
		Mat rev = new Mat();
		if (reverse) {
			reverse(binary).copyTo(rev);
		} else {
			binary.copyTo(rev);
		}
		int size = (int) (rev.total() * rev.channels());
		int[] temp = new int[size];
		rev.get(0, 0, temp);
		while (true) {
			Vector mFlag = new Vector(); 
			for (int i = 0; i < rev.rows(); i++) {
				for (int j = 0; j < rev.cols(); j++) {
					/**
						p9 p2 p3
						p8 p1 p4
	                	p7 p6 p5
	                 */
					int idx = j+i*rev.cols();
					int p1 = temp[idx];
					if (p1 != 255) {
						continue;
					}
					int p4 = (j == rev.cols()-1) ? 0 : temp[j+1+i*rev.cols()];
					int p8 = (j == 0) ? 0 : temp[j-1+i*rev.cols()];
					int p2 = (i == 0) ? 0 : temp[j+(i-1)*rev.cols()];
					int p3 = (i == 0 || j == rev.cols()-1) ? 0 : temp[j+1+(i-1)*rev.cols()];
					int p9 = (i == 0 || j == 0) ? 0 : temp[j-1+(i-1)*rev.cols()];
					int p6 = (i == rev.rows()-1) ? 0 : temp[j+(i+1)*rev.cols()];
					int p5 = (i == rev.rows()-1 || j == rev.cols()-1) ? 0 : temp[j+1+(i+1)*rev.cols()];
					int p7 = (i == rev.rows()-1 || j == 0) ? 0 : temp[j-1+(i+1)*rev.cols()];
					
					if ((p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9) >= 2 * 255 && (p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9) <= 6 * 255) {
						int ap = 0;  
	                    if (p2 == 0 && p3 == 255) ++ap;  
	                    if (p3 == 0 && p4 == 255) ++ap;  
	                    if (p4 == 0 && p5 == 255) ++ap;  
	                    if (p5 == 0 && p6 == 255) ++ap;  
	                    if (p6 == 0 && p7 == 255) ++ap;  
	                    if (p7 == 0 && p8 == 255) ++ap;  
	                    if (p8 == 0 && p9 == 255) ++ap;  
	                    if (p9 == 0 && p2 == 255) ++ap;  
	  
	                    if (ap == 1 && p2 * p4 * p6 == 0 && p4 * p6 * p8 == 0) {  
	                        mFlag.add(new Integer(idx));
	                    }
					}
				}
			}
			
			for (int i = 0; i < mFlag.size(); i++) {
				Integer p = (Integer) mFlag.get(i);
				temp[p] = 0;		// remove pixel
			}
			
			rev.put(0, 0, temp);
			
			if (mFlag.isEmpty()) {
				break;								// algorithm complete
			} else {
				mFlag.clear();						// next iteration
			}
			
			for (int i = 0; i < rev.rows(); i++) {
				for (int j = 0; j < rev.cols(); j++) {
					/**
						p9 p2 p3
						p8 p1 p4
	                	p7 p6 p5
	                 */
					int idx = j+i*rev.cols();
					int p1 = temp[idx];
					if (p1 != 255) {
						continue;
					}
					int p4 = (j == rev.cols()-1) ? 0 : temp[j+1+i*rev.cols()];
					int p8 = (j == 0) ? 0 : temp[j-1+i*rev.cols()];
					int p2 = (i == 0) ? 0 : temp[j+(i-1)*rev.cols()];
					int p3 = (i == 0 || j == rev.cols()-1) ? 0 : temp[j+1+(i-1)*rev.cols()];
					int p9 = (i == 0 || j == 0) ? 0 : temp[j-1+(i-1)*rev.cols()];
					int p6 = (i == rev.rows()-1) ? 0 : temp[j+(i+1)*rev.cols()];
					int p5 = (i == rev.rows()-1 || j == rev.cols()-1) ? 0 : temp[j+1+(i+1)*rev.cols()];
					int p7 = (i == rev.rows()-1 || j == 0) ? 0 : temp[j-1+(i+1)*rev.cols()];
					
					if ((p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9) >= 2 * 255 && (p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9) <= 6 * 255) {
						int ap = 0;  
	                    if (p2 == 0 && p3 == 255) ++ap;
	                    if (p3 == 0 && p4 == 255) ++ap;  
	                    if (p4 == 0 && p5 == 255) ++ap;  
	                    if (p5 == 0 && p6 == 255) ++ap;  
	                    if (p6 == 0 && p7 == 255) ++ap;  
	                    if (p7 == 0 && p8 == 255) ++ap;  
	                    if (p8 == 0 && p9 == 255) ++ap;  
	                    if (p9 == 0 && p2 == 255) ++ap;  
	  
	                    if (ap == 1 && p2 * p4 * p6 == 0 && p4 * p6 * p8 == 0) {  
	                    	mFlag.add(new Integer(idx));
	                    }
					}
				}
			}
			
			for (int i = 0; i < mFlag.size(); i++) {
				Integer p = (Integer) mFlag.get(i);
				temp[p] = 0;		// remove pixel
			}
			
			rev.put(0, 0, temp);
			
			if (mFlag.isEmpty()) {
				break;								// algorithm complete
			} else {
				mFlag.clear();						// next iteration
			}
			
			thinningIter--;
			if (iterations != Constants.MAX_THINNING_ITER && thinningIter <= 0) {
				break;
			}
		}
		
		return rev;
	}
	
	public void match(Mat scene,Mat object) {
		FeatureDetector detector = FeatureDetector.create(FeatureDetector.SURF);
        DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        // DETECTION

        // first image
        Mat descriptors_scene= new Mat();
        MatOfKeyPoint keypoints_scene= new MatOfKeyPoint();
        detector.detect(scene, keypoints_scene);
        descriptor.compute(scene, keypoints_scene, descriptors_scene);

        // second image
        Mat descriptors_object= new Mat();
        MatOfKeyPoint keypoints_object = new MatOfKeyPoint();
        detector.detect(object, keypoints_object);
        descriptor.compute(object, keypoints_object,descriptors_object);
        
        // MATCHING
		ArrayList<MatOfDMatch> matches = new ArrayList<MatOfDMatch>();
		matcher.knnMatch(descriptors_object, descriptors_scene,matches, 5);
        
        // ratio test
        LinkedList<DMatch> good_matches = new LinkedList<DMatch>();
        for (Iterator<MatOfDMatch> iterator = matches.iterator(); iterator.hasNext(); ) {
            MatOfDMatch matOfDMatch = (MatOfDMatch) iterator.next();
            if (matOfDMatch.toArray()[0].distance / matOfDMatch.toArray()[1].distance < 0.9) {
                good_matches.add(matOfDMatch.toArray()[0]);
            }
        }
        
        // get keypoint coordinates of good matches to find homography and remove outliers using ransac
        ArrayList<Point> pts_object = new ArrayList<Point>();
        ArrayList<Point> pts_scene = new ArrayList<Point>();
        for (int i = 0; i<good_matches.size(); i++) {
        	pts_object.add(keypoints_object.toList().get(good_matches.get(i).queryIdx).pt);
            pts_scene.add(keypoints_scene.toList().get(good_matches.get(i).trainIdx).pt);
        }
        
        // convertion of data types - there is maybe a more beautiful way
        Mat outputMask = new Mat();
        MatOfPoint2f pts_objectMat = new MatOfPoint2f();
        pts_objectMat.fromList(pts_object);
        MatOfPoint2f pts_sceneMat = new MatOfPoint2f();
        pts_sceneMat.fromList(pts_scene);
        
        Mat Homog = Calib3d.findHomography(pts_objectMat, pts_sceneMat, Calib3d.RANSAC, 10, outputMask);
        Mat resultMat=new Mat(new Size(object.cols(),object.rows()),object.type());   
        //Imgproc.warpPerspective(object, object, Homog, resultMat.size());
        
        // outputMask contains zeros and ones indicating which matches are filtered
        LinkedList<DMatch> better_matches = new LinkedList<DMatch>();
        for (int i = 0; i < good_matches.size(); i++) {
        //  System.out.println(outputMask.get(i, 0)[0]);
            if (outputMask.get(i, 0)[0] != 0.0) {
                better_matches.add(good_matches.get(i));
            }
        }
        
        // DRAWING OUTPUT
        Mat outputImg = new Mat();
        // this will draw all matches, works fine
        MatOfDMatch better_matches_mat = new MatOfDMatch();
        better_matches_mat.fromList(better_matches);
        //System.out.println(better_matches_mat.toString());
        Features2d.drawMatches(object, keypoints_object, scene, keypoints_scene, better_matches_mat, outputImg);
        Highgui.imwrite("D:\\w\\workspace\\JavaOpenCV\\img\\match_result.jpg", outputImg);
        
		//Mat out = new Mat();
		//Features2d.drawKeypoints(img1, mkp1, out);
		//Highgui.imwrite("D:\\w\\workspace\\JavaOpenCV\\img\\keyPoint.bmp", out);
	}
}
