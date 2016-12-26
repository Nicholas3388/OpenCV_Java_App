package com.doit.detect;

public class AlgorithmFactory {
	public static BaseDetect create(AlgorithmType which) {
		switch(which) {
			case MY_DETECT:
			{
				return new MyDetect();
			}
			
			case MY_DETECT_PRO:
			{
				return new MyDetectPro();
			}
		}
		
		return new MyDetect();
	}
}
