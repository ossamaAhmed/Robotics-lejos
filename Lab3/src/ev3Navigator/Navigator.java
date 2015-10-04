package ev3Navigator;

import lejos.hardware.Sound;
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
    private int FORWARD_SPEED = 250;
    private int ROTATE_SPEED = 150;
    private double desiredX;
    private double desiredY;

    public Navigator(Odometer odometer, UltrasonicPoller usPoller, EV3LargeRegulatedMotor leftMotor,
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
    }

    public void move() {
   	 double dx = this.desiredX - odometer.getX();
   	 double dy = this.desiredY - odometer.getY();
   	 double distance = Math.sqrt((Math.pow(dx, 2) + Math.pow(dy, 2)));
   	 double distanceFromBlock= this.usPoller.getDistance();
   	 if(distanceFromBlock<7){   		
   		turnTo(90);
   		leftMotor.setSpeed(FORWARD_SPEED);
      	rightMotor.setSpeed(FORWARD_SPEED);
      	leftMotor.rotate(convertDistance(wr, 30), true);
      	rightMotor.rotate(convertDistance(wr, 30), false);

      	
   		turnTo(-90);
   		leftMotor.setSpeed(FORWARD_SPEED);
      	rightMotor.setSpeed(FORWARD_SPEED);
      	leftMotor.rotate(convertDistance(wr, 40), true);
      	rightMotor.rotate(convertDistance(wr, 40), false);
      	
   		turnTo(-90);
   		leftMotor.setSpeed(FORWARD_SPEED);
      	rightMotor.setSpeed(FORWARD_SPEED);
      	leftMotor.rotate(convertDistance(wr, 30), true);
      	rightMotor.rotate(convertDistance(wr, 30), false);
      	
    	leftMotor.setSpeed(ROTATE_SPEED);
      	rightMotor.setSpeed(ROTATE_SPEED);
   		turnTo(getMinAngle(getNewHeading()));
   	 }

   	 else{
   		leftMotor.setSpeed(FORWARD_SPEED);
      	 rightMotor.setSpeed(FORWARD_SPEED);
      	 leftMotor.forward();
      	 rightMotor.forward();
      	 if (distance < 1) {
      		 rightMotor.setSpeed(0);
      		 leftMotor.setSpeed(0);
      		 leftMotor.forward();
      		 rightMotor.forward();
      		 isNavigating = false;
      	 }
   		 
   	 }
   	 
    }

    public void travelTo(double x, double y) {
   	 this.desiredX = x;
   	 this.desiredY = y;
   	 isNavigating = true;
   	 // Find new heading -> convert to min angle to turn -> turn by this
   	 // angle
   	 turnTo(getMinAngle(getNewHeading()));
   	 while (this.isNavigating) {
   		 move();

   	 }

    }

    public void turnTo(double theta) {
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
   	 if (minAngle >= -180 && minAngle <= 180) {
   		 Sound.buzz();
   		 // Leave d as is
   	 } else if (minAngle < -180) {
   		 minAngle = minAngle + 360;
   	 } else if (minAngle > 180) {
   		 minAngle = minAngle - 360;
   	 }
   	 //

   	 return minAngle;
    }

    public double getNewHeading() {
   	 double dx = this.desiredX - odometer.getX();
   	 double dy = this.desiredY - odometer.getY();
   	 double theta = 0;
   	 double distance = Math.sqrt((Math.pow(dx, 2) + Math.pow(dy, 2)));
   	 if(dx==0 && dy>0){
   		 theta= Math.toDegrees(Math.PI/2);
   	 }
   	 else if(dx==0 &&dy<0){
   		 theta= -1*Math.toDegrees(Math.PI/2);
   	 }

   	 else if (dx > 0) {
   		 theta = Math.toDegrees(Math.atan(dy / dx));

   	 }

   	 else if (dx < 0 && dy >= 0) {
   		 theta = Math.toDegrees(Math.atan(dy / dx)) + 180;

   	 }

   	 else if (dx < 0 && dy < 0) {
   		 theta = Math.toDegrees(Math.atan(dy / dx)) - 180;

   	 }
   	 return theta;
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


