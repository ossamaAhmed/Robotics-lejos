package ev3Navigator;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import ev3Odometer.*;
import ev3Ultrasonic.*;

public class Navigator extends Thread {
	// Variables
	private Odometer odometer;
	private UltrasonicPoller usPoller;
	private static EV3LargeRegulatedMotor leftMotor;
	private static EV3LargeRegulatedMotor rightMotor;
	private double track;
	private double wr;
	private boolean isNavigating;

	public Navigator(Odometer odometer, UltrasonicPoller usPoller,
			EV3LargeRegulatedMotor leftMotor,
			EV3LargeRegulatedMotor rightMotor, double track, double wr) {
		this.odometer = odometer;
		this.usPoller = usPoller;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.track = track;
		this.wr = wr;
	}

	public void run() {
		while (true) {
			// TODO Do stuff here

		}

	}

	public void travelTo(double x, double y) {
		// TODO

	}

	public void turnTo(double theta) {
		// TODO
	}

	public boolean isNavigating() {
		return this.isNavigating;
	}

	public int getDistance() {
		return this.usPoller.getDistance();
	}

}
