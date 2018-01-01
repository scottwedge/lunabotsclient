/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package networking;

import java.net.DatagramPacket;

import arduino.ArdLunaInterface;
/**
 *
 * @author zacharyduve
 */
public interface NetworkInterface 
{
    public void addArdLunaInterface( ArdLunaInterface aLI );
    
    public void exeCommand( byte [ ] cmd );
    
    public boolean canProcessCommand( byte [ ] cmd );
    
    public void addUDPConnection( UDPConnection conn );
}
