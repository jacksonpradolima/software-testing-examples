/* Description: This is the Timer used in the game.
 * 
 * 
 * Copyright 2003 Gustav Evertsson All Rights Reserved.
*/

package Logic;

import EventInterface.*;

public class Timer implements Runnable {
	protected volatile Thread timer;
	protected IEventListner listner;
	protected int sleepTime;
	
	public Timer(IEventListner listner, int sleepTime) {
		this.listner = listner;
		this.sleepTime = sleepTime;
	}

	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}
	
	public void start() {
		// clarkv
		if (Gui.Driver.fileName==null) {
			timer = new Thread(this);
			timer.start();
		}
	}

	public void stop() {
		timer = null;
	}
    	
	public void run() {
		Thread me = Thread.currentThread();
		while (timer == me) {
			try {
				Thread.currentThread().sleep(sleepTime);
			}
			catch (InterruptedException e) { }
			//clarkv
			System.out.println("tm:"+System.currentTimeMillis());
			listner.incomingEvent(IEventListner.TIMER);
		}
	}	
}
