/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package testclient;

import net.java.games.input.*;
import networking.*;
import java.net.*;

/**
 *
 * @author Zachary Duve (zas4)
 */
public class TestClient 
{
    public static void main( String [ ] args )
    {
        //UDPConnection connection = new UDPConnection( 0 );
        
        Controller [ ] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
    
        Controller theOne = null;
        
        System.out.println( "Number of controllers connected: " + controllers.length );
        
        for( int i = 0; i < controllers.length; i++ )
        {
            System.out.println( "Controller " + i + " type: " + controllers[ i ].getType( ) );
            if( controllers[ i ].getType() == Controller.Type.GAMEPAD )
            {
                theOne = controllers[ i ];
                break;
            }
        }
        
        if( theOne != null )
        {
            System.out.println( theOne.getName( ) ); 
            System.out.println( "Number of Components: " + theOne.getComponents().length );
            System.out.println( "Number of Rumblers: " + theOne.getRumblers().length );
            Component [ ] components = theOne.getComponents();
            try
            {
                while( true )
                {
                    theOne.poll();
                    
                    
                    for( int i = 0; i < components.length; i++ )
                    {
                        int button = i;
                        //System.out.println( components[ 8 ]);
                        System.out.println( components[ button ].getName( ) + " " + components[ button ].isAnalog( ) + " " + components[ button ].getPollData( ) );
                    }
                    /*
                    
                    System.out.println( "y " + components[ 12 ].getPollData( ) );
                    System.out.println( "ry " + components[ 15 ].getPollData( ) );
                    
                    float yAxisVal = components[ 12 ].getPollData( );
                    byte direction = 0x10;
                    
                    if( yAxisVal < 0 )
                    {
                        yAxisVal *= -1.0f;
                        direction = 0x00;
                    }
                    
                    byte motorSpeed = (byte) ( 8 * yAxisVal );
                    
                    byte [ ] data = { 0x20 }; //leftmotor
                    
                    data[ 0 ] = (byte) ( data[ 0 ] | direction | motorSpeed );
                    
                    DatagramPacket pack = new DatagramPacket( data, data.length );

                    byte [ ] pidress = { (byte) 192, (byte) 168, (byte) 1, (byte) 18 };
                    
                    //pack.setAddress( InetAddress.getByAddress( pidress ) );
                    
                    pack.setAddress( InetAddress.getLocalHost( ) );
                    
                    pack.setPort( 3004 );

                    connection.send( pack );
                    
                    float rYAxisVal = components[ 15 ].getPollData( );
                    byte rDirection = 0x10;
                    
                    if( rYAxisVal < 0 )
                    {
                        rYAxisVal *= -1.0f;
                        rDirection = 0x00;
                    }
                    
                    byte rMotorSpeed = (byte) ( 8 * rYAxisVal );
                    
                    byte [ ] rData = { 0x40 }; //leftmotor
                    
                    rData[ 0 ] = (byte) ( rData[ 0 ] | rDirection | rMotorSpeed );
                    
                    pack = new DatagramPacket( rData, rData.length );
                    
                    //pack.setAddress( InetAddress.getByAddress( pidress ) );
                    
                    pack.setAddress( InetAddress.getLocalHost( ) );
                    
                    pack.setPort( 3004 );

                    connection.send( pack );
                    
                    Thread.sleep( 100 );
                    */
                }
            }
            catch( Exception e )
            {
                
            }
        }
    }
}
