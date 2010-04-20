package bst;
/**
 * Exception class for failed finds/removes in search
 * trees, hash tables, and list and tree iterators.
 * @author Mark Allen Weiss
 */
public class ItemNotFound extends RuntimeException
{
    /**
     * Construct this exception object.
     * @param message the error message.
     */
    public ItemNotFound( String message )
    {
        super( message );
    }
}
