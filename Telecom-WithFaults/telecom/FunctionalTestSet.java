package telecom;

import junit.framework.TestCase;

public class FunctionalTestSet extends TestCase {
		
	//Functional testing the Call class
	
	public FunctionalTestSet(String name) {
		super(name);
	}
	
    /** 
     * Constructor specification:
     * Create a new call connecting caller to receiver
     * with a new connection. This should really only be
     * called by Customer.call(..) 
     */
	
    /**
     * Pickup specification:
     * picking up a call completes the current connection
     *(this means that you shouldnt merge calls until
     * they are completed)
     */
	
	public void testPickup() {
		Call c = new Call(new Customer("Jim", 650, "3361-1111"), 
				new Customer("Mik", 500, "3361-1112"), 
				false);
		c.pickup();
		assertTrue(c.isConnected());
	}
	
    /**
     * Hangup specification:
     * hanging up a call drops the connection
     */	
	public void testHangup() {
		Customer jim = new Customer("Jim", 650, "3361-1111");
		Customer mik = new Customer("Mik", 500, "3361-1112");
		Call c = new Call(jim, mik, false);
		c.pickup();
		c.hangup();
		assertFalse(c.isConnected());
	}	
    
	
    /**
     * Includes specification:
     * is Customer c one of the customers in this call?
     */
	
	public void testIncludesTrue() {
		Customer jim = new Customer("Jim", 650, "3361-1111");
		Customer mik = new Customer("Mik", 500, "3361-1112");
		Call c = new Call(jim, mik, false);
		c.pickup();		
		assertTrue(c.includes(mik));
	}
	
	public void testIncludesFalse() {
		Customer jim = new Customer("Jim", 650, "3361-1111");
		Customer mik = new Customer("Mik", 500, "3361-1112");
		Customer john = new Customer("John", 500, "3361-1113");
		Call c = new Call(jim, mik, false);
		c.pickup();		
		assertFalse(c.includes(john));
	}
	
	/**
     * Merge specification:
     * Merge all connections from call 'other' into 'this'
     */
	
	public void testMerge() {
		Customer jim = new Customer("Jim", 650, "3361-1111");
		Customer mik = new Customer("Mik", 500, "3361-1112");
		Customer john = new Customer("John", 500, "3361-1113");
		Customer cris = new Customer("Cris", 500, "3361-1114");
		Call c1 = new Call(jim, mik, false);
		c1.pickup();		
		Call c2 = new Call(john, cris, false);
		c2.pickup();
		c1.merge(c2);
		assertTrue(c1.includes(john));
		assertTrue(c1.includes(cris));
	}
}