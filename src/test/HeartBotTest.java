/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import networking.*;

/**
 *
 * @author Zachary Duve (zas4)
 */
public class HeartBotTest 
{
    public HeartBotTest( ) 
    {
        UDPConnection conn = new UDPConnection( 4030 );
        
        //HeartBeatBot hBeat = new HeartBeatBot( HeartBeatUtil.HEART_DELAY * 2, conn );
        
        while( true )
        {
            
        }
    }
    
    public static void main( String [ ] args )
    {
        new HeartBotTest( );
    }
}
