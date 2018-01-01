/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package arduino;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Zachary Duve (zas4)
 */
public class ArduinoConnection 
{
    
    private SerialPort serialPort = null;
    private int ioSpeed = 115200;
    
    public ArduinoConnection( )
    {
        //super();
    }
    
    public void connect ( String portName ) throws Exception
    {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
        }
        else
        {
            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
            
            if ( commPort instanceof SerialPort )
            {
                serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams( ioSpeed, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            }
            else
            {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }     
    }
    
    public InputStream getInputStream( )
    {
        try
        {
            return serialPort.getInputStream( );
        }
        catch( IOException iOE )
        {
            iOE.printStackTrace( );
            return null;
        }
    }
    
    public OutputStream getOutputStream( )
    {
        try
        {
            return serialPort.getOutputStream( );
        }
        catch( IOException iOE )
        {
            iOE.printStackTrace( );
            return null;
        }
    }
    
    public void setIOSpeed( int ioS )
    {
        if( serialPort != null )
        {
            System.err.println( "Can not set speed after connection has been made" );
        }
        else
        {
            ioSpeed = ioS;
        }
    }
    
    public int getIOSpeed( )
    {
        return ioSpeed;
    }
}
