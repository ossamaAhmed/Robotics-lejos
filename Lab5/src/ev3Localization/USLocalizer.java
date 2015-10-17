package ev3Localization;
import ev3Drive.Navigation;
import ev3Drive.Odometer;
import ev3Sensors.FilteredUltrasonicPoller;
import lejos.hardware.Sound;

public class USLocalizer {
	public enum LocalizationType {
		FALLING_EDGE, RISING_EDGE
	};

	// Variables
	public static float ROTATION_SPEED = 40;
	private int cornerAngle = 225;

	private Odometer odo;
	private LocalizationType locType;
	private Navigation nav;
	private FilteredUltrasonicPoller usPoller;

	//

	public USLocalizer(Odometer odo, FilteredUltrasonicPoller usPoller, LocalizationType locType) {
		this.odo = odo;
		this.locType = locType;
		this.nav = new Navigation(odo);
		this.usPoller = usPoller;
		nav.start();
	}

	public void doLocalization() {
		double angleA, angleB;

		if (locType == LocalizationType.FALLING_EDGE) {
			nav.setSpeeds(ROTATION_SPEED, -1 * ROTATION_SPEED);
			while (facingWall() != 0) { // read: Rotate clockwise until facing wall == false
			}
			while (facingWall() != 1) { // Rotate clockwise until facing wall == true
			}
			angleA = odo.getAng(); // First wall detected, this is angle A. Switch directions
			Sound.beep();
			nav.setSpeeds(-1 * ROTATION_SPEED, ROTATION_SPEED);
			while (facingWall() != 0 || getAngleDistance(angleA,odo.getAng()) < 30) { // Rotate c-clockwise until facing wall == false
			}
			while (facingWall() != 1) { // Rotate c-clockwise until facing wall == true
			}
			angleB = odo.getAng(); // Second wall detected, this is angle B.
			Sound.beep();
			odo.setAng(cornerAngle-180 + (getAngleDistance(angleA, angleB) / 2)); // Fix heading

		} else {
			nav.setSpeeds(ROTATION_SPEED, -1 * ROTATION_SPEED);
			while (facingWall() != 1) { // Rotate clockwise until facing wall == true
			}
			while (facingWall() != 0) { // Rotate clockwise until facing wall == false
			}
			angleA = odo.getAng(); // First wall detected, this is angle A. Switch directions
			Sound.beep();
			nav.setSpeeds(-1 * ROTATION_SPEED, ROTATION_SPEED);
			while (facingWall() != 1 || getAngleDistance(angleA,odo.getAng()) < 30 ) { // Rotate c-clockwise until facing wall == true
			}
			while (facingWall() != 0) { // Rotate c-clockwise until facing wall == false
			}
			angleB = odo.getAng(); // Second wall detected, this is angle B.
			Sound.beep();
			odo.setAng(cornerAngle + (getAngleDistance(angleA, angleB) / 2)); // Fix Heading
		}
		nav.turnTo(0, true);
		nav.setSpeeds(0, 0);
	}

	private int facingWall() {
		// 1 = True, 0 = False , 2 = Don't know
		float d = usPoller.getDistance();
		int facingWall = 2;
		if (d < 0.40)
			facingWall = 1;
		else if (d > 0.50)
			facingWall = 0;
		return facingWall;
	}

	public double getAngleDistance(double a, double b) {
		// Given a and b, find the minimum distance between a and b (in degrees)
		// while accounting for angle wrapping
		double result = 0;
		// Find the difference
		result = Math.abs(a - b);
		// Account for wrapping
		if (result > 180) {
			result = -1 * (result - 360);
		}

		return result;
	}

}
