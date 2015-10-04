package ev3Navigator;


import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import ev3Odometer.*;
import ev3Ultrasonic.*;

public class Lab3  {
	
	// Static Resources:
	// Left motor connected to output A
	// Right motor connected to output D
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	// Ultrasonic sensor connected to S1
	private static final SensorModes usSensor = new EV3UltrasonicSensor(LocalEV3.get().getPort("S1"));		// usSensor is the instance
	
	// Constants
	public static final double TRACK = 12.2;
	public static final double WHEEL_RADIUS = 2.1;

	public static void main(String[] args) {
		int buttonChoice;
		/////////////////
		// Initialization of Objects
		// Odometer
		Odometer odometer = new Odometer(leftMotor,rightMotor,TRACK,WHEEL_RADIUS);
		OdometryCorrection odometryCorrection = new OdometryCorrection(odometer);

		//Ultrasonic
		SampleProvider usDistance = usSensor.getMode("Distance");	// usDistance provides samples from this instance
		float[] usData = new float[usDistance.sampleSize()];		// usData is the buffer in which data are returned
		UltrasonicPoller usPoller = new UltrasonicPoller(usDistance, usData);
		//Navigator
		final Navigator navigator = new Navigator(odometer,usPoller,leftMotor,rightMotor,TRACK,WHEEL_RADIUS);
		// LCD
		TextLCD lcd = LocalEV3.get().getTextLCD();
		NavigatorDisplay display = new NavigatorDisplay(odometer,usPoller,lcd);
		////////////////
		
		
		//User Interface
		do {
			lcd.clear();
			lcd.drawString("< Debug | SqDrive >", 0, 0);
			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);

		if (buttonChoice == Button.ID_LEFT) {
			// Left Button Choice
			odometer.start();
			//usPoller.start();
			display.start();
			//navigator.start();
			//odometryCorrection.start();
			leftMotor.forward();
			leftMotor.flt();
			rightMotor.forward();
			rightMotor.flt();
			
		} else {
			// Square Drive
			
			odometer.start();
			display.start();
			usPoller.start();
			(new Thread() {
				public void run() {
					navigator.start();
				}
			}).start();
			navigator.travelTo(60, 30);
			Sound.beep();
			navigator.travelTo(30, 30);
			Sound.beep();
			navigator.travelTo(30, 60);
			Sound.beep();
			navigator.travelTo(60, 0);
			Sound.beep();

			
		}
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}