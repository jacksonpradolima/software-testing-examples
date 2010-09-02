/*
 * Copyright (c) 2005 Nicholas Lesiecki, Ramnivas Laddad, and New Aspects of Software. All rights reserved.
 */
package repository.service.delegate;

import java.rmi.RemoteException;

import model.Song;
import repository.service.RepositoryService;
import repository.service.impl.RepositoryServiceImpl;

/**
 * Client-side delegate to access possibly remote service. 
 * 
 * @author Ramnivas Laddad (ramnivas@aspectivity.com)
 */
public class RepositoryDelegate {
	private RepositoryService service = new RepositoryServiceImpl();
	
	private static RepositoryDelegate instance = new RepositoryDelegate();

	public static RepositoryDelegate instance() {
		return instance;
	}
	
	private RepositoryDelegate() {
	}
	
	public Song getSong(Long id) {
		try {
			return service.getSong(id);
		} catch (RemoteException ex) {
			throw new RepositoryException(ex);
		}
	}
	
	public void addSong(Long id, Song song) {
		try {
			service.addSong(id, song);
		} catch (RemoteException ex) {
			throw new RepositoryException(ex);
		}
	}
	
	public void removeSong(Long id) {
		try {
			service.removeSong(id);
		} catch (RemoteException ex) {
			throw new RepositoryException(ex);
		}
	}
}
