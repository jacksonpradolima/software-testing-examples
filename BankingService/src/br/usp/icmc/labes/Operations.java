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
import java.util.Iterator;

public class Operations {
	
	static int getAccount(ArrayList<BankAccount> accounts, int id) {
		
		System.out.println(accounts.size());
		int ret = -1;
		int i = 0;
		for (Iterator<BankAccount> iterator = accounts.iterator(); iterator.hasNext(); i++) {
			BankAccount bankAccount = (BankAccount) iterator.next();
			if(bankAccount.getId() == id) {
				ret = i;
				break;
			}
		}
		return ret;
	}
	
}
