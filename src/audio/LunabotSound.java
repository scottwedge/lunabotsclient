/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package audio;
import java.lang.Runtime;

/**
 *
 * @author Zachary Duve (zas4)
 */
public class LunabotSound 
{
    public LunabotSound( )
    {
        
    }
    
    public void playSound( String audioPath )
    {
        try
        {
            Runtime.getRuntime( ).exec( "mpg321 " + audioPath );
        }
        catch( Exception e )
        {
            e.printStackTrace( );
        }
    }
}
