package telecom;

import junit.framework.TestCase;

public class FunctionalTestSet extends TestCase {
	
	//Testing the Customer class
	
	
	
	public void testCall() {
		Customer jim = new Customer("Jim", 650, "3361-1111");
		Customer mik = new Customer("Mik", 650, "3361-1112");
		
		Call c1 = new Call(jim, mik, false);
		
		c1.pickup();
		
		Customer mic = new Customer("Mic", 652, "3361-1121");
		Customer car = new Customer("Car", 650, "3361-1112");
		
		Call c2 = new Call(mic, car, false);
		
		c2.pickup();
		
		c2.hangup();
		
		c1.merge(c2);
		
		System.out.println(c1.isConnected());
	}
	
	public void testCoverCCNodesCallHangup() {
		Customer jim = new Customer("Jim", 650, "3361-1111");
		Customer mik = new Customer("Mik", 650, "3361-1112");
		
		Call c1 = new Call(jim, mik, false);
		
		c1.hangup();
		
		//Vector c = c1.getConnections();
		
		//for(Enumeration e = c.elements(); e.hasMoreElements();) {
		//	if (((Connection)e.nextElement()).getState() != 2)  
		//		fail();
		//}
	}
}
