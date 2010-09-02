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
  
  public void testCoverCCNodesCallHangup() {
  	Customer jim = new Customer("Jim", 650, "3361-1111");
    Customer mik = new Customer("Mik", 650, "3361-1112");
    
    Call c1 = new Call(jim, mik, false);
    
    c1.hangup();
  }
  
  public void testCoverCCNodesCallPickup() {
  	Customer jim = new Customer("Jim", 650, "3361-1111");
    Customer mik = new Customer("Mik", 650, "3361-1112");
    
    Call c1 = new Call(jim, mik, false);
    
    c1.pickup();
  }

  public void testCoverNodesTimingGetTotalTime() {
  	Customer jim = new Customer("Jim", 650, "3361-1111");
    Customer mik = new Customer("Mik", 650, "3361-1112");
    
    Call c1 = new Call(jim, mik, false);
    
    c1.pickup();
    wait(2.0);
    c1.hangup();

    Timing t = Timing.aspectOf();
    System.out.println(t.getTotalConnectTime(jim));
  }
  
  public void testCoverNodesBillingGetPayer() {
  	Customer jim = new Customer("Jim", 650, "3361-1111");
    Customer mik = new Customer("Mik", 650, "3361-1112");
    
    Connection c = new Local(jim, mik, false);
        
    Billing b = Billing.aspectOf();
    Customer payer = b.getPayer(c);
    System.out.println(payer);
  }
  
  public void testCoverNodesBillingGetRateLocal() {
  	Customer jim = new Customer("Jim", 650, "3361-1111");
    Customer mik = new Customer("Mik", 650, "3361-1112");
    
    Connection c = new Local(jim, mik, false);
    long l = c.callRate();

    System.out.println(l);    
  }
  
  public void testChargeMobileLocal() {
  	Customer jim = new Customer("Jim", 650, "3361-1111");
    Customer mik = new Customer("Mik", 650, "3361-1112");
  
    Call c1 = new Call(jim, mik, true);
    
    c1.pickup();
    wait(2.0);
    c1.hangup();
   
    System.out.println(Billing.aspectOf().getTotalCharge(jim));
    System.out.println(Billing.aspectOf().getTotalCharge(mik));
  }
  
  public void testChargeMobileLD() {
  	Customer jim = new Customer("Jim", 650, "3361-1111");
    Customer mik = new Customer("Mik", 600, "3361-1112");
  
    Call c1 = new Call(jim, mik, true);
    
    c1.pickup();
    wait(2.0);
    c1.hangup();
   
    System.out.println(Billing.aspectOf().getTotalCharge(jim));
    System.out.println(Billing.aspectOf().getTotalCharge(mik));
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
