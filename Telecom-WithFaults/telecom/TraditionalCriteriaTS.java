package telecom;

import java.util.Enumeration;

import junit.framework.TestCase;

public class TraditionalCriteriaTS extends TestCase {
	// Covering All-nodes-ei
	
	public TraditionalCriteriaTS(String str) {
		super(str);
	}
	
	//Call constructor
	
	public void testCallConstructorLD() {
		Customer jim = new Customer("Jim", 650, "3361-1111");
		Customer mik = new Customer("Mik", 500, "3361-1112");
		Call c = new Call(jim, mik, false);
	}
	
	public void testCallConstructorLocal() {
		Customer jim = new Customer("Jim", 650, "3361-1111");
		Customer mik = new Customer("Mik", 650, "3361-1112");
		Call c = new Call(jim, mik, false);
	}
	
	//Pickup method	
	public void testCallPickup() {
		Customer jim = new Customer("Jim", 650, "3361-1111");
		Customer mik = new Customer("Mik", 500, "3361-1112");
		Call c = new Call(jim, mik, false);
		
		c.pickup();
		assertTrue(c.isConnected());
	}
	
	//Hangup method	
	public void testCallHangup() {
		Customer jim = new Customer("Jim", 650, "3361-1111");
		Customer mik = new Customer("Mik", 500, "3361-1112");
		Call c = new Call(jim, mik, false);
		
		c.pickup();
		c.hangup();
		
		assertFalse(c.isConnected());
	}
	
	//Includes method	
	public void testIncludesTrue() {
		Customer jim = new Customer("Jim", 650, "3361-1111");
		Customer mik = new Customer("Mik", 500, "3361-1112");
		Call c = new Call(jim, mik, false);
		c.pickup();		
		assertTrue(c.includes(mik));
	}
	
	//Merge method
	
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
	}
	
	// Covering All-edges-ei
	
	//Includes Method
	
	public void testIncludesEdge() {
		Customer jim = new Customer("Jim", 650, "3361-1111");
		Customer mik = new Customer("Mik", 500, "3361-1112");
		Customer john = new Customer("John", 500, "3361-1113");
		Customer cris = new Customer("Cris", 500, "3361-1114");
		Customer carl = new Customer("Carl", 500, "3361-1115");
		Customer luke = new Customer("Luke", 500, "3361-1116");

		Call c1 = new Call(jim, mik, false);
		c1.pickup();		
		Call c2 = new Call(john, cris, false);
		c2.pickup();
		Call c3 = new Call(carl, luke, false);
		c3.pickup();
		c1.merge(c2);
		c1.merge(c3);
		assertTrue(c1.includes(jim));
	} 
	
	// this test case revealed a fault:
	//for(Enumeration e = connections.elements(); e.hasMoreElements();) {		
    //  result = result || ((Connection)e.nextElement()).connects(c) ;
    //}
	// caused an infinite loop on the test case input
	
	//Covering All-uses-ei	
	public void testIncludesUse1() {
		Customer jim = new Customer("Jim", 650, "3361-1111");
		Customer mik = new Customer("Mik", 500, "3361-1112");
		Customer john = new Customer("John", 500, "3361-1113");
		Customer cris = new Customer("Cris", 500, "3361-1114");
		Call c1 = new Call(jim, mik, false);
		c1.pickup();
		Call c2 = new Call(john, cris, false);
		c2.pickup();
		c1.merge(c2);
		assertFalse(c2.includes(john));
	}
	
	public void testIncludesUse2() {
		Customer jim = new Customer("Jim", 650, "3361-1111");
		Customer mik = new Customer("Mik", 500, "3361-1112");
		Customer john = new Customer("John", 500, "3361-1113");
		Customer cris = new Customer("Cris", 500, "3361-1114");
		Customer carl = new Customer("Carl", 500, "3361-1115");
		Customer luke = new Customer("Luke", 500, "3361-1116");
	
		Call c1 = new Call(jim, mik, false);
		c1.pickup();
		Call c2 = new Call(john, cris, false);
		c2.pickup();
		Call c3 = new Call(carl, luke, false);
		c1.merge(c2);
		c1.merge(c3);
		assertTrue(c1.includes(luke));
	}
}
