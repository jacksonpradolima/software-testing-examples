/*
 * Copyright (c) 2005 Nicholas Lesiecki, Ramnivas Laddad, and New Aspects of Software. All rights reserved.
 */
package billing;

import model.*;

/**
 * @author Nicholas Lesiecki, Ramnivas Laddad, Ron Bodkin
 */
public aspect BillingPolicy {
	public pointcut useTitle() : 
        execution(* Playable.play(..)) || execution(* Song.showLyrics(..));
	
	public pointcut topLevelUseTitle(): useTitle() && !cflowbelow(useTitle());
	
	after(Playable playable) returning : topLevelUseTitle() && this(playable) {
		System.out.println(generateCharge(playable));
	}
	
	private String generateCharge(Playable playable) {
        User user = (User)Session.instance().getValue("currentUser");
        int amount = playable.getName().length();
        user.getAccount().bill(amount);
		return "Charge: " + user + " " + amount;
	}
}
