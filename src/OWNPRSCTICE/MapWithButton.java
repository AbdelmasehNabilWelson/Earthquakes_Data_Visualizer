package OWNPRSCTICE;

import java.awt.Color;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;

public class MapWithButton extends PApplet {
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	private UnfoldingMap map;
	public void setup() {
		size(800,600,OPENGL);
		map = new UnfoldingMap(this, 50, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
		MapUtils.createDefaultEventDispatcher(this, map);
	}
	
	public void draw() {
		
		background(255, 255, 255);
		mouseReleased();
		map.draw();
		drawRect(100f, 100f, new Color(150, 150, 150));
		drawRect(100f, 130f, new Color(0, 0, 50));
	}
	
	public void drawRect(float x, float y, Color c) {
		fill(c.getRed(), c.getGreen(), c.getBlue());
		rect(x, y, 25, 25);
	}
	
	public void mouseReleased() {
		if (mouseX > 100 && mouseX < 125 && mouseY > 100 && mouseY < 125) {
			background(150, 150, 150);
		}
		
		if (mouseX > 100 && mouseX < 125 && mouseY > 130 && mouseY < 155) {
			background(0, 0, 50);
		}
	}
}