/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package arduino;

import static arduino.ArdLunaInterface.ACTUATOR_CMD;
import static arduino.ArdLunaInterface.AUTO_DRIVE_BACKWARD;
import static arduino.ArdLunaInterface.AUTO_DRIVE_CMD;
import static arduino.ArdLunaInterface.AUTO_DRIVE_FORWARD;
import static arduino.ArdLunaInterface.AUTO_DRIVE_LEFT;
import static arduino.ArdLunaInterface.AUTO_DRIVE_RIGHT;
import static arduino.ArdLunaInterface.CONNECTION_CMD;
import static arduino.ArdLunaInterface.DELTA_SPEED;
import static arduino.ArdLunaInterface.EMERGENCY_STOP_CMD;
import static arduino.ArdLunaInterface.HEARTBEAT_CMD;
import static arduino.ArdLunaInterface.IO_SPEED;
import static arduino.ArdLunaInterface.MOTOR_CMD;
import static arduino.ArdLunaInterface.SPEED_MULT;
import static arduino.ArdLunaInterface.ZERO_SPEED_VALUE;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Zachary Duve (zas4)
 */
public class ArduinoCommandV2 implements ArdLunaInterface
{
    
    protected String arduinoPath = "/dev/ttyACM0";
    //protected String arduinoPath = "/dev/tty.usbmodem411";
    
    //private boolean connected = false;
    
    private BufferedInputStream in;
    protected BufferedOutputStream out;
    protected ArduinoConnection ardConnection;
    /**
     * Creates a connection to the arduino.
     */
    public ArduinoCommandV2( )
    {
        createConnection( );
    }
    
    /**
     * Creates a connection to the arduino by the given path.
     * @param aPath The path to the arduino.
     */
    public ArduinoCommandV2( String aPath )
    {
        arduinoPath = aPath;
        
        createConnection( );
    }
    
    /**
     * Create the connection to the Arduino
     */
    protected void createConnection( )
    {

        try
        {
            ardConnection = new ArduinoConnection( );
            ardConnection.setIOSpeed( IO_SPEED );
            ardConnection.connect( arduinoPath );
            
            in = new BufferedInputStream( ardConnection.getInputStream( ) );
            
            //new InputEater( in );
            
            out = new BufferedOutputStream( ardConnection.getOutputStream( ) );
            
            ///FileOutputStream fOS = new FileOutputStream( file );
            
            ///out = new BufferedOutputStream( fOS );
            
            
            Thread.currentThread().sleep( 1600 );//let the arduino get ready before writing
            System.out.println( "Ready to start talking" ); 
            
        }
        catch( Exception e )
        {
            e.printStackTrace( );
        }
    }
    
    /**
     * Change the path to the arduino and reconnect using the new path.
     * @param nP The new path to the arduino.
     */
    @Override
    public void upDatePath( String nP )
    {
        arduinoPath = nP;
        
        createConnection( );
    }
    
    /**
     * Attaches the command header and writes the given command to the 
     * arduino.
     * @param data The command to be sent to the arduino.
     */
    private synchronized void sendCommand( byte[ ] data )
    {
        byte [ ] command = new byte[ data.length + 2 ];
        
        command[ 0 ] = (byte) 0xFF;
        command[ 1 ] = (byte) 0xFE;
        
        for( int i = 2; i < command.length; i++ )
        {
            command[ i ] = data[ i - 2 ];
        }
        
        try
        {
            out.write( command );
            out.flush( );
        }
        catch( IOException iOE )
        {
            iOE.printStackTrace( );
        }
    }
    
    /**
     * STOPS THE MOVEMENT OF ALL MOTORS AND ACTUATORS.
     */
    @Override
    public void emergencyStop( )
    {
        byte [ ] estop = new byte[ 1 ];
        
        estop[ 0 ] = EMERGENCY_STOP_CMD;
        
        this.sendCommand( estop );
    }
    
    /**
     * Generic setting the speed and direction of a drive motor for the given
     * motor ID.
     * @param mID The motor to be controlled.
     * @param dir The new direction for the motor.
     * @param speed The new speed for the motor.
     */
    @Override
    public void setDrive( byte mID, byte dir, byte speed )
    {
        byte [ ] drive = new byte [ 4 ];
        drive[ 0 ] = MOTOR_CMD;
        drive[ 1 ] = mID;
        drive[ 2 ] = dir;
        drive[ 3 ] = speed;
        
        System.out.println( "MOTOR| mID: " + mID + " dir: " + dir + " speed: " + (int) speed );
        
        this.sendCommand( drive );
    }
    
    /**
     * Set the speed and direction for the left motor.
     * @param dir The left motors new direction.
     * @param speed The left motors new speed.
     */
    @Override
    public void setLeftDrive( byte dir, byte speed )
    {
        this.setDrive( LEFT, dir, speed );
    }
    
