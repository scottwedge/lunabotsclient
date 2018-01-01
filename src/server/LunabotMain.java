/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package server;

import networking.*;
import arduino.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
/**
 *
 * @author Zachary Duve (zas4)
 */
public class LunabotMain 
{
    
    private final int MAX_MESS_SIZE = 1; 
    private UDPConnection connection;
    private UDPConnection heartConnection;
    private NetworkInterface netInter;
    private ArdLunaInterface ardLunaInter;
    private HeartBeatBot heart;
    
    public LunabotMain( )
    {
        
        connection = new UDPConnection( 3004 );
     
        connection.setAutoReturn( true );//The connection will send packets to last recieved client
        
        netInter = new SingleByte( );
        
        ardLunaInter = new ArduinoCommandV2(  );
        //ardLunaInter = new ArduinoCommandTest( );
        
        heartConnection = new UDPConnection( 3005 );
        
        heart = new HeartBeatBot( HeartBeatUtil.HEART_DELAY * 2, heartConnection, ardLunaInter );
        
        netInter.addArdLunaInterface( ardLunaInter );
        netInter.addUDPConnection( connection );
        
        while( true )
        {
            byte [] data = new byte[ MAX_MESS_SIZE ];
            
            connection.receive( data );
            
            if( netInter.canProcessCommand( data ) )
            {
                netInter.exeCommand( data );
            }
        }
    }
    
    public static void main( String [] args )
    {
        new LunabotMain( );
    }
}
