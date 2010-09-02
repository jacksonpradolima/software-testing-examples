/*
 * Copyright (c) 2005 Nicholas Lesiecki, Ramnivas Laddad, and New Aspects of Software. All rights reserved.
 */
package billing;

/**
 * 
 * @author Ron Bodkin
 */
public class Account {
    int owed = 0;
    int creditLimit = 4;
    
    public void bill(int amount) {
        owed += amount;
    }
    
    public int getOwed() {
        return owed;
    }
    
    public void pay(int amount) {
        owed -= amount;
    }
    
    public int getCreditLimit() {
        return creditLimit;
    }
}
