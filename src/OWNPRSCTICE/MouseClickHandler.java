package OWNPRSCTICE;

import com.jogamp.newt.event.MouseAdapter;
import com.jogamp.newt.event.MouseEvent;

import processing.core.PApplet;
import processing.core.PGraphics;

public class MouseClickHandler extends PApplet {

    private int sectionCount = 0;

    public void setup() {
        size(800, 800);
        background(255);

 
    }

	public void draw() {
		fill(140);
		rect(50, 50, 20, 20);
		
		fill(0, 0, 0);
		rect(50, 100, 20, 20);
    }
	
	@Override
	public void mouseClicked() {
		if (mouseX >= 50 && mouseX <= 70 && mouseY >= 50 && mouseY <= 70) {
			background(140);
			fill(255, 0, 0);
			rect(200, 200, 255, 255);
		} else {
			background(0);
		}
	} 
}
