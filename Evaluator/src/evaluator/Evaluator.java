package evaluator;

import java.util.Stack;
import java.util.StringTokenizer;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * Evaluate infix expressions.
 */
public class Evaluator
{
    private static final int EOL     = 0;
    private static final int VALUE   = 1;
    private static final int OPAREN  = 2;
    private static final int CPAREN  = 3;
    private static final int EXP     = 4;
    private static final int MULT    = 5;
    private static final int DIV     = 6;
    private static final int PLUS    = 7;
    private static final int MINUS   = 8;

    private static class Precedence
    {
        public int inputSymbol;
        public int topOfStack;

        public Precedence( int inSymbol, int topSymbol )
        {
            inputSymbol = inSymbol;
            topOfStack  = topSymbol;
        }
    }

        // PrecTable matches order of Token enumeration
    private static Precedence [ ] precTable = new Precedence[ ]
    {
        new Precedence(   0, -1 ),  // EOL
        new Precedence(   0,  0 ),  // VALUE
        new Precedence( 100,  0 ),  // OPAREN
        new Precedence(   0, 99 ),  // CPAREN
        new Precedence(   6,  5 ),  // EXP
        new Precedence(   3,  4 ),  // MULT
        new Precedence(   3,  4 ),  // DIV
        new Precedence(   1,  2 ),  // PLUS
        new Precedence(   1,  2 )  // MINUS
    };

    private static class Token
    {
        public Token( )
        {
            this( EOL );
        }
        
        public Token( int t )
        {
            this( t, 0 );
        }
        
        public Token( int t, long v )
        {
            type = t;
            value = v;
        }
        
        public int getType( )
        {
            return type;
        }
        
        public long getValue( )
        {
            return value;
        }
        
        private int type = EOL;
        private long value = 0;
    }    

    private static class EvalTokenizer
    {
        public EvalTokenizer( StringTokenizer is )
        {
            str = is;
        }
        
        /**
         * Find the next token, skipping blanks, and return it.
         * For VALUE token, place the processed value in currentValue.
         * Print error message if input is unrecognized.
         */
        public Token getToken( )
        {
            long theValue;           

            if( !str.hasMoreTokens( ) )
                return new Token( );

            String s = str.nextToken( );
            if( s.equals( " " ) ) return getToken( );
            if( s.equals( "^" ) ) return new Token( EXP );
            if( s.equals( "/" ) ) return new Token( DIV );
            if( s.equals( "*" ) ) return new Token( MULT );
            if( s.equals( "(" ) ) return new Token( OPAREN );
            if( s.equals( ")" ) ) return new Token( CPAREN );
            if( s.equals( "+" ) ) return new Token( PLUS );
            if( s.equals( "-" ) ) return new Token( MINUS );

            try
              { theValue = Long.parseLong( s ); }
            catch( NumberFormatException e )
            {
                System.err.println( "Parse error" );
                return new Token( );
            }
            
            return new Token( VALUE, theValue );
        }
        
        private StringTokenizer str;
        
    }
    
    /**
     * Construct an evaluator object.
     * @param s the string containing the expression.
     */
    public Evaluator( String s )
    {
        opStack = new Stack( );
        postfixStack = new Stack( );

        str = new StringTokenizer( s, "+*-/^() ", true );

        opStack.push( new Integer( EOL ) );
    }

        // The only publicly visible routine

    /** 
     * Public routine that performs the evaluation.
     * Examine the  postfix machine to see if a single result is
     * left and if so, return it; otherwise print error.
     * @return the result.
     */
    public long getValue( )
    {
        EvalTokenizer tok = new EvalTokenizer( str );
        Token lastToken;
        
        do
        {
            lastToken = tok.getToken( );
            processToken( lastToken );
        } while( lastToken.getType( ) != EOL );

        if( postfixStack.isEmpty( ) )
        {
            System.err.println( "Missing operand!" );
            return 0;
        }

        long theResult = postFixTopAndPop( );
        if( !postfixStack.isEmpty( ) )
            System.err.println( "Warning: missing operators!" );

        return theResult;
    }


