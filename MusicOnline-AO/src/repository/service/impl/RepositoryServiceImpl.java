/*
 * Copyright (c) 2005 Nicholas Lesiecki, Ramnivas Laddad, and New Aspects of Software. All rights reserved.
 */
package repository.service.impl;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import model.Song;
import repository.service.RepositoryService;

/**
 * @author Ramnivas Laddad (ramnivas@aspectivity.com)
 */
public class RepositoryServiceImpl implements RepositoryService {
	private Map songs = new HashMap(); 
	
	public Song getSong(Long id) throws RemoteException {
		return (Song)songs.get(id);
	}

	public void addSong(Long id, Song song) throws RemoteException {
		songs.put(id, song);
	}

	public void removeSong(Long id) throws RemoteException {
		songs.remove(id);
	}
}
