/*
 * Copyright (c) 2005 Nicholas Lesiecki, Ramnivas Laddad, and New Aspects of Software. All rights reserved.
 */
package billing;

import java.util.HashMap;
import java.util.Map;

/**
 * Simulating HttpSession-like idea.
 * 
 * @author Ramnivas Laddad (ramnivas@aspectivity.com)
 */
public class Session {
	private Map attributes = new HashMap();
	private static Session instance = new Session();
	
	public static Session instance() {
		return instance;
	}
	
	public void putValue(String key, Object value) {
		attributes.put(key, value);
	}
	
	public void removeValue(String key) {
		attributes.remove(key);
	}
	
	public Object getValue(String key) {
		return attributes.get(key);
	}
}
