package ev3ObjectSearch;
import lejos.hardware.Sound;
import ev3Drive.*;
import ev3Sensors.*;


public class ObjectSearch {
	private Odometer odo;
	private FilteredUltrasonicPoller usPoller;
	private ObjectPoller objectPoller;
	private Navigation nav;
	private int counter;
	public ObjectSearch(Odometer odo, ObjectPoller objectPoller ,FilteredUltrasonicPoller usPoller){
		this.odo = odo;
		this.objectPoller = objectPoller;
		this.usPoller= usPoller;
		this.nav = new Navigation(odo);
		this.counter=0;
	}
	
	public void doBoardSearch(){
		this.nav.travelTo(30, 30);
		this.nav.turnTo(2, false);
		while(this.odo.getAng()<358){
			this.nav.turnTo(odo.getAng()+1, false);
			if(objectSeen())break;
		}
		this.nav.setSpeeds(0, 0);
		while(usPoller.getDistance()>0.075){
			this.nav.setSpeeds(50, 50);
		}
		this.nav.setSpeeds(0, 0);
		
		
	}
	public double getWallUsValue(){
		return 0.40*(1+(0.181818*Math.abs(Math.sin(Math.toRadians(2*odo.getAng()+180)))));
		
	}
	public boolean objectSeen(){
		if(usPoller.getDistance()<getWallUsValue()){
			Sound.beep();
			return true;
		}
		return false;
	}
	public int getCounter(){
		return counter;
	}	
}
