package ev3ObjectSearch;
import ev3Drive.*;
import ev3Sensors.*;


public class ObjectSearch {
	private Odometer odo;
	private ObjectPoller objectPoller;
	public ObjectSearch(Odometer odo, ObjectPoller objectPoller ){
		this.odo = odo;
		this.objectPoller = objectPoller;
		Navigation nav = new Navigation(odo);
		
	}
	
	public void doBoardSearch(){
		
	}
	
}
