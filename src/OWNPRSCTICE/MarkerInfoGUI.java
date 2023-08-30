package OWNPRSCTICE;

import processing.core.PApplet;

public class MarkerInfoGUI extends PApplet {

    Marker marker;
    boolean showInfo = false;

    public void settings() {
        size(800, 600);
    }

    public void setup() {
        marker = new Marker(width / 2, height / 2);
    }

    public void draw() {
        background(255);
        
        marker.display();
        
        if (showInfo) {
            displayMarkerInfo();
        }
    }

    public void mousePressed() {
        if (marker.isMouseOver(mouseX, mouseY)) {
            showInfo = !showInfo;
        } else {
            showInfo = false;
        }
    }

    void displayMarkerInfo() {
        fill(0);
        textSize(16);
        textAlign(LEFT, TOP);
        String info = "Marker Information:\n" +
                      "Latitude: " + marker.latitude + "\n" +
                      "Longitude: " + marker.longitude;
        text(info, 20, 20);
    }

    class Marker {
        float x, y;
        float size = 20;
        float latitude = (float) 37.7749; // Replace with actual latitude
        float longitude = (float) -122.4194; // Replace with actual longitude

        Marker(float x, float y) {
            this.x = x;
            this.y = y;
        }

        void display() {
            fill(255, 0, 0);
            ellipse(x, y, size, size);
        }

        boolean isMouseOver(float mx, float my) {
            float distance = dist(mx, my, x, y);
            return distance < size / 2;
        }
    }

    public static void main(String[] args) {
        PApplet.main("MarkerInfoGUI");
    }
}