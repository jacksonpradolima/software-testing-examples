package PointShadow;

import junit.framework.*;

/**
 * A sample test case, testing <code>BinarySearchTreeWithRank</code>.
 */
public class PSTestCase extends TestCase {
        public Point p;        

        public PSTestCase( String str ) {
                super( str );
        }

        public PSTestCase(  ) {
                this( "" );
        }

        public void setUp() {
        }

		public void testCase1() {		      
      	  p = new Point(1,1);
          p.setX(2);
          p.setY(2);
        }	
}