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
		return service.getSong(id); 
	}
	
	public void addSong(Long id, Song song) {
		service.addSong(id, song);
	}
	
	public void removeSong(Long id) {
		service.removeSong(id);
	}
	
	private static aspect ExceptionConversion {
		pointcut conversionOps() 
		    : call(* RepositoryService.*(..) throws RemoteException)
			  && within(RepositoryDelegate);
		
		declare soft: RemoteException: conversionOps();
		
		after() throwing(RemoteException ex) : conversionOps() {
			throw new RepositoryException(ex);
		}
	}
}
