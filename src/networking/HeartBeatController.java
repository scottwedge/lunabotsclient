/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package networking;

import controller.NewGui;
import java.awt.event.ActionEvent;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.net.DatagramSocket;

/**
 *
 * @author Zachary Duve (zas4)
 */
public class HeartBeatController implements ActionListener //, Runnable
{
    protected UDPConnection connection;
    protected Timer heartBeater;
    protected Timer monitorUpdater;
    protected Timer disconnectedTimer; //tracks how long the bot has gone since getting
                                       //any data from the bot. too long an it lights up the
                                       //LED representation in the GUI.
    protected int beatDelay;
    //protected DatagramSocket socket;
    NewGui gui = null;
    
      
    public HeartBeatController( int bDelay, UDPConnection conn )
    {
        connection = conn;
        beatDelay = bDelay;
        /*socket = conn.getSocket();
        try{socket.setSoTimeout(5);}
          catch(Exception e){System.err.println("Heartbeat may not be working. This may affect GUI or controller functionality");}
        */
        heartBeater = new Timer( beatDelay, this );
        monitorUpdater = new Timer(450, this);
        disconnectedTimer = new Timer(3000, this);        
        heartBeater.start( );
        monitorUpdater.start();
    }
    
    public void setGUI(NewGui g)
    {
      gui = g;
    }
    
  
    @Override
    public void actionPerformed(ActionEvent e)
    {
      if(e.getSource() == heartBeater)
      {
        byte [ ] data = new byte[ 1 ];
        
        data[ 0 ] = HeartBeatUtil.HEART_TO_BOT_MASK;
        
        connection.send( data );
        
        //System.out.println( "Sent Beat" );
        
        if(gui != null)
        {
          //gui.addText("Sent Beat");
          gui.heartUpdate("^\\/^");
        }
        
      }
      else if (e.getSource() == monitorUpdater)
      {
        if(gui != null)
        {
          gui.heartUpdate("--");
        }
      }
      else if(e.getSource() == disconnectedTimer)
      {
        gui.connLED.setSelected(false);
        disconnectedTimer.restart();
      }
    }

  /*@Override
  public void run() {
    while(true)
    {
      byte[] data = new byte[1];
      boolean received = connection.receive(data);
      if (received)
      {
        gui.connLED.setSelected(true);
        disconnectedTimer.restart();
        if(data[0] == HeartBeatUtil.HEART_FROM_BOT_MASK)
          gui.heartUpdate("^\\/^");
        else
          gui.addText("Unexpected byte received from bot" + data);
      }
    }
  }*/
}
