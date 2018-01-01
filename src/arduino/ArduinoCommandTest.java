/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package arduino;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
/**
 *
 * @author Zachary Duve (zas4)
 */
public class ArduinoCommandTest extends ArduinoCommandV2
{
    //private String logPath = "ardIOlog.log";
    /**
     * Creates a connection to the arduino.
     */
    public ArduinoCommandTest( )
    {
        this.createConnection( );
    }
    
    /**
     * Creates a connection to the arduino by the given path.
     * @param aPath The path to the arduino.
     */
    public ArduinoCommandTest( String aPath )
    {
        arduinoPath = aPath;
        this.createConnection( );
    }
    
    @Override
    protected void createConnection( )
    {
        String logPath = "ardIOlog.log";
        File file = new File( logPath );
        
        try
        {
            FileOutputStream fOS = new FileOutputStream( file );
            
            out = new BufferedOutputStream( fOS );
            
            
            Thread.currentThread().sleep( 1600 );//let the arduino get ready before writing
            //System.out.println( "Ready to start talking" ); 
        }
        catch( Exception e )
        {
            e.printStackTrace( );
        }
    }
}
