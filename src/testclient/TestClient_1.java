/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package testclient;

import net.java.games.input.*;
import networking.*;
import java.net.*;

/**
 *
 * @author Zachary Duve (zas4)
 */
public class TestClient_1 
{
    public static void main( String [ ] args )
    {
        UDPConnection connection = new UDPConnection( 0 );
        
        Controller [ ] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
    
        Controller theOne = null;
        
        System.out.println( "Number of controllers connected: " + controllers.length );
        
        for( int i = 0; i < controllers.length; i++ )
        {
            System.out.println( "Controller " + i + " type: " + controllers[ i ].getType( ) );
            if( controllers[ i ].getType() == Controller.Type.STICK )
            {
                theOne = controllers[ i ];
                break;
            }
        }
        
        if( theOne != null )
        {
            System.out.println( theOne.getName( ) ); 
            System.out.println( "Number of Components: " + theOne.getComponents().length );
            System.out.println( "Number of Rumblers: " + theOne.getRumblers().length );
            Component [ ] components = theOne.getComponents();
            float [] componentvals = new float[components.length];
            try
            {
              theOne.poll();
              for(int j = 0; j < components.length; j++)
              {
                componentvals[j] = components[ j ].getPollData( );
                System.out.println(components[ j ].getName( ) + " " + components[ j ].isAnalog( ) + " " + componentvals[j] );
              }
              
              EventQueue events;
              Event ev = new Event();
                      
              /******************************************************/
              while(true)
              {
                
                theOne.poll();
                events = theOne.getEventQueue();
                while(events.getNextEvent(ev))
                {
                  Component c = ev.getComponent();
                  if(!c.getName().equals("Z Axis"))
                    System.out.println(c.getName() + ": " + ev.getValue());
                }
                /*for(int j = 0; j < components.length; j++)
                {
                  if(components[ j ].getPollData( ) != componentvals[j])
                  {
                    componentvals[j] = components[ j ].getPollData( );
                    System.out.println(components[ j ].getName( ) + " " + components[ j ].isAnalog( ) + " " + componentvals[j] );
                  }
                }*/
                
              }               
               /*****************************************************/
                
            }
            catch( Exception e )
            {
                
            }
        }
    }
}
