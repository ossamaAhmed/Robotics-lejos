import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

public class USLocalizer {
	public enum LocalizationType {
		FALLING_EDGE, RISING_EDGE
	};

	public static float ROTATION_SPEED = 30;

	private Odometer odo;
	private SampleProvider usSensor;
	private float[] usData;
	private LocalizationType locType;
	private Navigation nav;

	//

	public USLocalizer(Odometer odo, SampleProvider usSensor, float[] usData,
			LocalizationType locType) {
		this.odo = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
		this.nav = new Navigation(odo);
		nav.start();
	}

	public void doLocalization() {
		double[] pos = new double[3];
		double angleA, angleB;

		if (locType == LocalizationType.FALLING_EDGE) {
			// Begin rotating clockwise until there is no wall
			nav.setSpeeds(ROTATION_SPEED, -1 * ROTATION_SPEED);
			while (facingWall() == true) {
			}
			// The robot no longer sees a wall (or never did), keep rotating
			// till there is a wall
			while (facingWall() == false) {
			}
			// The robot now sees a wall, this is angle A
			angleA = odo.getAng();
			Sound.beep();
			// Switch direction of rotation then wait for no wall
			nav.setSpeeds(-1 * ROTATION_SPEED, ROTATION_SPEED);
			while (facingWall() == true) {
			}
			// The robot no longer sees a wall
			while (facingWall() == false) {
			}
			// The robot now sees a wall, this is angle B
			angleB = odo.getAng();
			Sound.beep();
			correctHeading(angleA, angleB);

			// switch direction and wait until it sees no wall

			// keep rotating until the robot sees a wall, then latch the angle

			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'

			// update the odometer position (example to follow:)
			// odo.setPosition(new double[] { 0.0, 0.0, 0.0 }, new boolean[] {
			// true, true, true });
		} else {
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall. This
			 * is very similar to the FALLING_EDGE routine, but the robot will
			 * face toward the wall for most of it.
			 */
			// Begin rotating clockwise until there is a wall
			nav.setSpeeds(ROTATION_SPEED, -1 * ROTATION_SPEED);
			while (facingWall() == false) {
			}
			// The robot sees a wall (or it always did), keep rotating till
			// there is no wall
			while (facingWall() == true) {
			}
			// The robot now sees a wall, this is angle A
			angleA = odo.getAng();
			Sound.beep();
			// Switch direction of rotation then wait for a wall
			nav.setSpeeds(-1 * ROTATION_SPEED, ROTATION_SPEED);
			while (facingWall() == false) {
			}
			// The robot no longer sees a wall
			while (facingWall() == false) {
			}
			// The robot now sees a wall, this is angle B
			angleB = odo.getAng();
			Sound.beep();
			correctHeading(angleA, angleB);
		}
		// Stop robot
		nav.setSpeeds(0, 0);
	}

	private float getFilteredData() {
		// Filters are implemented into the SampleProvider already
		usSensor.fetchSample(usData, 0);
		return usData[0];
	}

	private void correctHeading(double a, double b) {
		if (a < b) {
			odo.setAng(135 + ((b - a) / 2));
		} else if (a > b)
			odo.setAng(135 + ((b - a + 360) / 2));
	}

	private boolean facingWall() {
		boolean facingWall = true;
		if (getFilteredData() < 40)
			facingWall = true;
		else if (getFilteredData() > 40)
			facingWall = false;
		return facingWall;
	}

}
