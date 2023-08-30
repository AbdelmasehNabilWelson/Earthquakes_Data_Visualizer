package module5;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PConstants;
import processing.core.PGraphics;

/** Implements a visual marker for cities on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 *
 */
// TODO: Change SimplePointMarker to CommonMarker as the very first thing you do 
// in module 5 (i.e. CityMarker extends CommonMarker).  It will cause an error.
// That's what's expected.
public class CityMarker extends CommonMarker {
	
	public static int TRI_SIZE = 5;  // The size of the triangle marker
	
	public CityMarker(Location location) {
		super(location);
	}
	
	
	public CityMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
		// Cities have properties: "name" (city name), "country" (country name)
		// and "population" (population, in millions)
	}

	
	/**
	 * Implementation of method to draw marker on the map.
	 */
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		// Save previous drawing style
		pg.pushStyle();
		
		// IMPLEMENT: drawing triangle for each city
		pg.fill(150, 30, 30);
		pg.triangle(x, y-TRI_SIZE, x-TRI_SIZE, y+TRI_SIZE, x+TRI_SIZE, y+TRI_SIZE);
		
		// Restore previous drawing style
		pg.popStyle();
	}
	
	
	@Override
	/** Show the title of the city if this marker is selected */
	public void showTitle(PGraphics pg, float x, float y) {
		pg.pushStyle();
		//pg.hint(ENABLE_DEPTH_TEST);
		String cityName = "City: " + getCity();
		String countryName = "Country: " +  getCountry();
		String pop = "Population: " + getPopulation();
		
		float maxWidth = Math.max(Math.max(pg.textWidth(cityName), pg.textWidth(countryName)), pg.textWidth(pop));

		float ascent = pg.textAscent();
		float descent = pg.textDescent();
		  
		float lineHeight = ascent + descent;
		pg.rect(x, y, maxWidth + 15, lineHeight * 3 + 5 * 2);
		
		pg.textAlign(PConstants.LEFT, PConstants.TOP);
		pg.fill(0);
		pg.text(cityName, x + 5, y + 5);
		pg.text(countryName, x + 5, y + 5 + lineHeight);
		pg.text(pop, x + 5, y + 5 + 2 * lineHeight);
		
		pg.popStyle();
	} 

	/* Local getters for some city properties.  
	 */
	public String getCity() {
		return getStringProperty("name");
	}
	
	public String getCountry() {
		return getStringProperty("country");
	}
	
	public float getPopulation() {
		return Float.parseFloat(getStringProperty("population"));
	}
}