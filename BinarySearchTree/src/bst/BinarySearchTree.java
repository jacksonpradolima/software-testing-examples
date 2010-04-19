package bst;


/**
 * Implements an unbalanced binary search tree.
 * Note that all "matching" is based on the compares method.
 * @author Mark Allen Weiss
 */
public class BinarySearchTree implements SearchTree
{
    /**
     * Construct the tree.
     */
    public BinarySearchTree( )
    {
        root = null;
    }

    /**
     * Insert into the tree.
     * @param x the item to insert.
     * @exception DuplicateItem if an item
     *        that matches x is already in the tree.
     */
    public void insert( Comparable x ) throws DuplicateItem
    {
        root = insert( x, root );
    }

    /**
     * Remove from the tree.
     * @param x the item to remove.
     * @exception ItemNotFound if no item
     *        that matches x can be found in the tree.
     */
    public void remove( Comparable x ) throws ItemNotFound
    {
        root = remove( x, root );
    }

    /**
     * Remove the smallest item from the tree.
     * @exception ItemNotFound if the tree is empty.
     */
    public void removeMin( ) throws ItemNotFound
    {
        root = removeMin( root );
    }

    /**
     * Find the smallest item in the tree.
     * @return smallest item.
     * @exception ItemNotFound if the tree is empty.
     */
    public Comparable findMin( ) throws ItemNotFound
    {
        return findMin( root ).element;
    }

    /**
     * Find the largest item in the tree.
     * @return the largest item.
     * @exception ItemNotFound if tree is empty.
     */
    public Comparable findMax( ) throws ItemNotFound
    {
        return findMax( root ).element;
    }

    /**
     * Find an item in the tree.
     * @param x the item to search for.
     * @return the matching item.
     * @exception ItemNotFound if no item
     *        that matches x can be found in the tree.
     */
    public Comparable find( Comparable x ) throws ItemNotFound
    {
        return find( x, root ).element;
    }

    /**
     * Make the tree logically empty.
     */
    public void makeEmpty( )
    {
        root = null;
    }

    /**
     * Test if the tree is logically empty.
     * @return true if empty, false otherwise.
     */
    public boolean isEmpty( )
    {
        return root == null;
    }

    /**
     * Print the tree contents in sorted order.
     */
    public void printTree( )
    {
        if( root == null )
            System.out.println( "Empty tree" );
        else
            printTree( root );
    }

    /**
     * Internal method to insert into a subtree.
     * @param x the item to insert.
     * @param t the node that roots the tree.
     * @return the new root.
     * @exception DuplicateItem if item that
     *        matches x is already in the subtree rooted at t.
     */
    protected BinaryNode insert( Comparable x, BinaryNode t ) throws DuplicateItem
    {
        if( t == null )
            t = new BinaryNode( x, null, null );
        else if( x.compares( t.element ) < 0 )
            t.left = insert( x, t.left );
        else if( x.compares( t.element ) > 0 )
            t.right = insert( x, t.right );
        else
            throw new DuplicateItem( "SearchTree insert" );
        return t;
    }

    /**
     * Internal method to remove from a subtree.
     * @param x the item to remove.
     * @param t the node that roots the tree.
     * @return the new root.
     * @exception ItemNotFound no item that
     *        matches x is in the subtree rooted at t.
     */
    protected BinaryNode remove( Comparable x, BinaryNode t ) throws ItemNotFound
    {
        if( t == null )
            throw new ItemNotFound( "SearchTree remove" );
        if( x.compares( t.element ) < 0 )
            t.left = remove( x, t.left );
        else if( x.compares( t.element ) > 0 )
            t.right = remove( x, t.right );
        else if( t.left != null && t.right != null ) // Two children
        {
            t.element = findMin( t.right ).element;
            t.right = removeMin( t.right );
        }
        else
            t = ( t.left != null ) ? t.left : t.right;
        return t;
    }

    /**
     * Internal method to remove the smallest item from a subtree.
     * @param t the node that roots the tree.
     * @return the new root.
     * @exception ItemNotFound the subtree is empty.
     */
    protected BinaryNode removeMin( BinaryNode t ) throws ItemNotFound
    {
        if( t == null )
            throw new ItemNotFound( "SearchTree removeMin" );
        if( t.left != null )
            t.left = removeMin( t.left );
        else
            t = t.right;
        return t;
    }

    /**
     * Internal method to find the smallest item in a subtree.
     * @param t the node that roots the tree.
     * @return node containing the smallest item.
     * @exception ItemNotFound the subtree is empty.
     */
    protected BinaryNode findMin( BinaryNode t ) throws ItemNotFound
    {
        if( t == null )
            throw new ItemNotFound( "SearchTree findMin" );

        while( t.left != null )
            t = t.left;
        return t;
    }

    /**
     * Internal method to find the largest item in a subtree.
     * @param t the node that roots the tree.
     * @return node containing the largest item.
     * @exception ItemNotFound the subtree is empty.
     */
    protected BinaryNode findMax( BinaryNode t ) throws ItemNotFound
    {
        if( t == null )
            throw new ItemNotFound( "SearchTree findMax" );

        while( t.right != null )
            t = t.right;
        return t;
    }

    /**
     * Internal method to find an item in a subtree.
     * @param x is item to search for.
     * @param t the node that roots the tree.
     * @return node containing the matched item.
     * @exception ItemNotFound the
     *               item is not in the subtree.
     */
    protected BinaryNode find( Comparable x, BinaryNode t ) throws ItemNotFound
    {
        while( t != null )
            if( x.compares( t.element ) < 0 )
                t = t.left;
            else if( x.compares( t.element ) > 0 )
                t = t.right;
            else
                return t;    // Match

        throw new ItemNotFound( "SearchTree find" );
    }

    /**
     * Internal method to print a subtree in sorted order.
     * @param t the node that roots the tree.
     */
    protected void printTree( BinaryNode t )
    {
        if( t != null )
        {
            printTree( t.left );
            System.out.println( t.element.toString( ) );
            printTree( t.right );
        }
    }

      /** The tree root. */
    protected BinaryNode root;


        // Test program; should print min and max and nothing else
    public static void main( String [ ] args )
    {
        SearchTree t = new BinarySearchTree( );
        final int NUMS = 4000;
        final int GAP  =   37;

        System.out.println( "Checking... (no more output means success)" );

        try
        {
            for( int i = GAP; i != 0; i = ( i + GAP ) % NUMS )
                t.insert( new MyInteger( i ) );

            for( int i = 1; i < NUMS; i+= 2 )
                t.remove( new MyInteger( i ) );

            if( NUMS < 40 )
                t.printTree( );
            if( ((MyInteger)(t.findMin( ))).intValue( ) != 2 ||
                ((MyInteger)(t.findMax( ))).intValue( ) != NUMS - 2 )
                System.out.println( "FindMin or FindMax error!" );

            for( int i = 2; i < NUMS; i+=2 )
                 t.find( new MyInteger( i ) );

            for( int i = 1; i < NUMS; i+=2 )
            {
                try
                  { System.out.println( "OOPS!!! " + t.find( new MyInteger( i ) ) ); }
                catch( ItemNotFound e )
                  { }
            }
        }
        catch( DuplicateItem e )
          { System.out.println( e ); }
        catch( ItemNotFound e )
          { System.out.println( e ); }
    }

}