/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package networking;

import java.net.*;
/**
 *
 * @author Zachary Duve (zas4)
 */

// ATTN: Changed defaultAdd from 192.168.1.2 to 192.168.1.4
public class UDPConnection
{
    protected int port = 3010;
    protected InetAddress address;
    //protected byte[ ] defaultAdd = { (byte) 192, (byte) 168, (byte) 2, (byte) 5 }; 
    
    // OLF IP ADDRESS
    //protected byte[ ] defaultAdd = { (byte) 192, (byte) 168, (byte) 2, (byte) 18 };  //raspi
   // protected byte[ ] defaultAdd = { (byte) 192, (byte) 168, (byte) 2, (byte) 12 };// GREEN CHIP
  //protected byte[ ] defaultAdd = { (byte) 192, (byte) 168, (byte) 1, (byte) 3};
protected byte[ ] defaultAdd = { (byte) 169, (byte) 254, (byte) 59, (byte) 161 }; 
    //protected byte[ ] defaultAdd = { (byte) 10, (byte) 40, (byte) 52, (byte) 161 };
    protected DatagramSocket socket;
    protected boolean autoReturnEnabled;
    //private final Object lock;
    //private Thread reader, writer;
    /**
     * Creates a default UDP server.
     */
    public UDPConnection( )
    {
        createConnection( );
    }
    
    /**
     * Creates a UDP server on the specified port.
     * @param p The port the server will listen on.
     */
    public UDPConnection( int p )
    {
        //port = p;
        
        createConnection( p );
    }
    
    /**
     * Creates a server on the given port.
     */
    private void createConnection( int p )
    {
        try
        {
            socket = new DatagramSocket( p );
            address = InetAddress.getByAddress( defaultAdd );
        }
        catch( Exception e )
        {
            e.printStackTrace( );
            System.exit( 1 );
        }
    }
    
    private void createConnection( )
    {
        try
        {
            socket = new DatagramSocket( );
            address = InetAddress.getByAddress( defaultAdd );
        }
        catch( Exception e )
        {
            e.printStackTrace( );
            System.exit( 1 );
        }
    }
    
    public synchronized boolean send( byte [ ] dataOut )
    {
        try
        {
            DatagramPacket packetOut = new DatagramPacket( dataOut, dataOut.length, address, port );
            socket.send( packetOut );
            return true;
        }
        catch( Exception e )
        {
            e.printStackTrace( );
            return false;
        }
    }
    
    public synchronized boolean receive( byte [ ] dataIn )
    {
        DatagramPacket packetIn = null;
        try
        {
            packetIn = new DatagramPacket( dataIn, dataIn.length );
            socket.receive( packetIn );
            
        }
        catch( Exception e )
        {
            e.printStackTrace();
            return false;
        }
        
        if( autoReturnEnabled )
        {
            address = packetIn.getAddress( );
            port = packetIn.getPort( );
            /*
            try
            {
                address = InetAddress.getByName( "192.168.1.23" );
                port = 3004;
            }
            catch( Exception e )
            {
                
            }
            */
        }
        
        return true;
    }
    
    public synchronized void setAddress( InetAddress addr )
    {
        address = addr;
    }
    
    public synchronized void setPort( int p )
    {
        port = p;
    }
    
    public DatagramSocket getSocket()
    {
      return socket;
    }
    
    public void setAutoReturn( boolean enabled )
    {
        autoReturnEnabled = enabled;
    }
}
