package ev3Sensors;

import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MedianFilter;

// This class constantly polls the ultrasonic sensor and filters the data
// using a clipping filter and a median filter. There is a ~70 ms delay
// between polling giving a frequency of 14Hz

public class FilteredColorPoller extends Thread {
	private SampleProvider colorFilteredSource;
	float[] colorData;

	public FilteredColorPoller(SensorModes colorSensor,int ReadingsToMedian) {
		SampleProvider colorReading = colorSensor.getMode("Blue");
		// Stack a filter which takes average readings
		SampleProvider colorMedianSource = new MedianFilter(colorReading, ReadingsToMedian);
		// The final, filtered data from the us sensor is stored in colorFilteredSource
		this.colorFilteredSource = colorMedianSource;
		// initialize an array of floats for fetching samples
		this.colorData = new float[colorFilteredSource.sampleSize()];
	}

	public void run() {
		while (true) {
			colorFilteredSource.fetchSample(colorData, 0); // acquire data
			try {
				Thread.sleep(50);
			} catch (Exception e) {
			} // Poor man's timed sampling
		}
	}

	public float getBlue() {
		return this.colorData[0];
	}

}
