/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import networking.MulticastConnection;
import networking.UDPConnection;
import java.net.*;

/**
 *
 * @author Zachary Duve (zas4)
 */
public class MultiServerTest 
{
    private UDPConnection connection;
    
    public MultiServerTest( )
    {
        //connection = new MulticastConnection( );
        connection = new UDPConnection( );
        byte [ ] groupAddress = { (byte) 224, (byte) 0, (byte) 30, (byte) 0 };
        
        try
        {
            //connection.setAddress( InetAddress.getLocalHost( ) );
            connection.setAddress( InetAddress.getByAddress( groupAddress ) );
            connection.setPort( 3004 );
        }
        catch( Exception e )
        {
            e.printStackTrace( );
        }
        
        System.out.println( "SERVER STARTED" );
        while( true )
        {
            byte [] data = new byte[ 256 ];
            
            try
            {
                System.in.read( data );
                
                connection.send( data );
                
                //System.out.println( new String( data ) );
            }
            catch( Exception e )
            {
                
            }
        }
    }
    
    public static void main( String [ ] args )
    {
        MultiServerTest mST = new MultiServerTest( );
    }
}
