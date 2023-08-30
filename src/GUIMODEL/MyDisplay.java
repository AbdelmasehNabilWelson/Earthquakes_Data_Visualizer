package GUIMODEL;

import processing.core.PApplet;
import processing.core.PImage;

public class MyDisplay extends PApplet {
	PImage img;
	String URL = "https://as2.ftcdn.net/v2/jpg/05/72/96/15/1000_F_572961522_bZLsZfH1OvvX6nKLi5Yf9QRlzYWo0foe.jpg";
	
	public void setup() {
		size(800, 600);
		background(255);
		stroke(0);
		img = loadImage(URL, "jpg");
	}
	
	public void draw() {
		image(img, 0, 0);
		img.resize(0, HEIGHT);		
		
		int[] color = sunColorSec(second());
		fill(color[0], color[1], color[2]);
		ellipse(width / 4, height / 5, width/ 4, height / 5);
		
	};
	
	public int[] sunColorSec(float seconds) {
		int[] rgb = new int[3];
		float diffFrom30 = Math.abs(30 - seconds);
		float ratio = diffFrom30 / 30;
		rgb[0] = (int)(255 * ratio);
		rgb[1] = (int)(255 * ratio);
		rgb[2] = 0;
		
		return rgb;
	}
}
