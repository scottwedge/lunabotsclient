/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package arduino;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
/**
 *
 * @author Zachary Duve (zas4)
 */
public class ArduinoCommand implements ArdLunaInterface
{
    
    protected String arduinoPath = "/dev/ttyACM0";
    //private String arduinoPath = "/dev/tty.usbmodem411";
    
    //private boolean connected = false;
    
    private BufferedInputStream in;
    protected BufferedOutputStream out;
    protected ArduinoConnection ardConnection;
    /**
     * Creates a connection to the arduino.
     */
    public ArduinoCommand( )
    {
        createConnection( );
    }
    
    /**
     * Creates a connection to the arduino by the given path.
     * @param aPath The path to the arduino.
     */
    public ArduinoCommand( String aPath )
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
            
            new InputEater( in );
            
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
    private void sendCommand( byte[ ] data )
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
        byte [ ] drive = new byte [ 3 ];
        drive[ 0 ] = MOTOR_CMD;
        drive[ 1 ] = mID;
        
        byte speedVal = (byte) ( speed * SPEED_MULT );
        if( speedVal > DELTA_SPEED - 1 )
        {
            speedVal = DELTA_SPEED - 1;
        }
        
        if( dir == 0 )
        {
            drive[ 2 ] = (byte) ( ZERO_SPEED_VALUE + speedVal );
        }
        else
        {
            drive[ 2 ] = (byte) ( ZERO_SPEED_VALUE - speedVal );
        }
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
        this.setDrive( this.LEFT, (byte) ( ( dir + LEFT_DIR ) % 2 ), speed );
    }
    
    /**
     * Set the speed and direction for the right motor.
     * @param dir The right motors new direction.
     * @param speed The right motors new speed.
     */
    @Override
    public void setRightDrive( byte dir, byte speed )
    {
        this.setDrive( this.RIGHT, (byte) ( ( dir + RIGHT_DIR ) % 2 ), speed );
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
        if( ARM_DIR == 0 || dir == 0x00 ) //if direction is normal or it is shut off arm
        {
            this.moveActuator( this.ARM , dir );
        }
        else //if( ARM_DIR == 1 ) //direction is reversed and is to move arm
        {
            this.moveActuator( this.ARM , (byte) (~dir) );
        }
    }
    
    /**
     * Move the actuator for the bucket.
     * @param dir The direction the bucket will move.
     */
    @Override
    public void moveBucket( byte dir )
    {
        if( BUCKET_DIR == 0 || dir == 0x00 )
        {
            this.moveActuator( this.BUCKET , dir );
        }
        else
        {
            this.moveActuator( this.BUCKET, (byte) (~dir) );
        }
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
    
    public void toggleBumpers( )
    {
        
    }

  @Override
  public void sendSubroutineCommand(byte b) {
    //throw new UnsupportedOperationException("Not supported yet.");
  }

    @Override
    public void requestData(byte b) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public byte[] readInput() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
