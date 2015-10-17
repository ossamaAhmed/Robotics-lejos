package ev3ObjectSearch;
import ev3Drive.Odometer;
import ev3Sensors.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.robotics.SampleProvider;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

public class LCDInfo implements TimerListener{
	public static final int LCD_REFRESH = 200;
	private Odometer odo;
	private Timer lcdTimer;
	private TextLCD LCD = LocalEV3.get().getTextLCD();;
	private FilteredUltrasonicPoller usPoller;
	private FilteredColorPoller colorPoller;
	private ObjectPoller objectSearch;

	
	// arrays for displaying data
	private double [] pos;
	
	public LCDInfo(Odometer odo, FilteredUltrasonicPoller usPoller, FilteredColorPoller colorPoller, ObjectPoller objectPoller ) {
		this.odo = odo;
		this.lcdTimer = new Timer(LCD_REFRESH, this);

		
		// initialise the arrays for displaying data
		pos = new double [3];
		
		// start the timer
		lcdTimer.start();
	}
	
	public void timedOut() { 
		odo.getPosition(pos);
		LCD.clear();
		LCD.drawString("X: "+ pos[0], 0, 0);
		LCD.drawString("Y: "+pos[1], 0, 1);
		LCD.drawString("H: "+pos[2], 0, 2);
//		LCD.drawString(, 3, 0);
//		LCD.drawInt((int)(pos[1] * 10), 3, 1);
//		LCD.drawInt((int)pos[2], 3, 2);
//		usSource.fetchSample(usData, 0);
//		colorSource.fetchSample(colorData, 0);
		LCD.drawString("US Distance: " + usPoller.getDistance(), 0, 3 );	// print last US reading
		LCD.drawString("Blue Reading: " + colorPoller.getBlue(), 0, 4 );	// print last color reading
		
		if (objectSearch.isThereObject == 1) {
			LCD.drawString("Object Detected", 0, 5 );
		}
		
		else if (objectSearch.isThereObject == 0) {
			LCD.drawString("No Object Detected", 0, 5 );
		}
	}
}
