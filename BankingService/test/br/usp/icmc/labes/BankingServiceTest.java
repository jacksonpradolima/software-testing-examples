package br.usp.icmc.labes;

import junit.framework.TestCase;

public class BankingServiceTest extends TestCase {
	private BankingService BS = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		BS = new BankingService();		
	}

	public void test1() throws java.lang.Exception {
		
		int accountid = BS.openAccount(20.0);
    	//assertNotNull(accountid);
    	assertTrue(accountid > 0);
    	        
        String res = BS.deposit(accountid, 20.0);
        System.out.println("Deposit: " + res);
        assertNotNull(res);
        assertEquals("ResultOK", res);
        
    	res = BS.withdraw(accountid, 15.0);
        System.out.println("Withdraw: " + res);
        assertNotNull(res);
        assertEquals("ResultOK", res);
        
        res = BS.closeAccount(accountid);
        System.out.println("CloseAccount: " + res);
        assertNotNull(res);
        assertEquals("ResultOK", res);   
    }	
	
}
