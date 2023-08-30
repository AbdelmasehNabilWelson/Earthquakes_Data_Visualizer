package module5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import module5.EarthquakeMarker;
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
	
	private UnfoldingMap map;
	
	private List<Marker> cityMarkers;
	
	private List<Marker> quakeMarkers;

	private List<Marker> countryMarkers;
	
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	private List<Marker> markersWithinCity = new ArrayList<Marker>();;
	
	public void setup() {
		  println("Processing version: " + PApplet.javaVersion);
		// (1) Initializing canvas and map tiles
		size(900, 700, OPENGL);
		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 650, 600);
		    earthquakesURL = "2.5_week.atom";  // The same feed, but saved August 7, 2015
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 650, 600, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
		    //earthquakesURL = "2.5_week.atom";
		}
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// (2) Reading in earthquake data and geometric properties
		List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		
		//     STEP 2: read in city data
		List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
		cityMarkers = new ArrayList<Marker>();
		for(Feature city : cities) {
		  cityMarkers.add(new CityMarker(city));
		}
	    
		// Uncomment this line to take the quiz
		earthquakesURL = "quiz2.atom";		
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    quakeMarkers = new ArrayList<Marker>();
	    
	    for(PointFeature feature : earthquakes) {
		  if(isLand(feature)) {
		    quakeMarkers.add(new LandQuakeMarker(feature));
		  }
		  else {
		    quakeMarkers.add(new OceanQuakeMarker(feature));
		  }
	    }
	    printQuakes();
	    
	    map.addMarkers(quakeMarkers);
	    map.addMarkers(cityMarkers);
	    
	    sortAndPrint(5);
	    sortAndPrint(20);
	    	    
	}  // End setup
	
	public void draw() {
		background(0);
		map.draw();
		addKey();
	}
	
	private void sortAndPrint(int numToPrint) {
		EarthquakeMarker[] quakesToSort = quakeMarkers.toArray(new EarthquakeMarker[0]);
		
		if (numToPrint > quakesToSort.length) numToPrint = quakesToSort.length; 
		
		Arrays.sort(quakesToSort, Comparator.comparing(EarthquakeMarker :: getMagnitude).reversed());
		System.out.println("Sort and Print for call numToPrint = " + numToPrint);
		//Arrays.sort(quakes);
		for(int i = 0; i < numToPrint; ++i) {
			System.out.println(quakesToSort[i].getTitle());
		}
		System.out.println();
	}	
	
	
	@Override // even handler is called automatically when mouse moved
	public void mouseMoved() {
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		
		}
		selectMarkerIfHover(quakeMarkers);
		selectMarkerIfHover(cityMarkers);
	}
	
	private void selectMarkerIfHover(List<Marker> markers) {
		if (lastSelected != null) {
			return; // to guarantee that two markers will never be selected
		}
		
		for(Marker m : markers) {
			CommonMarker cm = (CommonMarker) m;
			if (cm.isInside(map, mouseX, mouseY)) {
				lastSelected = cm;
				cm.setSelected(true);
				return;
			}
		}
	}
	
	@Override
	public void mouseClicked() {
		lastClicked = whichMarkerIsClicked();
		if (lastClicked == null) {
			unhideMarkers();
			markersWithinCity.clear();
		} else {
			lastClicked.setClicked(true);
			showOnlyClickedMarker();
			
			if (lastClicked instanceof EarthquakeMarker) {
				
				double threatCirc = ((EarthquakeMarker) lastClicked).threatCircle();
				showCitiesThatAffectedByEarthQuake(((EarthquakeMarker) lastClicked));
				
			} else if (lastClicked instanceof CityMarker) {
				showEarhQuakContCityInThreateCircle(((CityMarker) lastClicked));
				
				if (markersWithinCity.size() != 0) {
					printTheEarthquakesThatAffectACity();
				}
			}
		}
	}
	
	private CommonMarker whichMarkerIsClicked() {
		CommonMarker m = null;
		if (lastClicked == null) { // to guarantee that only one marker is clicked
			for(Marker marker : quakeMarkers) {
				if (marker.isSelected()) {
					m = (CommonMarker) marker;
					m.setClicked(true);
				}
			}
				
			for(Marker marker : cityMarkers) {
				if (marker.isSelected()) {
					m = (CommonMarker) marker;
					m.setClicked(true);
				}
			}
		}
		return m;
	}
	
	
	private void unhideMarkers() {
		for(Marker marker : quakeMarkers) {
			((CommonMarker) marker).setClicked(false);
			marker.setHidden(false);
		}
			
		for(Marker marker : cityMarkers) {
			((CommonMarker) marker).setClicked(false);
			marker.setHidden(false);
		}
	}
	
	private void showOnlyClickedMarker() {
		for(Marker marker : quakeMarkers) {
			if (((CommonMarker) marker).getClicked()) {
				marker.setHidden(false);
			} else {
				marker.setHidden(true);
			}
		}
			
		for(Marker marker : cityMarkers) {
			if (((CommonMarker) marker).getClicked()) {
				marker.setHidden(false);
			} else {
				marker.setHidden(true);
			}
		}
	}
	
	public void showCitiesThatAffectedByEarthQuake(EarthquakeMarker m) {
		for(Marker city : cityMarkers) {
			double distance = m.getDistanceTo(city.getLocation());
			if (distance <= m.threatCircle()) {
				((CommonMarker) city).setHidden(false);
			} else {
				((CommonMarker) city).setHidden(true);
			}
		}
		
	}
	
	public void showEarhQuakContCityInThreateCircle(CityMarker city) {
		for(Marker m : quakeMarkers) {
			if (((EarthquakeMarker) m).threatCircle() >= city.getDistanceTo(m.getLocation())) {
				m.setHidden(false);
				markersWithinCity.add(m);
			} else {
				m.setHidden(true);
				
			}
		}
	}
	
	private void printTheEarthquakesThatAffectACity() {
		System.out.println("Start Print the Earthquakes");
		if (markersWithinCity.size() != 0) {
			for(Marker m : markersWithinCity) {
				System.out.println(((EarthquakeMarker) m).getTitle());
			}
		}
		System.out.println("End Print the Earthquakes");		
	}
	
	// helper method to draw key in GUI
	private void addKey() {	
		fill(255, 250, 240);
		
		int xbase = 25;
		int ybase = 50;
		
		rect(xbase, ybase, 150, 250);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Earthquake Key", xbase+25, ybase+25);
		
		fill(150, 30, 30);
		int tri_xbase = xbase + 35;
		int tri_ybase = ybase + 50;
		triangle(tri_xbase, tri_ybase-CityMarker.TRI_SIZE, tri_xbase-CityMarker.TRI_SIZE, 
				tri_ybase+CityMarker.TRI_SIZE, tri_xbase+CityMarker.TRI_SIZE, 
				tri_ybase+CityMarker.TRI_SIZE);

		fill(0, 0, 0);
		textAlign(LEFT, CENTER);
		text("City Marker", tri_xbase + 15, tri_ybase);
		
		text("Land Quake", xbase+50, ybase+70);
		text("Ocean Quake", xbase+50, ybase+90);
		text("Size ~ Magnitude", xbase+25, ybase+110);
		
		fill(255, 255, 255);
		ellipse(xbase+35, ybase+70, 10, 10);
		rect(xbase+35-5, ybase+90-5, 10, 10);
		
		fill(color(255, 255, 0));
		ellipse(xbase+35, ybase+140, 12, 12);
		fill(color(0, 0, 255));
		ellipse(xbase+35, ybase+160, 12, 12);
		fill(color(255, 0, 0));
		ellipse(xbase+35, ybase+180, 12, 12);
		
		textAlign(LEFT, CENTER);
		fill(0, 0, 0);
		text("Shallow", xbase+50, ybase+140);
		text("Intermediate", xbase+50, ybase+160);
		text("Deep", xbase+50, ybase+180);

		text("Past hour", xbase+50, ybase+200);
		
		fill(255, 255, 255);
		int centerx = xbase+35;
		int centery = ybase+200;
		ellipse(centerx, centery, 12, 12);

		strokeWeight(2);
		line(centerx-8, centery-8, centerx+8, centery+8);
		line(centerx-8, centery+8, centerx+8, centery-8);
		
		if (lastClicked != null && lastClicked instanceof CityMarker && markersWithinCity.size() != 0) {
			cityMarkerLeftBottomGUI();
		}
			
	}

	private void cityMarkerLeftBottomGUI() {
		fill(255, 250, 240);
		rect(25, 330, 150, 250);
		
		fill(0);
		float avergMagnitude = earthQuakesAffCityAverage();
		String str = "Average Magnitude";
		text(str, 35, 350);
		text(avergMagnitude, 35, 370);
		
		int numberOfEarthQuakes = markersWithinCity.size();
		text("Total Earthquakes", 35, 390);
		text(numberOfEarthQuakes, 35, 410);
		
		int numLand = numberOfLandQuakes();
		text("Quakes Num on Land", 35, 430);
		text(numLand, 35, 450);
		
		int numOcean = numberOfEarthQuakes - numLand;
		text("Quakes Num on Ocean", 35, 470);
		text(numOcean, 35, 490);
	}
	
	private float earthQuakesAffCityAverage() {
		float average = 0f;
		for(int i = 0; i < markersWithinCity.size(); ++i) {
			Marker m = markersWithinCity.get(i);
			average +=  ((EarthquakeMarker) m).getMagnitude() / markersWithinCity.size();
		}
		return average;
	}
	
	private int numberOfLandQuakes() {
		int num = 0;
		for(int i = 0; i < markersWithinCity.size(); ++i) {
			Marker m = markersWithinCity.get(i);
			if (((EarthquakeMarker) m).isOnLand) {
				num++;
			}
		}
		return num;
	}
	
	
	private boolean isLand(PointFeature earthquake) {
		for (Marker country : countryMarkers) {
			if (isInCountry(earthquake, country)) {
				return true;
			}
		}
		
		return false;
	}
	
	// prints countries with number of earthquakes
	private void printQuakes() {
		int totalWaterQuakes = quakeMarkers.size();
		for (Marker country : countryMarkers) {
			String countryName = country.getStringProperty("name");
			int numQuakes = 0;
			for (Marker marker : quakeMarkers)
			{
				EarthquakeMarker eqMarker = (EarthquakeMarker)marker;
				if (eqMarker.isOnLand()) {
					if (countryName.equals(eqMarker.getStringProperty("country"))) {
						numQuakes++;
					}
				}
			}
			if (numQuakes > 0) {
				totalWaterQuakes -= numQuakes;
				System.out.println(countryName + ": " + numQuakes);
			}
		}
		System.out.println("OCEAN QUAKES: " + totalWaterQuakes);
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
