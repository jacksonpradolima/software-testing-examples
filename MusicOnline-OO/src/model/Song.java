/*
 * Copyright (c) 2005 Nicholas Lesiecki, Ramnivas Laddad, and New Aspects of Software. All rights reserved.
 */
package model;

/**
 * @author Ramnivas Laddad (ramnivas@aspectivity.com)
 */
public class Song implements Playable {
	private String name;

	public Song(String name) {
		super();
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void play() {
		System.out.println("Playing song " + getName());
	}
	
	public void showLyrics(){
		System.out.println("Displaying lyrics for " + getName());
	}
	
	public boolean equals(Object o){
		Song other = (Song) o;
		return this.name.equals(other.name);
	}
	
	public int hashCode() {
		return name.hashCode();
	}	
}
