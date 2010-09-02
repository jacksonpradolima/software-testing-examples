/*
 * Copyright (c) 2005 Nicholas Lesiecki, Ramnivas Laddad, and New Aspects of Software. All rights reserved.
 */
package repository.service.delegate;

import java.rmi.RemoteException;
import model.Song;
import repository.service.RepositoryService;
import junit.framework.TestCase;

/**
 * @author Ramnivas Laddad (ramnivas@aspectivity.com)
 */
public class RepositoryDelegateTest extends TestCase {
	private RepositoryDelegate testDelegate;
	
	static final Song TEST_SONG1 = new Song("test song1");
	static final Song TEST_SONG2 = new Song("test song2");	
	
	protected void setUp() throws Exception {
		super.setUp();
		testDelegate = RepositoryDelegate.instance();
		testDelegate.addSong(new Long(1), TEST_SONG1);		
		testDelegate.addSong(new Long(2), TEST_SONG2);		
	}
	
	public void testGetSong() throws RepositoryException {
		Song testSong1 = testDelegate.getSong(new Long(1));
		assertEquals(TEST_SONG1, testSong1);

		Song testSong2 = testDelegate.getSong(new Long(2));
		assertEquals(TEST_SONG2, testSong2);
	}

	public void testGetSongRemoteException() {
		FaultInjector.aspectOf().injectFault = true;
		
		try {
			testDelegate.getSong(new Long(1));
		} catch (RepositoryException ex) {
			assertEquals(RemoteException.class, ex.getCause().getClass());
		}
	}
	
	public void testAddSong() throws RepositoryException {
		Long addId = new Long(3);
		Song testSong3 = new Song("test song 3");
		testDelegate.addSong(addId, testSong3);
		
		assertEquals(testSong3, testDelegate.getSong(addId));
	}

	public void testAddSongRemoteException() {
		FaultInjector.aspectOf().injectFault = true;
		
		try {
			testDelegate.addSong(new Long(1), new Song("doesn't matter"));
		} catch (RepositoryException ex) {
			assertEquals(ex.getCause().getClass(), RemoteException.class);
		}
	}

	public void testRemoveSong() throws RepositoryException {
		Long removalId = new Long(1);
		testDelegate.removeSong(removalId);
		
		assertEquals(null, testDelegate.getSong(removalId));
	}

	public void testRemoveSongRemoteException() {
		FaultInjector.aspectOf().injectFault = true;
		
		try {
			testDelegate.removeSong(new Long(1));
		} catch (RepositoryException ex) {
			assertEquals(ex.getCause().getClass(), RemoteException.class);
		}
	}

	private static aspect FaultInjector {
		boolean injectFault = false;
		
        before() throws RemoteException :
            call(* RepositoryService.*(..) throws RemoteException) 
              && cflow(execution(* RepositoryDelegateTest.*(..)))
              && if(aspectOf().injectFault) {
			injectFault = false;
			throw new RemoteException("Simulated fault");
		}
	}
}
