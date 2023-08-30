package module3;

//Java utilities libraries
import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;

//Processing library
import processing.core.PApplet;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;

//Parsing library
import parsing.ParseFeed;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 * Date: July 17, 2015
 * */
public class EarthquakeCityMap extends PApplet {

	// You can ignore this.  It's to keep eclipse from generating a warning.
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFLINE, change the value of this variable to true
	private static final boolean offline = true;
	//private static final boolean offline = false;
	
	// Less than this threshold is a light earthquake
	private final float THRESHOLD_MODERATE = 5;
	// Less than this threshold is a minor earthquake
	private static final float THRESHOLD_LIGHT = 4;

	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	// The map
	private UnfoldingMap map;
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";

	
	public void setup() {
		size(950, 600, OPENGL);

		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 700, 500, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
			//earthquakesURL = "2.5_week.atom";
		}
		
	    map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map);	
			
	    // The List you will populate with new SimplePointMarkers
	    List<Marker> markers = new ArrayList<Marker>();

	    //Use provided parser to collect properties for each earthquake
	    //PointFeatures have a getLocation method
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	  
	    
	    for (PointFeature eq : earthquakes) {
	    	markers.add(createMarker(eq));
	    }
	    System.out.println(markers.size());
	    
	    map.addMarkers(markers);
	}
		
	private SimplePointMarker createMarker(PointFeature feature) {  
		//System.out.println(feature.getProperties());
		
		// Create a new SimplePointMarker at the location given by the PointFeature
		SimplePointMarker marker = new SimplePointMarker(feature.getLocation());
		
		Object magObj = feature.getProperty("magnitude");
		float mag = Float.parseFloat(magObj.toString());
		
		if (mag >= THRESHOLD_MODERATE) { // > 5
			setPointToRed(marker);
		} else if (mag >= THRESHOLD_LIGHT) {
			setPointToYellow(marker);
		} else { 
			setPointToBlue(marker);
		}
	    
	    return marker;
	}
	
	public void draw() {
	    background(10);
	    map.draw();
	    addKey();
	}

	private void setPointToRed(SimplePointMarker marker) {
		int red = color(255, 0, 0);
		marker.setColor(red);
		marker.setRadius(20);
	}
	
	private void setPointToYellow(SimplePointMarker marker) {
		int yellow = color(255, 255, 0);
		marker.setColor(yellow); // 4-4.9
		marker.setRadius(15);		
	}
	
	private void setPointToBlue(SimplePointMarker marker) {
		int blue = color(0, 0, 255);
		marker.setColor(blue);
		marker.setRadius(10);		
	}
	
	private void addKey() {	
		fill(255);
		rect(25, 50, 150, 250);
		fill(0);
		text("Earthquake Key", 50, 70);
		
		drawCircle(40, 100, 15, 255, 0, 0);
		fill(0);
		text("5.0+ Magnitude", 70, 105);
		
		drawCircle(40, 150, 10, 255, 255, 0);
		fill(0);
		text("4.0+ Magnitude", 70, 155);
		
		drawCircle(40, 200, 5, 0, 0, 255);
		fill(0);
		text("Below 4.0", 70, 205);		
	}
	
	private void drawCircle(float x, float y, int rad, int R, int G, int B) {
		fill(R, G, B);
		ellipse(x, y, rad, rad);
	}
}
