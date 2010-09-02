/*
 * Copyright (c) 2005 Nicholas Lesiecki, Ramnivas Laddad, and New Aspects of Software. All rights reserved.
 */
package billing;

/**
 * 
 * @author Ron Bodkin
 */
public class SuspendedAccountException extends RuntimeException {

    public SuspendedAccountException() {
        super("account exceeded credit limit");
    }

    public SuspendedAccountException(String description) {
        super(description);
    }

    public SuspendedAccountException(Throwable cause) {
        super(cause);
    }

    public SuspendedAccountException(String description, Throwable cause) {
        super(description, cause);
    }

    private static final long serialVersionUID = 1;
}
