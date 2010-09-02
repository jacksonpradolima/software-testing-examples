/*
 * Created on 15/12/2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package telecom;

import junit.framework.TestCase;

public class TelecomTestCase extends TestCase {
	
  public TelecomTestCase( String str ) {
    super( str );
  }

  public TelecomTestCase(  ) {
    this( "" );
  }
  
  public void setup() {}
  
  //Covering All-nodes-c
  
  //Call Constructor
  
  public void testCoverCCNodesCallConstrLongDistance() {
  	Customer jim = new Customer("Jim", 650, "3361-1111");
    Customer mik = new Customer("Mik", 500, "3361-1112");
    
    Call c1 = new Call(jim, mik, false);
  }
  
  public void testCoverCCNodesCallConstrLocal() {
  	Customer jim = new Customer("Jim", 650, "3361-1111");
    Customer mik = new Customer("Mik", 650, "3361-1112");
    
    Call c1 = new Call(jim, mik, false);
  }
  
  //Pickup method
  
  public void testCoverCCNodesPickup() {
	Customer jim = new Customer("Jim", 650, "3361-1111");
	Customer mik = new Customer("Mik", 650, "3361-1112");
	    
	Call c1 = new Call(jim, mik, false);
	    
	c1.pickup();
	
	//since the connection just started but did not stop,
	//connection time should be 0	
	assertTrue(Timing.aspectOf().getTotalConnectTime(jim) == 0);
  }
  
  public void testCoverCCNodesCallHangup() {
  	Customer jim = new Customer("Jim", 650, "3361-1111");
    Customer mik = new Customer("Mik", 650, "3361-1112");
    
    Call c1 = new Call(jim, mik, false);
    
    c1.pickup();
    wait(2.0);
    c1.hangup();
   
    //since jim called mik and stayed connected for around 200 seconds,
    //total charge for jim should be around 200 * 3 (which is the local
    //rate). Since the time is not very precise because it depends
    //on the execution, assertion cannot be exact.

    assertTrue((Timing.aspectOf().getTotalConnectTime(jim) > 150) &&
    		   (Timing.aspectOf().getTotalConnectTime(jim) < 250));
    assertTrue((Billing.aspectOf().getTotalCharge(jim) > 450) &&
    		   (Billing.aspectOf().getTotalCharge(jim) < 750));
  }
  
  protected static void wait(double seconds) {
    Object dummy = new Object();
    synchronized (dummy) {
    //cheat and only wait 0.1 seconds per second
        try {dummy.wait((long)(seconds*100)); }
        catch (Exception e) {}
    }
  }
}
