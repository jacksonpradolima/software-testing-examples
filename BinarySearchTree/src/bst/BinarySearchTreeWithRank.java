package bst;


/**
 * Implements a binary search tree with a findKth method.
 * Note that all "matching" is based on the compares method.
 * @author Mark Allen Weiss
 */
public class BinarySearchTreeWithRank extends BinarySearchTree
{
    /**
     * Find the kth smallest item in the tree.
     * @param k the desired rank (1 is the smallest item).
     * @return the kth smallest item in the tree.
     * @exception ItemNotFound if k is less
     *     than 1 or more than the size of the tree.
     */
    public Comparable findKth( int k ) throws ItemNotFound
    {
        return findKth( k, root ).element;
    }


    /**
     * Internal method to insert into a subtree, adjusting
     *     Size fields as appropriate.
     * @param x the item to insert.
     * @param t the node that roots the tree.
     * @return the new root.
     * @exception DuplicateItem if item that
     *     matches x is already in the subtree rooted at t.
     */
    protected BinaryNode insert( Comparable x, BinaryNode t ) throws DuplicateItem
    {
        if( t == null )
            return new BinaryNode( x, null, null );
        else if( x.compares( t.element ) < 0 )
            t.left = insert( x, t.left );
        else if( x.compares( t.element ) > 0 )
            t.right = insert( x, t.right );
        else
            throw new DuplicateItem( "BSTWithRank insert" );

        t.size++;
        return t;
    }

    /**
     * Internal method to remove from a subtree, adjusting
     *    Size fields as appropriate.
     * @param x the item to remove.
     * @param t the node that roots the tree.
     * @return the new root.
     * @exception ItemNotFound no item that
     *    matches x is in the subtree rooted at t.
     */
    protected BinaryNode remove( Comparable x, BinaryNode t ) throws ItemNotFound
    {
        if( t == null )
            throw new ItemNotFound( "BSTWithRank remove" );
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
            return ( t.left != null ) ? t.left : t.right;
        t.size--;
        return t;
    }

    /**
     * Internal method to remove the smallest item from a subtree,
     *     adjusting Size fields as appropriate.
     * @param t the node that roots the tree.
     * @return the new root.
     * @exception ItemNotFound the subtree is empty.
     */
    protected BinaryNode removeMin( BinaryNode t ) throws ItemNotFound
    {
        if( t == null )
            throw new ItemNotFound( "BSTWithRank removeMin" );
        if( t.left == null )
            return t.right;
        t.left = removeMin( t.left );
        t.size--;
        return t;
    }

    /**
     * Internal method to find kth smallest item in a subtree.
     * @param k the desired rank (1 is the smallest item).
     * @return the node containing the kth smallest item in the subtree.
     * @exception ItemNotFound if k is less
     *     than 1 or more than the size of the subtree.
     */
    protected BinaryNode findKth( int k, BinaryNode t ) throws ItemNotFound
    {
        if( t == null )
            throw new ItemNotFound( "BSTWithRank findKth" );
        int leftSize = ( t.left != null ) ? t.left.size : 0;

        if( k <= leftSize )
            return findKth( k, t.left );
        if( k == leftSize + 1 )
            return t;
        return findKth( k - leftSize - 1, t.right );
    }


        // Test program; should print min and max and nothing else
    public static void main( String [ ] args )
    {
        BinarySearchTreeWithRank t = new BinarySearchTreeWithRank( );
        final int NUMS = 4000;
        final int GAP  =   37;

        System.out.println( "Checking... (no more output means success)" );

        try
        {
            for( int i = GAP; i != 0; i = ( i + GAP ) % NUMS )
                t.insert( new MyInteger( i ) );
            
            //t.insert(new MyInteger(1));

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
            for( int i = 2; i < NUMS; i+= 2 )
            if( ((MyInteger)(t.findKth( i / 2 ))).intValue( ) != i )
                System.out.println( "FindKth error!" );
            
            System.out.println("Min: " + t.findMin());
            System.out.println("Max: " + t.findMax());
        }
        catch( DuplicateItem e )
          { System.out.println( e ); }
        catch( ItemNotFound e )
          { System.out.println( e ); }        
        
    }

}