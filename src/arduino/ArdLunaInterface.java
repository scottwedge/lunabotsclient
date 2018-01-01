/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package arduino;

/**
 *
 * @author zacharyduve
 */
public interface ArdLunaInterface 
{
    public final byte LEFT = 0x00;
    public final byte RIGHT = 0x01;
    public final byte ARM = 0x002;
    public final byte BUCKET =0x03;
    
    
    public final int LEFT_DIR = 0; //0 normal command 1 is invert
    public final int RIGHT_DIR = 1; //
    public final int ARM_DIR = 0;
    public final int BUCKET_DIR = 1;
    
    
    public final byte MOTOR_CMD = 0x00;
    public final byte ACTUATOR_CMD = 0x01;
    public final byte CONNECTION_CMD = 0x02;
    public final byte ERROR_CMD = 0x03;
    public final byte HEARTBEAT_CMD = 0x04;
    public final byte EMERGENCY_STOP_CMD = 0x05;
    
    
    //------------------------FOR ArduinoCommand--------------------------------
    public final byte ZERO_SPEED_VALUE = (byte) 0xC0; //192 (254 - 128) / 2 + 128
    
    public final byte MAX_SPEED_VALUE = (byte) 0xFE; //254
    public final byte MIN_SPEED_VALUE = (byte) 0x80; //128
    public final byte DELTA_SPEED = (byte) 0x40;
    public final byte SPEED_MULT = 4;
    //--------------------------------------------------------------------------
    
    public final int IO_SPEED = 57600;
    
    public final byte ON = 0x01;
    public final byte OFF = 0x00;
    
    public final byte AUTO_DRIVE_CMD = 0x06;
    public final byte AUTO_DRIVE_FORWARD = 0x00;
    public final byte AUTO_DRIVE_LEFT = 0x01;
    public final byte AUTO_DRIVE_RIGHT = 0x02;
    public final byte AUTO_DRIVE_BACKWARD = 0x03;
    
    public final byte TOGGLE_BUMPER_CMD = 0x07;
    
    public void upDatePath( String nP );
    
    public void emergencyStop( );

    public void setDrive( byte mID, byte dir, byte speed );
    
    public void setLeftDrive( byte dir, byte speed );
    
    public void setRightDrive( byte dir, byte speed );
    
    public void moveActuator( byte aID, byte dir );
    
    public void moveArm( byte dir );
    
    public void moveBucket( byte dir );
    
    public void setNetConnected( byte connStatus );
    
    public void sendHeartBeat( );
    
    public int getArmPosition( );
    
    public int getBucketPosition( );
    
    public void sendAutoDriveForward( );
    
    public void sendAutoDriveLeft( );
    
    public void sendAutoDriveRight( );
    
    public void sendAutoDriveBackward( );
    
    public void toggleBumpers( );
    
    public void sendSubroutineCommand(byte b);
    
    public void requestData(byte b);
    
    public byte[ ] readInput( );
}
