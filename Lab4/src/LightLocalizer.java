import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

public class LightLocalizer {
	// Resources
	private Odometer odo;
	private SampleProvider colorSource;
	private float[] colorData;
	private Navigation nav;

	// Variables
	private double d = 5; // Distance from centre of rotation to light sensor
	private int xStart = 12;
	private int yStart = 12;
	public static float ROTATION_SPEED = 30;
	private double intersectionAngles[] = { 0, 0, 0, 0 };

	public LightLocalizer(Odometer odo, SampleProvider colorSource, float[] colorData) {
		this.odo = odo;
		this.colorSource = colorSource;
		this.colorData = colorData;
		this.nav = new Navigation(odo);
		nav.start();
	}

	public void doLocalization() {
		// drive to location listed in tutorial
		// start rotating and clock all 4 gridlines
		// do trig to compute (0,0) and 0 degrees

		// when done travel to (0,0) and turn to 0 degrees

		// Travel to starting position and face south
		// nav.travelTo(xStart, yStart);
		nav.turnTo(270, true);

		// Begin counter-clockwise turn
		nav.setSpeeds(-1 * ROTATION_SPEED, ROTATION_SPEED);
		int i = 0;
		while (i < 4) {
			colorSource.fetchSample(colorData, 0);
			// If a line is detected AND the reading is distinct to ensure we don't double count lines
			if (colorData[0] < 50 && getAngleDifference(intersectionAngles[i], odo.getAng()) > 10) {
				Sound.beep();
				intersectionAngles[i] = odo.getAng();
				i++;
			}
			double newX = d * Math.cos(0.5 * getAngleDifference(intersectionAngles[0], intersectionAngles[2]));
			double newY = d * Math.sin(0.5 * getAngleDifference(intersectionAngles[1], intersectionAngles[3]));
			double newTheta = 45 + 0.5 * getAngleDifference(intersectionAngles[0], intersectionAngles[3]);
			double newPosition[] = { newX, newY, newTheta };
			boolean update[] = { true, true, true };
			odo.setPosition(newPosition, update);
		}

	}

	public double getAngleDifference(double a, double b) {
		// Given a and b, find the minimum distance between a and b (in degrees)
		// while accounting for angle wrapping
		double result = 0;
		// Find the difference
		result = Math.abs(a - b);
		// Account for wrapping
		if (result > 180) {
			result = -1 * (result - 360);
		}

		return result;
	}

}
