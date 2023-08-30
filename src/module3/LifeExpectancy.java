package module3;

import processing.core.PApplet;

import java.util.*;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.Google.GoogleMapProvider;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;

//Parsing library
import parsing.ParseFeed;

public class LifeExpectancy extends PApplet {

	// You can ignore this.  It's to keep eclipse from generating a warning.
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFLINE, change the value of this variable to true
	private static final boolean offline = true;
	
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	private UnfoldingMap map;
	
	Map<String, Float> lifeExpByCountry;
	
	List<Feature> countries;
	List<Marker> countryMarkers;
	
	public void setup() {
		size(800, 600, OPENGL);
		//map = new UnfoldingMap(this, 50, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
		map = new UnfoldingMap(this, 50, 50, 700, 500, new GoogleMapProvider());
	    map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map);
	    
	    
	    lifeExpByCountry = loadLifeExpectancyFromCSV("G:\\Java Projjects\\COURSE_3\\Week_2\\UCSDUnfoldingMaps\\data\\LifeExpectancyWorldBank.csv");
	    // this code is to create features and markers for countries 
	    countries = GeoJSONReader.loadData(this, "G:\\Java Projjects\\COURSE_3\\Week_2\\UCSDUnfoldingMaps\\data\\countries.geo.json");
	    countryMarkers = MapUtils.createSimpleMarkers(countries);
	    map.addMarkers(countryMarkers);
	    shadeCountries();
	}
	
	public void draw() {
	    background(10);
	    map.draw();
	}
	
	private Map<String, Float> loadLifeExpectancyFromCSV(String fileName) {
		Map<String, Float> lifeExpMap = new HashMap<String, Float>();
		
		String[] rows = loadStrings(fileName);
		for(String row : rows) {
			String[] columns = row.split(",");
			if (columns[5].length() > 4) {
				//System.out.println(columns[5]);
				float value = Float.parseFloat(columns[5]);
				lifeExpMap.put(columns[4], value);				
			}
		}
		return lifeExpMap;
	}
	
	private void shadeCountries() {
		for (Marker marker : countryMarkers) {
			String countryId = marker.getId();
			//System.out.println(countryId);
			if (lifeExpByCountry.containsKey(countryId)) {
				float lifeExp = lifeExpByCountry.get(countryId);
				// this map method maps takes a number(lifeExp) pre-defined in range
				// (40, 90) and map it into its comparable location in a different range of values
				int colorLevel = (int) map(lifeExp, 40, 90, 0, 255); // we casted it to int because color code works only with Integers
				//marker.setColor(color(255-colorLevel, 100,colorLevel));
				// low life expectancy =====> colored bright red (red must be as high as possible near to 255)
				// high life expectancy =====> colored bright blue (blue must be as high as possible and others must be low)
				marker.setColor(color(255-colorLevel, 100,colorLevel));
			} else {
				marker.setColor(color(150,150,150));
			}
		}
	}
}
