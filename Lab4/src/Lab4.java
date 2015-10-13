import lejos.hardware.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.*;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MeanFilter;

public class Lab4 {

	// Static Resources:
	// Left motor connected to output A
	// Right motor connected to output D
	// Ultrasonic sensor port connected to input S1
	// Color sensor port connected to input S2
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final Port usPort = LocalEV3.get().getPort("S1");
	private static final Port colorPort = LocalEV3.get().getPort("S2");
	private static float lightThreshold=0.40f;
	private static int lightSensorDistance=11;

	// Variables
	private static final float maxValue = 0.5f; // us threshold
	private static final int usReadingsToAverage = 10;
	private static final int colorReadingsToAverage = 3;

	public static void main(String[] args) {
		int buttonChoice = 0;
		// Ultrasonic Sensor Initialization
		@SuppressWarnings("resource")
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);
		SampleProvider usReading = usSensor.getMode("Distance");
		// Filter which caps sensor values to n
		SampleProvider usCappedSource = new MaxValueFilter(usReading, maxValue);
		// Stack a filter which takes average readings
		SampleProvider usAveragedSource = new MeanFilter(usCappedSource, usReadingsToAverage);
		// The final, filtered data from the us sensor is stored in usFilteredSource
		SampleProvider usFilteredSource = usAveragedSource;
		// initialize an array of floats for fetching samples
		float[] usData = new float[usFilteredSource.sampleSize()];
		//
		//

		// Color Sensor Initialization
		@SuppressWarnings("resource")
		SensorModes colorSensor = new EV3ColorSensor(colorPort);
		SampleProvider colorSource = colorSensor.getMode("Red");
		// Stack a filter which takes average readings
		SampleProvider colorAveragedSource = new MeanFilter(colorSource, colorReadingsToAverage);
		// The final, filtered data from the color sensor is stored in colorFilteredSource
		SampleProvider colorFilteredSource = colorAveragedSource;
		// initialize an array of floats for fetching samples
		float[] colorData = new float[colorFilteredSource.sampleSize()];
		//
		//

		// Odometer and Display
		Odometer odo = new Odometer(leftMotor, rightMotor, 30, true);
		LCDInfo lcd = new LCDInfo(odo, usFilteredSource, usData, colorFilteredSource, colorData);

		// User Interface
		(new Thread() {
			public void run() {
				while (Button.waitForAnyPress() != Button.ID_ESCAPE)
					;
				System.exit(0);
			}
		}).start();

		do {
			buttonChoice = Button.waitForAnyPress();
		}

		while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT && buttonChoice != Button.ID_UP && buttonChoice != Button.ID_DOWN);

		if (buttonChoice == Button.ID_LEFT) {
			// perform the ultrasonic localization with falling edge
			USLocalizer usl = new USLocalizer(odo, usFilteredSource, usData, USLocalizer.LocalizationType.FALLING_EDGE);
			usl.doLocalization();
		}

		else if (buttonChoice == Button.ID_RIGHT) {

			// perform the ultrasonic localization with rising edge
			USLocalizer usl = new USLocalizer(odo, usFilteredSource, usData, USLocalizer.LocalizationType.RISING_EDGE);
			usl.doLocalization();
			sleep();
			Sound.beep();
			Navigation nav = new Navigation(odo);
			nav.start();
			colorSource.fetchSample(colorData, 0);
			while(colorData[0]<lightThreshold)
			{
				colorSource.fetchSample(colorData, 0);
				nav.travelTo(odo.getX()+1, odo.getY());
			}
			sleep();
			nav.travelTo(odo.getX()+lightSensorDistance, odo.getY());
			sleep();
			nav.turnTo(90, true);
			sleep();
			colorSource.fetchSample(colorData, 0);
			while(colorData[0]<lightThreshold){
				colorSource.fetchSample(colorData, 0);
				nav.travelTo(odo.getX(), odo.getY()+1);
			}
			sleep();
			nav.travelTo(odo.getX(), odo.getY()+lightSensorDistance);
			sleep();
			nav.turnTo(0, true);
			Sound.beep();
			LightLocalizer lsl = new LightLocalizer(odo, colorFilteredSource, colorData);
			lsl.doLocalization();

		}

		else if (buttonChoice == Button.ID_UP) {
			// Float motors for debugging
			leftMotor.forward();
			leftMotor.flt();
			rightMotor.forward();
			rightMotor.flt();
		}

		else if (buttonChoice == Button.ID_DOWN) {
			// perform light localization
			LightLocalizer lsl = new LightLocalizer(odo, colorFilteredSource, colorData);
			lsl.doLocalization();

		}

	}
	public static void sleep(){
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
