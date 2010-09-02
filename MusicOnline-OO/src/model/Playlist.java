/*
 * Copyright (c) 2005 Nicholas Lesiecki, Ramnivas Laddad, and New Aspects of Software. All rights reserved.
 */
package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Ramnivas Laddad (ramnivas@aspectivity.com)
 */
public class Playlist implements Playable {

	private String name;
	private List contained = new ArrayList();
	
	public Playlist(String name) {
		super();
		this.name = name;
	}

	public void add(Playable playable) {
		contained.add(playable);
	}

	public void remove(Playable playable) {
		contained.remove(playable);
	}
	
	public void play() {
		System.out.println("playing album " + getName());
		for(Iterator iter = contained.iterator(); iter.hasNext();) {
			Playable playable = (Playable)iter.next();
			playable.play();
		}
	}

	public String getName() {
		return name;
	}
	
}
