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
	private int FORWARD_SPEED = 200;
	private int ROTATE_SPEED = 50;

	public Navigator(Odometer odometer, UltrasonicPoller usPoller,
			EV3LargeRegulatedMotor leftMotor,
			EV3LargeRegulatedMotor rightMotor, double track, double wr) {
		this.odometer = odometer;
		this.usPoller = usPoller;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.track = track;
		this.wr = wr;
		// Reset Motors
		for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] {
				leftMotor, rightMotor }) {
			motor.stop();
			motor.setAcceleration(3000);
		}
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
		double d = theta - odometer.getTheta();

		// Find minimum angle
		if (d >= -180 || d <= 180) {
			// Leave d as is
		} else if (d < -180) {
			d = d + 360;
		} else if (d > 180) {
			d = d - 360;
		}
		//

		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		leftMotor.rotate(convertAngle(wr, track, d), true);
		rightMotor.rotate(-convertAngle(wr, track, d), false);
	}

	public boolean isNavigating() {
		return this.isNavigating;
	}

	public int getDistance() {
		return this.usPoller.getDistance();
	}

	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

}
