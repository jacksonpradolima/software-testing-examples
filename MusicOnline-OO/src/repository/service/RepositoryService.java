/*
 * Copyright (c) 2005 Nicholas Lesiecki, Ramnivas Laddad, and New Aspects of Software. All rights reserved.
 */
package repository.service;

import java.rmi.RemoteException;

import model.Song;

/**
 * RMI interface for repository service access.
 * 
 * @author Ramnivas Laddad (ramnivas@aspectivity.com)
 */
public interface RepositoryService {
	public Song getSong(Long id) throws RemoteException;
	
	public void addSong(Long id, Song song) throws RemoteException;
	
	public void removeSong(Long id) throws RemoteException;
}
