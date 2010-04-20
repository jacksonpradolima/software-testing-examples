package bst;

/**
 * Exception class for duplicate item errors
 * in search tree insertions.
 * @author Mark Allen Weiss
 */
public class DuplicateItem extends RuntimeException
{
    /**
     * Construct this exception object.
     * @param message the error message.
     */
    public DuplicateItem( String message )
    {
        super( message );
    }
}
