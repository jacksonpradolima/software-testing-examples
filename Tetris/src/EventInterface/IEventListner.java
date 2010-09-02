/* Description: This is an interface used to send the "events" from
 * the GUI and Timer to the Logic.
 * 
 * Copyright 2003 Gustav Evertsson All Rights Reserved.
*/

package EventInterface;

public interface IEventListner {
	
	public static final int UP			= 1;
	public static final int DOWN		= 2;
	public static final int LEFT		= 3;
	public static final int RIGHT		= 4;

	public static final int TIMER		= 5;
	
	public static final int NEWGAME	= 6;
	public static final int PAUSE		= 7;
	
	// This method is called when a new events is fired.
	public void incomingEvent(int eventType);
	
}