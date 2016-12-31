package com.doit.opencv;

import com.doit.detect.BaseDetect;
import com.doit.common.Constants;

import org.opencv.core.*;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		MyWindow frame = new MyWindow("OpenCV APP", Constants.DEF_FRAME_WIDTH, Constants.DEF_FRAME_HEIGHT);
		
		//Mat m = Mat.eye(3, 3, CvType.CV_8UC1);
		//System.out.println("m = " + m.dump());
	}

}
