/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package arduino;

import java.io.*;

/**
 *
 * @author Zachary Duve (zas4)
 */
public class InputEater implements Runnable
{
    private InputStream in;
    
    public InputEater( InputStream iStream )
    {
        in = iStream;
        
        Thread t = new Thread( this );
        t.start( );
    }
    
    public void run( )
    {
        int numAvail = 0;
                
        while( true )
        {
            try
            {
                 numAvail = in.available( );
            }
            catch( IOException iOE )
            {
                System.err.println( "IOSTREAM CLOSED" );
            }
            
            if( numAvail > 0 )
            {
                byte [ ] dataIn = new byte [ numAvail ];
                
                try
                {
                     in.read( dataIn );
                     System.out.println( new String( dataIn ) );
                }
                catch( IOException iOE )
                {
                    System.err.println( "IOSTREAM CLOSED" );
                }
            }
            else
            {
                try
                {
                    //Thread.currentThread().sleep( 100 );
                }
                catch( Exception e )
                {
                    e.printStackTrace( );
                }
            }
        }
    }
}
