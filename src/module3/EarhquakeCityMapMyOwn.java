package module3;

import de.fhpotsdam.unfolding.*;
import de.fhpotsdam.unfolding.data.*;
import de.fhpotsdam.unfolding.geo.*;
import de.fhpotsdam.unfolding.marker.*;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.utils.*;
import processing.core.PApplet;

public class EarhquakeCityMapMyOwn extends PApplet {
	private UnfoldingMap map;
	
	public void setup() {
		size(950, 600, OPENGL);
		map = new UnfoldingMap(this, 200, 50, 700, 500, new  Google.GoogleMapProvider());
		map.zoomToLevel(2);
		MapUtils.createDefaultEventDispatcher(this, map);
		Location valloc = new Location(26.9060999f, 30.8768375f);
		SimplePointMarker val = new SimplePointMarker(valloc);
		map.addMarker(val);
		
		Feature valEq = new PointFeature(valloc);
	}
	
	public void draw() {
		background(10);
		map.draw();
	}
 }
