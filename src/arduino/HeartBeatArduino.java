/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package arduino;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import networking.UDPConnection;

/**
 *
 * @author Zachary Duve (zas4)
 */
public class HeartBeatArduino  implements ActionListener
{
    protected ArdLunaInterface ard;
    protected Timer heartBeater;
    protected int beatDelay = 250; //250 milliseconds
    
    public HeartBeatArduino( ArdLunaInterface a )
    {
        ard = a;
        
        heartBeater = new Timer( beatDelay, this );
        
        heartBeater.start( );
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        ard.sendHeartBeat( );
    }
}
