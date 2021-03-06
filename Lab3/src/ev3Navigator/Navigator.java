package ev3Navigator;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import ev3Odometer.*;
import ev3Ultrasonic.*;

public class Navigator extends Thread {
	// Variables
	private final int blockThreshold = 7;
	private final int FORWARD_SPEED = 250;
	private final int ROTATE_SPEED = 150;
	private final int BLOCK_WIDTH = 30;
	private final int BLOCK_LENGTH = 40;
	private final int ROTATE_BLOCK = 90;
	private final int DISTANCE_ERROR = 1;

	private Odometer odometer;
	private UltrasonicPoller usPoller;
	private static EV3LargeRegulatedMotor leftMotor;
	private static EV3LargeRegulatedMotor rightMotor;
	private double track;
	private double wr;
	private boolean isNavigating;
	private double desiredX;
	private double desiredY;

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
	}

	public void run() {
		// Nothing is required to run in this thread
	}

	public void move() {
		double dx = this.desiredX - odometer.getX();
		double dy = this.desiredY - odometer.getY();
		double distance = Math.sqrt((Math.pow(dx, 2) + Math.pow(dy, 2)));
		double distanceFromBlock = this.usPoller.getDistance();

		if (distanceFromBlock < blockThreshold) {
			turnBy(ROTATE_BLOCK);
			moveForward(BLOCK_WIDTH);

			turnBy(-1 * ROTATE_BLOCK);
			moveForward(BLOCK_LENGTH);

			turnBy(-1 * ROTATE_BLOCK);
			moveForward(BLOCK_WIDTH);

			turnBy(getMinAngle(getNewHeading()));
		}

		else {
			leftMotor.setSpeed(FORWARD_SPEED);
			rightMotor.setSpeed(FORWARD_SPEED);
			leftMotor.forward();
			rightMotor.forward();
			if (distance < DISTANCE_ERROR) {
				rightMotor.setSpeed(0); // stoping the motors
				leftMotor.setSpeed(0);
				leftMotor.forward();
				rightMotor.forward();
				isNavigating = false;
			}

		}

	}

	public void moveForward(double d) {
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.rotate(convertDistance(wr, d), true);
		rightMotor.rotate(convertDistance(wr, d), false);
	}

	public void travelTo(double x, double y) {
		this.desiredX = x;
		this.desiredY = y;
		isNavigating = true;
		// Find new heading -> convert to min angle to turn -> turn by this
		// angle
		turnBy(getMinAngle(getNewHeading()));
		while (this.isNavigating) {
			move();
		}
	}

	public void turnBy(double theta) {
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		leftMotor.rotate(-convertAngle(wr, track, theta), true);
		rightMotor.rotate(convertAngle(wr, track, theta), false);
	}

	public boolean isNavigating() {
		return this.isNavigating;
	}

	public double getMinAngle(double theta) {

		double minAngle = theta - odometer.getThetaDegrees();
		// Find minimum angle
		if (minAngle >= (-1 * Math.toDegrees(Math.PI))
				&& minAngle <= (Math.toDegrees(Math.PI))) {
			// Leave d as is
		} else if (minAngle < (-1 * Math.toDegrees(Math.PI))) {
			minAngle = minAngle + (Math.toDegrees(Math.PI) * 2);
		} else if (minAngle > (Math.toDegrees(Math.PI))) {
			minAngle = minAngle - (2 * Math.toDegrees(Math.PI));
		}

		return minAngle;
	}

	public double getNewHeading() {
		double dx = this.desiredX - odometer.getX();
		double dy = this.desiredY - odometer.getY();
		double theta = 0;
		if (dx == 0 && dy > 0) {
			theta = Math.toDegrees(Math.PI / 2);
		} else if (dx == 0 && dy < 0) {
			theta = -1 * Math.toDegrees(Math.PI / 2);
		}

		else if (dx > 0) {
			theta = Math.toDegrees(Math.atan(dy / dx));

		}

		else if (dx < 0 && dy >= 0) {
			theta = Math.toDegrees(Math.atan(dy / dx))
					+ (Math.toDegrees(Math.PI));

		}

		else if (dx < 0 && dy < 0) {
			theta = Math.toDegrees(Math.atan(dy / dx))
					- (Math.toDegrees(Math.PI));

		}
		return theta;
	}

	public int getDistance() {
		return this.usPoller.getDistance();
	}

	private static int convertDistance(double radius, double distance) {
		return (int) (((Math.toDegrees(Math.PI)) * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius,
				Math.PI * width * angle / (2 * Math.toDegrees(Math.PI)));
	}

}
