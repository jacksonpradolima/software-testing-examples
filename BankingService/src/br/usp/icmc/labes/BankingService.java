/**
 * 
 * Banking Service
 * Implemented by Andre T. Endo (andreendo@gmail.com)
 * 
 * Software Engineering Laboratory - LABES
 * Institute of Mathematical and Computer Sciences
 * University of Sao Paulo - USP
 * Sao Carlos, SP, Brazil
 *        
 */

package br.usp.icmc.labes;

import java.util.ArrayList;
import org.apache.axis2.context.ServiceContext;

public class BankingService {
	
	private ArrayList<BankAccount> accounts = null;
	private int number = 0;
	
	public BankingService() {
		//temporary method - only to test
		accounts = new ArrayList<BankAccount>();
		number = 0;
	}
	
	public void init(ServiceContext serviceContext) {  
		System.out.println("INIT: Banking Service");
		
		accounts = new ArrayList<BankAccount>();
		number = 0;
	}
	
	public void destroy(ServiceContext serviceContext) {
		System.out.println("DESTROY: Banking Service");
	}
	
	public int openAccount(double init) 
		throws Exception
	{
		if(init <= 0)
			throw new Exception("Initial value must be greater than 0.");
		
		int id = ++number;
		BankAccount ac = new BankAccount();
		ac.setId(id);
		ac.setInitialValue(init);
		accounts.add(ac);
		
		return id;
	}

	public String deposit(int id, double v) 
		throws Exception
	{
		int index = Operations.getAccount(accounts, id);
		
		if(index == -1)
			throw new Exception("Account does not exist.");
		
		if(v <= 0)
			throw new Exception("Value must be greater than 0.");	
		
		BankAccount ac = accounts.get(index);
		ac.deposit(v);
		
		return "ResultOK";
	}

	public String withdraw(int id, double v) 
		throws Exception
	{
		int index = Operations.getAccount(accounts, id);
		
		if(index == -1)
			throw new Exception("Account does not exist.");
		
		if(v <= 0)
			throw new Exception("Value must be greater than 0.");
		
		BankAccount ac = accounts.get(index);
		
		if(v > ac.getValue())
			throw new Exception("Value must be less or equal than account value.");
		
		ac.withdraw(v);
		
		return "ResultOK";
	}
	
	public String closeAccount(int id) 
		throws Exception
	{
		int index = Operations.getAccount(accounts, id);		

		if(index == -1)
			throw new Exception("Account does not exist.");
		
		accounts.remove(index);
		
		return "ResultOK";
	}
	
}
