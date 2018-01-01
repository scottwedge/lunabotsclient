/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.net.InetAddress;
import networking.MulticastConnection;
import networking.UDPConnection;

/**
 *
 * @author Zachary Duve (zas4)
 */
public class MultiClientTest 
{
    private UDPConnection connection;
    
    public MultiClientTest( )
    {
        //connection = new UDPConnection( 3004 );
        connection = new MulticastConnection( 3004 );
        connection.setPort( 3004 );
        byte [ ] groupAddress = { (byte) 224, (byte) 0, (byte) 30, (byte) 0 };
        
        try
        {
            connection.setAddress( InetAddress.getByAddress( groupAddress ) );
        }
        catch( Exception e )
        {
            e.printStackTrace( );
        }
            
        while( true )
        {
            byte [] data = new byte[ 256 ];
            
            try
            {
                connection.receive( data );
                
                System.out.print( new String( data ) );
            }
            catch( Exception e )
            {
                
            }
        }
    }
    
    public static void main( String [ ] args )
    {
        new MultiClientTest( );
    }
}
