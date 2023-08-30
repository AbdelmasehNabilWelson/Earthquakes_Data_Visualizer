package module4;

import java. awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import parsing.ParseFeed;
import processing.core.PApplet;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 * Date: July 17, 2015
 * */
public class EarthquakeCityMap extends PApplet {
	// You can ignore this.  It's to get rid of eclipse warnings
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFILINE, change the value of this variable to true
	private static final boolean offline = true;
	
	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
	
	// The files containing city names and info and country names and info
	private String cityFile = "city-data.json";
	private String countryFile = "countries.geo.json";
	
	// The map
	private UnfoldingMap map;
	
	// Markers for each city
	private List<Marker> cityMarkers;
	// Markers for each earthquake
	private List<Marker> quakeMarkers;

	// A List of country markers
	private List<Marker> countryMarkers;
	
	public void setup() {		
		// (1) Initializing canvas and map tiles
		size(900, 700, OPENGL);
		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 650, 600, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom";  // The same feed, but saved August 7, 2015
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 650, 600, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
		    //earthquakesURL = "2.5_week.atom";
		}
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// FOR TESTING: Set earthquakesURL to be one of the testing files by uncommenting
		// one of the lines below.  This will work whether you are online or offline
		//earthquakesURL = "test1.atom";
		//earthquakesURL = "test2.atom";
		
		// WHEN TAKING THIS QUIZ: Uncomment the next line
		earthquakesURL = "quiz1.atom";
		
		
		
		// (2) Reading in earthquake data and geometric properties
	    //     STEP 1: load country features and markers
		List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		
		//     STEP 2: read in city data
		List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
		cityMarkers = new ArrayList<Marker>();
		for(Feature city : cities) {
		  cityMarkers.add(new CityMarker(city));
		}
	    
		//     STEP 3: read in earthquake RSS feed
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    quakeMarkers = new ArrayList<Marker>();
	    
	    for(PointFeature feature : earthquakes) {
		  if(isLand(feature)) {
		    quakeMarkers.add(new LandQuakeMarker(feature));
		  } else {
		    quakeMarkers.add(new OceanQuakeMarker(feature));
		  }
	    }
	    
	    printQuakes();
	    map.addMarkers(quakeMarkers);
	    map.addMarkers(cityMarkers);
	}
	
	public void draw() {
		background(0);
		map.draw();
		addKey();
	}
	
	private void addKey() {	
		fill(255, 250, 240);
		rect(25, 50, 150, 250);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Earthquake Key", 50, 75);
		
		drawCityMarker(55, 100);
		
		
		fill(255);
		drawLandQuakeMarker(55, 120);
		fill(0);
		text("Land Quake", 70, 120);
		
		drawOceanQuakeMarker(47.5f, 135);
		fill(0);
		text("Ocean Quake", 70, 140);
		
		text("Size ~ Magnitude", 50, 165);
		drawQuakDepthInTheKey("Shallow", 55, 185, new Color(255, 255, 0));
		drawQuakDepthInTheKey("Intermidate", 55, 205, new Color(0, 0, 255));
		drawQuakDepthInTheKey("Deep", 55, 225, new Color(255, 0, 0));
	}

	private void drawCityMarker(float x, float y) {
		int TRI_SIZE = 7;
		float x1 = x - TRI_SIZE;
		float y1 = y + TRI_SIZE;
		float x2 = x;
		float y2 = y - TRI_SIZE;
		float x3 = x + TRI_SIZE;
		float y3 = y + TRI_SIZE;
		fill(255, 0, 0);
		triangle(x1, y1, x2, y2, x3, y3);	
		text("City Marker", 70, 100);
	}
	
	private void drawLandQuakeMarker(float x, float y) {
		ellipse(x, y, 15, 15); // circle
	}
	
	private void drawOceanQuakeMarker(float x, float y) {
		fill(255);
		rect(x, y, 15, 15);
	}
	
	private void drawQuakDepthInTheKey(String str, float x, float y, Color c) {
		fill(c.getRed(), c.getGreen(), c.getBlue());
		ellipse(x, y, 15, 15);
		fill(0);
		text(str, x + 15, y);		
	}
	
	private boolean isLand(PointFeature earthquake) {
		for (Marker m : countryMarkers) {
			if (isInCountry(earthquake, m)) {
				return true;
			}
		}
		return false;
	}
	
	private void printQuakes() {
		int totalLandQuakes = 0;
		for(Marker cm : countryMarkers) {
			int quake = 0;
			String cmName = (String)cm.getProperty("name");
			for (Marker m : quakeMarkers) {
				EarthquakeMarker em = (EarthquakeMarker)m;
				if (em.isOnLand) {
					String country = (String)m.getProperty("country");
					if (cmName.equals(country)) {
						quake++;
					}
				}
			}
			
			totalLandQuakes += quake;
			if (quake > 0) {
				System.out.println(cmName + ": " + quake);
			}
		}
		
		int oceanQuakes = quakeMarkers.size() - totalLandQuakes;
		System.out.println("OCEAN QUAKES: " + oceanQuakes);
	}
	
	private boolean isInCountry(PointFeature earthquake, Marker country) {
		Location checkLoc = earthquake.getLocation();

		if(country.getClass() == MultiMarker.class) {
			for(Marker marker : ((MultiMarker)country).getMarkers()) {
				if(((AbstractShapeMarker)marker).isInsideByLocation(checkLoc)) {
					earthquake.addProperty("country", country.getProperty("name"));
					return true;
				}
			}
		}
			
		else if(((AbstractShapeMarker)country).isInsideByLocation(checkLoc)) {
			earthquake.addProperty("country", country.getProperty("name"));
			
			return true;
		}
		return false;
	}

}
