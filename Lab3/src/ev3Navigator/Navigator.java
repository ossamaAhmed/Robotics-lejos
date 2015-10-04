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
	private int FORWARD_SPEED = 150;
	private int ROTATE_SPEED = 50;
	private double x;
	private double y;
	private boolean didItTurn= false;
	private Object lock;

	public Navigator(Odometer odometer, UltrasonicPoller usPoller,
			EV3LargeRegulatedMotor leftMotor,
			EV3LargeRegulatedMotor rightMotor, double track, double wr) {
		this.odometer = odometer;
		this.usPoller = usPoller;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.track = track;
		this.wr = wr;
		this.lock = new Object();
		// Reset Motors
	}

	public void run() {
		for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] {
				leftMotor, rightMotor }) {
			motor.stop();
			motor.setAcceleration(2000);
		}
		

	}

	public void move() {
		// First find the required heading angle
		double dx = this.x - odometer.getX();
		double dy = this.y - odometer.getY();
		double theta = 0;
		double distance = Math.sqrt((Math.pow(dx, 2) + Math.pow(dy, 2)));

		if(Math.abs(dx)>0&& Math.abs(dx)<2 && Math.abs(dy)>0&& Math.abs(dy)<2){
			
		}
		else if (dx > 0) {
			theta = Math.toDegrees(Math.atan(dy / dx));
			this.turnTo(theta);

		}

		else if (dx < 0 && dy >= 0) {
			theta = Math.toDegrees(Math.atan(dy / dx)) + 180;
			this.turnTo(theta);


		}

		else if (dx < 0 && dy < 0) {
			theta = Math.toDegrees(Math.atan(dy / dx)) - 180;
			this.turnTo(theta);

		}
		
			this.didItTurn=true;
		
		//

		// Turn to the desired angle before going straight.
		

		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.forward();
		rightMotor.forward();
		if(distance<1){
			rightMotor.setSpeed(0);
			leftMotor.setSpeed(0);
			leftMotor.forward();
			rightMotor.forward();
			isNavigating=false;
		}
		//leftMotor.rotate(convertDistance(wr, distance), true);
		//rightMotor.rotate(convertDistance(wr, distance), false);

	}

	public void travelTo(double x, double y) {
		this.didItTurn=false;
		this.x = x;
		this.y = y;
		isNavigating= true;
		while (this.isNavigating) {
			move();
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public void turnTo(double theta) {
		double d = theta - odometer.getThetaDegrees();
		if(Math.abs(d)<3)return;
		
		// Find minimum angle
		if (d >= -180 || d <= 180) {
			// Leave d as is
		} else if (d < -180) {
			d = d + 360;
		} else if (d > 180) {
			d = d - 360;
		}
		//
		
		double z= d+odometer.getThetaDegrees();
		if(z<0)z+=360;
		if(Math.abs(z-odometer.getThetaDegrees())<3)return;
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		leftMotor.rotate(-convertAngle(wr, track, d), true);
		rightMotor.rotate(convertAngle(wr, track, d), false);
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
