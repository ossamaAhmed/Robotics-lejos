/* 
 * OdometryCorrection.java
 */
package ev3Odometer;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.Color;

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	private static EV3ColorSensor sensor = new EV3ColorSensor(LocalEV3.get().getPort("S2"));
	private SensorMode mode;
	private Object lock;
	private int correction=4;

	// constructor
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
		// Mode to sense colors
		mode= sensor.getRedMode();
		// Turn on white LED
		sensor.setFloodlight(Color.RED);
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;

		while (true) {
				correctionStart = System.currentTimeMillis();
				float[] sample= new float[mode.sampleSize()];
				mode.fetchSample(sample, 0);
				float intensity= sample[0]*100;
				// Check the sensor for a black line
				if (intensity<60) {
					// Rounds the position to the nearest multiple of 15 and nearest Pi/2 for angle
					double newX=odometer.getX();
					double newY=odometer.getY();
					double newTheta = Math.round(odometer.getTheta()/(Math.PI / 2))* (Math.PI)/2 ;
					
					// Reduce Theta to be between 0 and 2 Pi
					while (newTheta >= 2*Math.PI)
					{
						newTheta -= 2*Math.PI;
					}
					
					//first time passing a vertical line
					if(newY<15 && newTheta==0){
						newY= 15;
					}
					//first time passing a horizontal line 
					else if(newX<15 && newTheta>(Math.PI*0.25)){
						newX=15;
						
					}
					// Facing up or down
					else if (newTheta == 0 || newTheta== (Math.PI)){
						newY = Math.round(odometer.getY()/15)*15;
						Sound.buzz();
						}
					// Facing Right or left
					else if (newTheta == 0.5*Math.PI || newTheta== (Math.PI*1.5) ){
						newX= Math.round(odometer.getX()/15)*15;
						Sound.beep();
						}
					
					else {
						Sound.buzz();
					}
					
					// Set these new values in the odometer
					double[] newPos = {newX,newY,newTheta};
					boolean[] newUpdate = {true,true,true};
					odometer.setPosition(newPos,newUpdate);
				}

				// this ensure the odometry correction occurs only once every
				// period
				correctionEnd = System.currentTimeMillis();
				if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
					try {
						Thread.sleep(CORRECTION_PERIOD
								- (correctionEnd - correctionStart));
					} catch (InterruptedException e) {
						// there is nothing to be done here because it is not
						// expected that the odometry correction will be
						// interrupted by another thread
					}
				}
			
		}
	}
}

