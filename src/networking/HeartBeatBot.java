/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package networking;

import java.lang.Runnable;
import arduino.ArdLunaInterface;
import java.awt.event.*;
import javax.swing.Timer;

/**
 *
 * @author Zachary Duve (zas4)
 */
public class HeartBeatBot implements Runnable, ActionListener
{
    protected int maxMilTimeOut = 2000;
    protected Thread heartBeater;
    protected Timer eStopTimer;
    protected UDPConnection connection;
    protected ArdLunaInterface ardLunaInter;
    
    public HeartBeatBot( int maxMTimeOut, UDPConnection conn , ArdLunaInterface aLI )
    {
        maxMilTimeOut = maxMTimeOut;
        connection = conn;
        ardLunaInter = aLI;
        eStopTimer = new Timer( maxMilTimeOut, this );
        eStopTimer.setRepeats( true );
        heartBeater = new Thread( this );
        
        
        eStopTimer.start( );
        heartBeater.start( );
    }
    
    public void run( )
    {
        while( true )
        {
            
            byte[ ] data = new byte[ 1 ];
            connection.receive( data );
            //System.out.println( "heart data: " + data.length );

            if( data[ 0 ] == HeartBeatUtil.HEART_TO_BOT_MASK )
            {
                eStopTimer.restart( );
                System.out.println( "Heart Recieved" );
                
                ardLunaInter.setNetConnected( (byte) ardLunaInter.ON );

                data[ 0 ] = HeartBeatUtil.HEART_FROM_BOT_MASK;

                connection.send( data ); 
            }

        }
    }
    
    public void actionPerformed( ActionEvent ae )
    {
        System.out.println( "ESTOP ENGAGED " + System.currentTimeMillis( ) );
        
        ardLunaInter.emergencyStop( );
        ardLunaInter.setNetConnected( (byte) ardLunaInter.OFF );
    }
}
