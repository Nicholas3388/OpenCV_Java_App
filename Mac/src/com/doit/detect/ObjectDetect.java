package com.doit.detect;

import java.awt.Image;

public interface ObjectDetect {
	void detect();
	Image detect(String filePath);
}
