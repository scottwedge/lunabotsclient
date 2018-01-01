

package test;
import controller.ControllerMain;
import controller.NewGui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import javax.swing.Timer;
import networking.*;

/**
 *
 * @author zacharyduve
 */
public class TestNetSingleBot implements ActionListener
{
    UDPConnection conn;
    byte [ ] commands = { (byte) 0x20 };
    public TestNetSingleBot( )
    {
        conn = new UDPConnection( );
        conn.setPort( 3004 );
        
        UDPConnection heartConn = new UDPConnection( );
        heartConn.setPort( 3005 );
        
        try
        {
            //String address = "192.168.1.30";
            
            //conn.setAddress( InetAddress.getByName( address ) );
            //heartConn.setAddress( InetAddress.getByName( address ) );
        }
        catch( Exception e )
        {
            e.printStackTrace( );
        }
        
        HeartBeatController hBC = new HeartBeatController( 2000, heartConn );
        
        Timer commandTimer = new Timer( 1000, this );
        commandTimer.setRepeats( true );
        
        commandTimer.start( );
        
        while( true )
        {
            
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        new TestNetSingleBot( );
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        conn.send( commands );
        commands[ 0 ] = (byte) ( commands[ 0 ] + 0x01 );
        if( commands[ 0 ] > 0x5F )
        {
            commands[ 0 ] = 0x20;
        }
    }
    
}
