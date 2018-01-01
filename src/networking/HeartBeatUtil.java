/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package networking;

/**
 *
 * @author Zachary Duve (zas4)
 */
public abstract class HeartBeatUtil 
{
    public static final byte HEART_TO_BOT_MASK = (byte) 0xFF;
    public static final byte HEART_FROM_BOT_MASK = (byte) 0xFE;
    public static final int HEART_DELAY = 2000;
}