    private Stack opStack;       // Operator stack for conversion
    private Stack postfixStack;  // Stack for postfix machine
    private StringTokenizer str; // StringTokenizer stream

 
    /**
     * Internal method that hides type-casting.
     */
    private long postFixTopAndPop( )
    {
        return ( (Long) ( postfixStack.pop( ) ) ).longValue( );
    }

    /**
     * Another internal method that hides type-casting.
     */
    private int opStackTop( )
    {
        return ( (Integer) ( opStack.peek( ) ) ).intValue( );
    }

    /**
     * After a token is read, use operator precedence parsing
     * algorithm to process it; missing opening parentheses
     * are detected here.
     */
    private void processToken( Token lastToken )
    {
        int topOp;
        int lastType = lastToken.getType( );
      
   
        switch( lastType )
        {
          case VALUE:
            postfixStack.push( new Long( lastToken.getValue( ) ) );
            return;

          case CPAREN:
            while( ( topOp = opStackTop( ) ) != OPAREN && topOp != EOL )
                binaryOp( topOp );
            if( topOp == OPAREN )
                opStack.pop( );  // Get rid of opening parentheseis
            else
                System.err.println( "Missing open parenthesis" );
            break;

          default:    // General operator case
            while( precTable[ lastType ].inputSymbol <=
                   precTable[ topOp = opStackTop( ) ].topOfStack )
                binaryOp( topOp );
            if( lastType != EOL )
                opStack.push( new Integer( lastType ) );
            break;
        }
    }

    /*
     * topAndPop the postfix machine stack; return the result.
     * If the stack is empty, print an error message.
     */
    private long getTop( )
    {
        if ( postfixStack.isEmpty( ) )
        {
            System.err.println( "Missing operand" );
            return 0;
        }
        return postFixTopAndPop( );
    }

    /**
     * Internal routine to compute x^n.
     */
    private static long pow( long x, long n )
    {
        if( x == 0 )
        {
            if( n == 0 )
                System.err.println( "0^0 is undefined" );
            return 0;
        }
        if( n < 0 )
        {
            System.err.println( "Negative exponent" );
            return 0;
        }
        if( n == 0 )
            return 1;
        if( n % 2 == 0 )
            return pow( x * x, n / 2  );
        else
            return x * pow( x, n - 1 );
    }

    /**
     * Process an operator by taking two items off the postfix
     * stack, applying the operator, and pushing the result.
     * Print error if missing closing parenthesis or division by 0.
     */
    private void binaryOp( int topOp )
    {
        if( topOp == OPAREN )
        {
            System.err.println( "Unbalanced parentheses" );
            opStack.pop( );
            return;
        }
        long rhs = getTop( );
        long lhs = getTop( );

        if( topOp == EXP )
            postfixStack.push( new Long( pow( lhs, rhs ) ) );
        else if( topOp == PLUS )
            postfixStack.push( new Long( lhs + rhs ) );
        else if( topOp == MINUS )
            postfixStack.push( new Long( lhs - rhs ) );
        else if( topOp == MULT )
            postfixStack.push( new Long( lhs * rhs ) );
        else if( topOp == DIV )
            if( rhs != 0 )
                postfixStack.push( new Long( lhs / rhs ) );
            else
            {
                System.err.println( "Division by zero" );
                postfixStack.push( new Long( lhs ) );
            }
        opStack.pop( );
    }

    /**
     * Simple main to exercise Evaluator class.
     */
    public static void main( String [ ] args )
    {
        String str;
        BufferedReader in = new BufferedReader( new InputStreamReader( System.in ) );

        try
        {
            System.out.println( "Enter expressions, one per line:" );
            while( ( str = in.readLine( ) ) != null )
            {
                System.out.println( "Read: " + str );
                Evaluator ev = new Evaluator( str );
                System.out.println( ev.getValue( ) );
                System.out.println( "Enter next expression:" );
            }
        }
        catch( IOException e ) { e.printStackTrace( ); }
    }
}