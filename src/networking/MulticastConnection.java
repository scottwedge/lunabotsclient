/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package networking;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
/**
 *
 * @author Zachary Duve (zas4)
 */
public class MulticastConnection extends UDPConnection
{
    protected byte [ ] defaultGroupAddress = { (byte) 224, (byte) 1, (byte) 1, (byte) 30 };  
    
    public MulticastConnection( )
    {
        createConnection( );
    }
    
    public MulticastConnection( int p )
    {
        createConnection( p );
    }
    
    public void createConnection( )
    {
        try
        {
            MulticastSocket multiSocket = new MulticastSocket( );
            
            address = InetAddress.getByAddress( defaultGroupAddress );
            
            multiSocket.joinGroup( address );
            
            super.socket = multiSocket;
        }
        catch( Exception e )
        {
            e.printStackTrace( );
        }
    }
    
    public void createConnection( int p )
    {
        try
        {
            MulticastSocket multiSocket = new MulticastSocket( p );
            
            address = InetAddress.getByAddress( defaultGroupAddress );
            
            multiSocket.joinGroup( address );
            
            super.socket = multiSocket;
        }
        catch( Exception e )
        {
            e.printStackTrace( );
        }
    }
}