    /**
     * Set the speed and direction for the right motor.
     * @param dir The right motors new direction.
     * @param speed The right motors new speed.
     */
    @Override
    public void setRightDrive( byte dir, byte speed )
    {
        this.setDrive( RIGHT, dir, speed );
    }
    
    /**
     * Generic activation of an actuator.
     * @param aID The actuator's id.
     * @param state The actuator's direction.
     */
    @Override
    public void moveActuator( byte aID, byte state )
    {
        byte [ ] actu = new byte[ 4 ];
        actu[ 0 ] = ACTUATOR_CMD;
        actu[ 1 ] = aID;
        actu[ 2 ] = (byte) ( state & 0x01 );
        actu[ 3 ] = (byte) ( state & 0x02 );
        
        this.sendCommand( actu );
    }
    
    /**
     * Move the actuator for the arm.
     * @param dir The direction of the arm.
     */
    @Override
    public void moveArm( byte dir )
    {
        
        this.moveActuator( ARM , dir );
        
    }
    
    /**
     * Move the actuator for the bucket.
     * @param dir The direction the bucket will move.
     */
    @Override
    public void moveBucket( byte dir )
    {
        this.moveActuator( BUCKET , dir );
    }
    
    public void setNetConnected( byte connStatus )
    {
        byte [ ] conn = new byte[ 2 ];
        
        conn[ 0 ] = CONNECTION_CMD;
        conn[ 1 ] = connStatus;
        
        this.sendCommand( conn );
    }
    
    public void sendHeartBeat( )
    {
        byte [ ] heart = new byte[ 1 ];
        
        heart[ 0 ] = HEARTBEAT_CMD;
        
        this.sendCommand( heart );
    }
    
    public int getArmPosition( )
    {
        int position = 0;
        
        return position;
    }
    
    public int getBucketPosition( )
    {
        int position = 0;
        
        return position;
    }
    
    public static void main( String [ ] args )
    {
        ArduinoCommand aC = new ArduinoCommand( "/Users/zacharyduve/Dropbox/Lunabots 2014/CS/atest.txt" );
        
        aC.setLeftDrive( (byte) 0x00, (byte) 0xFF );
        aC.setRightDrive( (byte) 0x00, (byte) 0xFF );
        aC.emergencyStop( );
    }

    @Override
    public void sendAutoDriveForward() 
    {
        byte autoForwardCMD [ ] = new byte[ 2 ];
        
        autoForwardCMD[ 0 ] = AUTO_DRIVE_CMD;
        autoForwardCMD[ 1 ] = AUTO_DRIVE_FORWARD;
        
        this.sendCommand( autoForwardCMD );
    }

    @Override
    public void sendAutoDriveLeft() 
    {
        byte autoLeftCMD [ ] = new byte[ 2 ];
        
        autoLeftCMD[ 0 ] = AUTO_DRIVE_CMD;
        autoLeftCMD[ 1 ] = AUTO_DRIVE_LEFT;
        
        this.sendCommand( autoLeftCMD );
    }

    @Override
    public void sendAutoDriveRight() 
    {
        byte autoRightCMD [ ] = new byte[ 2 ];
        
        autoRightCMD[ 0 ] = AUTO_DRIVE_CMD;
        autoRightCMD[ 1 ] = AUTO_DRIVE_RIGHT;
        
        this.sendCommand( autoRightCMD );
    }

    @Override
    public void sendAutoDriveBackward() 
    {
        byte autoBackwardCMD [ ] = new byte[ 2 ];
        
        autoBackwardCMD [ 0 ] = AUTO_DRIVE_CMD;
        autoBackwardCMD [ 1 ] = AUTO_DRIVE_BACKWARD;
        
        this.sendCommand( autoBackwardCMD );
    }

    @Override
    public void toggleBumpers() 
    {
        byte toggleBumperCMD [ ] = new byte[ 1 ];
        
        toggleBumperCMD[ 0 ] = TOGGLE_BUMPER_CMD;
        
        this.sendCommand( toggleBumperCMD );
    }
    
    public void sendSubroutineCommand(byte b)
    {
        byte subCmd [] = new byte[1];
        
        subCmd[0] = b;
        
        this.sendCommand(subCmd);
    }

    @Override
    public void requestData(byte b) 
    {
        byte cmd [] = new byte[1];
        
        cmd[0] = b;
        
        this.sendCommand(cmd);
    }
    
    public byte[ ] readInput( )
    {
        int numAvail = 0;
        
        try
        {
             numAvail = in.available( );
        }
        catch( IOException iOE )
        {
            System.err.println( "ERROR" );
        }

        byte [ ] dataIn = new byte [ numAvail ];

        try
        {
             in.read( dataIn );
             System.out.println( new String( dataIn ) );
        }
        catch( IOException iOE )
        {
            System.err.println( "ERROR" );
        }
        
        return dataIn;
    }
}
