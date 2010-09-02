/*
 * Copyright (c) 2005 Nicholas Lesiecki, Ramnivas Laddad, and New Aspects of Software. All rights reserved.
 */
package billing;

/**
 * 
 * @author Ron Bodkin
 */
public aspect AccountSuspension {
    private boolean Account.suspended = false;
    
    public boolean Account.isSuspended() {
        return suspended;
    }
    
    after(Account account) returning: set(int Account.owed) && this(account) {
        account.suspended = 
        	(account.getOwed() > account.getCreditLimit());
    }
    
    before() : BillingPolicy.topLevelUseTitle() {
        User user = (User)Session.instance().getValue("currentUser");
        if (user.getAccount().isSuspended()) {
            throw new SuspendedAccountException();
        }        
    }
}
