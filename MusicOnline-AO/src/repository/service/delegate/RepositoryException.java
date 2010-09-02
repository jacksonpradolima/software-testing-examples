/*
 * Copyright (c) 2005 Nicholas Lesiecki, Ramnivas Laddad, and New Aspects of Software. All rights reserved.
 */
package repository.service.delegate;

/**
 * @author Ramnivas Laddad (ramnivas@aspectivity.com)
 */
public class RepositoryException extends RuntimeException {

	/**
	 * @param ex
	 */
	public RepositoryException(Throwable ex) {
		super(ex);
	}
	
    private static final long serialVersionUID = 1;
}
