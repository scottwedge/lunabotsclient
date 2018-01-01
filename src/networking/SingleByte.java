/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package networking;

import arduino.ArdLunaInterface;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Zachary Duve (zas4)
 */
public class SingleByte implements NetworkInterface
{
    private ArdLunaInterface arduino;
    private UDPConnection connection;
    
    //---------------------MASKS----------------------------------
    public final byte COMMAND_MASK = (byte) 0xE0;
    
    public final byte ESTOP_MASK = 0x00;
    public final byte LEFT_MASK =  0x20;
    public final byte RIGHT_MASK = 0x40;
    public final byte ARM_MASK = (byte) 0x60; //Netbeans has issues?
    public final byte BUCKET_MASK = (byte) 0x80; //Netbeans has issues?
    
    //-----UTIL MASKS-------
    public final byte DIR_MASK = 0x10;
    public final byte SPEED_MASK = 0x0F;
    public final byte ACT_STATE_MASK = 0x03;
    
    //-----Misc Commands--------
    public final byte MISC_COMMAND_MASK = (byte) 0xE0;
    public final byte AUTO_DRIVE_FORWARD = (byte) 0xE1;
    public final byte AUTO_DRIVE_LEFT = (byte) 0xE6;
    public final byte AUTO_DRIVE_RIGHT = (byte) 0xE7;
    public final byte AUTO_DRIVE_BACKWARD = (byte) 0xEA;
    public final byte OVERRIDE_COLLISION_DETECTION_MASK = (byte) 0xE8;
    public final byte OVERRIDE_ARM_SENSORS_MASK = (byte) 0xE9;
    
    public final byte MINING_POSITION_MASK = (byte) 0xE3; //command mask to automaticallly 
                                                              //assume optimal mining position
    public final byte AUTODUMP_MASK = (byte) 0xE4; //command to automatically dump our load    
    public final byte AUTO_RAISE_ARM_MASK = (byte) 0xE5; //automatically raise arm while keeping bucket level
    public final byte DATA_REQUEST = (byte) 0xEE;
    
    public SingleByte( )
    {
        
    }

    @Override
    public void addArdLunaInterface(ArdLunaInterface aLI) 
    {
        arduino = aLI;
    }
    
    @Override
    public void addUDPConnection( UDPConnection conn )
    {
        connection = conn;
    }

    @Override
    public void exeCommand( byte[ ] cmd ) 
    {
        if( arduino == null )
        {
            //System.out.println( new String( cmd ) );
            throw new IllegalStateException( "Need to add an ArdLunaInterface\n" );
        }
        
        byte commandHeader = (byte) ( cmd[ 0 ] & COMMAND_MASK );
        
        byte command = cmd[ 0 ];
        
        if( commandHeader == ESTOP_MASK )
        {
            System.out.println( "ESTOP" );
            arduino.emergencyStop( );
        }
        else if( commandHeader == LEFT_MASK )
        {
            System.out.println( "LEFT" );
            arduino.setLeftDrive( (byte) ( ( command & DIR_MASK ) >> 4 ), (byte) ( ( command & SPEED_MASK ) * 16 ) );
        }
        else if( commandHeader == RIGHT_MASK )
        {
            System.out.println( "RIGHT" );// + ( command & SPEED_MASK ) );
            arduino.setRightDrive( (byte) ( ( command & DIR_MASK ) >> 4 ), (byte) ( ( command & SPEED_MASK ) * 16 ) );
        }
        else if( commandHeader == ARM_MASK )
        {
            System.out.println( "ARM" );
            arduino.moveArm( (byte) ( command & ACT_STATE_MASK ) );
        }
        else if( commandHeader == BUCKET_MASK )
        {
            System.out.println( "BUCKET" );
            arduino.moveBucket( (byte) ( command & ACT_STATE_MASK ) );
        }
        else if( commandHeader == MISC_COMMAND_MASK )
        {
            System.out.println( "MISC COMMAND" );
            if( command == AUTO_DRIVE_FORWARD )
            {
                System.out.println( "AUTO DRIVE FORWARD" );
                arduino.sendAutoDriveForward( );
            }
            else if( command == AUTO_DRIVE_LEFT )
            {
                System.out.println( "AUTO DRIVE LEFT" );
                arduino.sendAutoDriveLeft( );
            }
            else if( command == AUTO_DRIVE_RIGHT )
            {
                System.out.println( "AUTO DRIVE RIGHT" );
                arduino.sendAutoDriveRight( );
            }
            else if( command == AUTO_DRIVE_BACKWARD )
            {
                System.out.println( "AUTO DRIVE BACKWARD" );
                arduino.sendAutoDriveBackward( );
            }
            else if( command == OVERRIDE_COLLISION_DETECTION_MASK )
            {
                System.out.println( "OCDM" );
                arduino.toggleBumpers( );
            }
            else if( command == AUTO_RAISE_ARM_MASK)
            {
                System.out.println("auto raise arm");
                arduino.sendSubroutineCommand(command);
            }
            else if (command == AUTODUMP_MASK)
            {
                System.out.println("auto dump");
                arduino.sendSubroutineCommand(command);
            }
            else if (command == MINING_POSITION_MASK)
            {
                System.out.println("auto move to mining position");
                arduino.sendSubroutineCommand(command);
            }
            else if (command == DATA_REQUEST)
            {
                //System.out.println("auto move to mining position");
                arduino.requestData(command);
                
                try 
                {
                    Thread.currentThread().sleep( 200 );
                } catch (InterruptedException ex) 
                {
                    Logger.getLogger(SingleByte.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                String powerUsedString = new String( arduino.readInput( ) );
                
                System.out.println( "Power Used: " + powerUsedString );
                
                File file = new File( "/home/pi/LunaPi/POWER_USAGE.txt" );
        
                try
                {
                    FileOutputStream fOS = new FileOutputStream( file );

                    BufferedOutputStream out = new BufferedOutputStream( fOS );

                    PrintWriter pWriter = new PrintWriter( out );
                    
                    pWriter.print( "Power Used: " + powerUsedString );
                    pWriter.flush( );
                    pWriter.close( );
                }
                catch( Exception e )
                {
                    e.printStackTrace( );
                }
                //double powerUsed = new Double( powerUsedString );
                
                connection.send( powerUsedString.getBytes() );
            }
            
        }
        else
        {
            
        }
    }

    @Override
    public boolean canProcessCommand( byte[ ] cmd) 
    {
        return ( cmd.length == 1 );
    }
}
