 

package test;

import java.net.InetAddress;
import networking.HeartBeatController;
import networking.HeartBeatUtil;
import networking.UDPConnection;

/**
 *
 * @author Zachary Duve (zas4)
 */
public class HeartContTest 
{
    public HeartContTest( )
    {
        UDPConnection conn = new UDPConnection( );
        
        try
        {
            conn.setAddress( InetAddress.getByName( "10.0.1.21" ) );
            conn.setPort( 3005 );
        }
        catch( Exception e )
        {
            e.printStackTrace( );
        }
        
        HeartBeatController hBeat = new HeartBeatController( HeartBeatUtil.HEART_DELAY, conn );
        
        
        while( true )
        {
            
        }
    }
    
    public static void main( String [ ] args )
    {
        new HeartContTest( );
    }
}
