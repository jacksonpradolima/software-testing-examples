/*
 * Copyright (c) 2005 Nicholas Lesiecki, Ramnivas Laddad, and New Aspects of Software. All rights reserved.
 */
package main;

import model.Playlist;
import model.Song;
import repository.service.delegate.RepositoryDelegate;
import repository.service.delegate.RepositoryException;
import billing.Session;
import billing.SuspendedAccountException;
import billing.User;

/**
 * @author Ramnivas Laddad (ramnivas@aspectivity.com)
 */
public class Main {
	
	
	public static void main(String[] args) throws RepositoryException {
		populateRepository();
		
		Session session = Session.instance();
        User user = new User("user1");
		session.putValue("currentUser", user);
		Playlist list = createList();        
		
		list.play();
        System.out.println("total owed = "+user.getAccount().getOwed());
        
        // try again, now past limit
        try {
            list.play();
            System.err.println("why wasn't the account suspended?");            
        } catch (SuspendedAccountException e) {
            System.out.println("overdrawn account suspended!");
        }
	}
	
	private static void populateRepository() throws RepositoryException {
		RepositoryDelegate repository = RepositoryDelegate.instance();
		
		repository.addSong(new Long(1), new Song("Song 1"));
		repository.addSong(new Long(2), new Song("Song 2"));		
	}
	
	private static Playlist createList() throws RepositoryException {
		RepositoryDelegate repository = RepositoryDelegate.instance();
		
		Playlist list = new Playlist("list1");
		list.add(repository.getSong(new Long(1)));
		list.add(repository.getSong(new Long(2)));
		
		return list;
	}
}
