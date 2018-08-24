package gui;

import java.awt.Toolkit;

public class ScreenConverter {
	// converts relative pixel values to absolute ones based on screen resolution
	
	public static int getAbsoluteHeight(int rel) {
		if (rel > 0) {
			return (int) Math.round(Toolkit.getDefaultToolkit().getScreenSize().getHeight() * rel / 1050);
		} else {
			return 0;
		}
	}
	
	public static int getAbsoluteWidth(int rel) {
		if (rel > 0) {
			return (int) Math.round(Toolkit.getDefaultToolkit().getScreenSize().getWidth() * rel / 1680);
		} else {
			return 0;
		}
		
	}
	
	public static int getAbsoluteHeight(float rel) {
		if (rel > 0.0d) {
			return (int) Math.round(Toolkit.getDefaultToolkit().getScreenSize().getHeight() * rel);
		} else {
			return 0;
		}
	}
	
	public static int getAbsoluteWidth(float rel) {
		if (rel > 0.0d) {
			return (int) Math.round(Toolkit.getDefaultToolkit().getScreenSize().getWidth() * rel);
		} else {
			return 0;
		}		
	}
	
	// developer function
	public static float convertToRelativeHeight(int abs) {
		if (abs > 0) {
			return abs / (float) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		} else {
			return 0.0f;
		}
	}
	
	// developer function
	public static float convertToRelativeWidth(int abs) {
		if (abs > 0) {
			return abs / (float) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		} else {
			return 0.0f;
		}
	}
}
