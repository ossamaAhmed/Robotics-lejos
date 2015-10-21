package ev3ObjectSearch;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class ObjectSearchArm {
	// Init
	private static final EV3LargeRegulatedMotor armMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	
	// Adjustable Varibles
	private static final int armAngle = 135; // The required rotation to raise or lower the arm
	public ObjectSearchArm(){
		}
	
	public void raiseArm(){
		armMotor.rotateTo(0);
	}
	
	public void dropArm(){
		armMotor.rotate(armAngle);
		
	}

}
