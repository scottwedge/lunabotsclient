
/**
 * ControllerMain class.
 */

package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import net.java.games.input.*;
import networking.*;
//import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Leland Curtis
 */
public class ControllerMain implements ActionListener, ItemListener {
  
    //---------------MASKS-----------------------
    public final byte COMMAND_MASK = (byte) 0xE0;
    
    public final byte ESTOP_MASK = 0x00;
    public final byte LEFT_MASK =  0x20;
    public final byte RIGHT_MASK = 0x40;
    public final byte ARM_MASK = (byte) 0x60; //Netbeans has issues?
    public final byte BUCKET_MASK = (byte) 0x80; //Netbeans has issues?
    
    public final byte ROUTINE_MASK = (byte) 0xA0; //command to switch on or off 
    
    
    //-----UTIL MASKS-------
    public final byte DIR_MASK = 0x10;
    public final byte SPEED_MASK = 0x0F;
    public final byte ARM_STATE_MASK = 0x03;
    public final byte DATA_REQUEST = (byte) 0xEE;
    
    //----SUBROUTINE COMMANDS--------
    public final byte AUTODRIVE_MASK = (byte) 0xE1; //command to automatically dirve forward 
                                                        //until told otherwise
    public final byte AUTODRIVE_BACK = (byte) 0xEA;
    
    public final byte AUTOSCOOP_MASK = (byte) 0xE2; //command to automatically scoop up moondirt
    public final byte MINING_POSITION_MASK = (byte) 0xE3; //command mask to automaticallly 
                                                              //assume optimal mining position
    public final byte AUTODUMP_MASK = (byte) 0xE4; //command to automatically dump our load    
    public final byte AUTO_RAISE_ARM_MASK = (byte) 0xE5; //automatically raise arm while keeping bucket level
    
    public final byte AUTO_TURN_LEFT = (byte) 0xE6;
    public final byte AUTO_TURN_RIGHT = (byte) 0xE7;
    
    public final byte OVERRIDE_COLLISION_DETECTION_MASK = (byte) 0xE8;
    public final byte OVERRIDE_ARM_SENSORS_MASK = (byte) 0xE9;
        
    
    //STILL NOT SURE IF WE"RE EVEN GOING TO USE THESE LIKE THIS
    //----RETURN DATA MASKS----------
    //These are only used to handle data coming from the robot so it doesn't matter
    //that they overlap some of the data sending masks
    public final byte ERROR_LED_MASK = (byte) 0x03;
    public final byte BUCKET_POSITION_MASK = (byte) 0x1C;
    public final byte ARM_POSITION_MASK = (byte) 0xE0;
    
        
    //-------INSTANCE VARS--------------
    NewGui gui;
    UDPConnection udp;
    Gamepad gamepad = null;
    UDPConnection heartConn;
    HeartBeatController heart;
    Controller keyboard = null;
    //alternate joystick code vars
    float lastLeftStick = 0.0f; float lastRightStick = 0.0f;
    float lastLBTrig = 0.0f; float lastLTTrig = 0.0f;
    float lastRBTrig = 0.0f; float lastRTTrig = 0.0f;
    float lastDumpButton = 0.0f; float lastPovHat = Component.POV.CENTER;
    float lastMinePosButton = 0.0f; float lastRaiseArmButton = 0.0f;
    float lastScoopButton = 0.0f;
    Component leftStick,rightStick,leftBottomTrig, leftTopTrig, rightBottomTrig, rightTopTrig,
              dumpButton, minePosButton, raiseArmButton, motorStop, povHat;    
    //Thread heartThread;

   
  //private subclass that encapsulates controller activity
  private class Gamepad{
    Controller pad = null;
    EventQueue events;
    Event ev = new Event();
   
    //---constructor---
    public Gamepad()
    {
      //---try to find gamepad---
      Controller[] c = ControllerEnvironment.getDefaultEnvironment().getControllers();
      for(int i = 0; i < c.length; i++)
      {
        gui.addText("Controller " + i + " type: " + c[i].getType().toString());
        if(c[i].getType() == Controller.Type.STICK)
        {
          pad = c[i];
          break;
        }
      }
      //---exit if it can't be found---
      if(pad == null)
      {
        System.out.println("ERROR: GAMEPAD NOT FOUND");
        gui.addText("ERROR: GAMEPAD NOT FOUND!");
        //System.exit(1);
      }      
    }
    //---method to return list of changed values  
    public void poll()
    {
      pad.poll();
    }
    public Component getNextChangedComponent()
    {
      pad.poll();
      events = pad.getEventQueue();
      if(events.getNextEvent(ev))
        return ev.getComponent();
      else
        return null;
    }
  }
  
  
  public ControllerMain()
  {
    gui = new NewGui(this);
    
    udp = new UDPConnection(); //set up new UDP connection
    
    heartConn = new UDPConnection();
    heartConn.setPort(3005);
    heart = new HeartBeatController(2000, heartConn);
    heart.setGUI(gui);
    
    //heartThread = new Thread(heart);
    
    gamepad = new Gamepad();    
    leftStick = gamepad.pad.getComponent(Component.Identifier.Axis.Y);
    rightStick = gamepad.pad.getComponent(Component.Identifier.Axis.RZ);
    leftBottomTrig = gamepad.pad.getComponent(Component.Identifier.Button._4);
    rightBottomTrig = gamepad.pad.getComponent(Component.Identifier.Button._5);
    leftTopTrig = gamepad.pad.getComponent(Component.Identifier.Button._6);
    rightTopTrig = gamepad.pad.getComponent(Component.Identifier.Button._7);
    dumpButton = gamepad.pad.getComponent(Component.Identifier.Button._3);
    raiseArmButton = gamepad.pad.getComponent(Component.Identifier.Button._1);
    minePosButton = gamepad.pad.getComponent(Component.Identifier.Button._2);
    motorStop = gamepad.pad.getComponent(Component.Identifier.Button._0);    
    povHat = gamepad.pad.getComponent(Component.Identifier.Axis.POV);
    //JOptionPane.showMessageDialog(null, "MAKE SURE YOU'RE ON THE RIGHT NETWORK!!!");
  }
  
  public void run()
  { 
    //heartThread.start();
    try
    {
      //------------CONTROLLER LOOP-----------------        
   
      //Component c = null;
      //rather than constantly transmit minor fluctuations in the stick axis, we'll
      //have 8(?) divisions of the axis and only send new data if it moves between them
      //int lastLeftStickGrad = 0, lastRightStickGrad = 0;    
      ///////////////////////////////////////////////////////
      while(true)
      {
        
        //////////////////////////////////////////////////////////////////
        //GAMEPAD CODE
        //////////////////////////////////////////////////////////////////    
        
        altControllerCode();
        
        
      }
    } 
    catch(Exception e) {gui.addText("EXCEPTION CAUGHT: " + e.getMessage());}   
  }
  
  /////////////////////////////////////////////////////////////////
  //===============ALTERNATE CONTROLLER CODE=======================
  //////////////////////////////////////////////////////////////////
  public void altControllerCode()
  {
      ///////////////////////////////////////////
      //---------LEFT STICK------------------
      ///////////////////////////////////////////
      gamepad.poll();
      float stickVal = leftStick.getPollData();
      
        boolean changed = false;
        
        if (Math.abs(stickVal) < .1)
        {
          stickVal = 0.0f;
          if(stickVal != lastLeftStick)
          {
            changed = true;
            lastLeftStick = stickVal;
          }
        }
        else if (Math.abs(stickVal) > .95f)
        {
          stickVal = Math.round(stickVal);
          if(stickVal != lastLeftStick)
          {
            changed = true;
            lastLeftStick = stickVal;
          }
        }
        else if (Math.abs(stickVal-lastLeftStick) > .2)
        {
          if(stickVal != lastLeftStick)
          {
            changed = true;
            lastLeftStick = stickVal;
          }
        }
        
        if (changed == true)
        {
          //----send a byte----
          byte[] data = new byte[1];
          if(lastLeftStick >= 0)
            data[0] = (byte) (LEFT_MASK | DIR_MASK | (int) (Math.floor(lastLeftStick*15)));
          else
            data[0] = (byte) (LEFT_MASK | (int) (Math.floor(lastLeftStick * -15)));
          udp.send(data);
          gui.addText("Left Stick " + data[0]);
        }
        
      /////////////////////////////////////////////
      //------------RIGHT STICK---------------
      /////////////////////////////////////////////
      stickVal = rightStick.getPollData();
        changed = false;
        
        if (Math.abs(stickVal) < .1)
        {
          stickVal = 0.0f;
          if(stickVal != lastRightStick)
          {
            changed = true;
            lastRightStick = stickVal;
          }
        }
        else if (Math.abs(stickVal) > .95f)
        {
          stickVal = Math.round(stickVal);
          if(stickVal != lastRightStick)
          {
            changed = true;
            lastRightStick = stickVal;
          }
        }
        else if (Math.abs(stickVal-lastRightStick) > .2)
        {
          if(stickVal != lastRightStick)
          {
            changed = true;
            lastRightStick = stickVal;
          }
        }
        
        if (changed == true)
        {
          //----send a byte----
          byte[] data = new byte[1];
          if(lastRightStick >= 0)
            data[0] = (byte) (RIGHT_MASK | DIR_MASK | (int) (Math.floor(lastRightStick*15)));
          else
            data[0] = (byte) (RIGHT_MASK | (int) (Math.floor(lastRightStick * -15)));
          udp.send(data);
          gui.addText("Right Stick " + data[0]);
        }
      
      ////////////////////////////////////////////
      //---------ACTUATOR SHIT-----------------
      ////////////////////////////////////////////
      float buttonVal = leftTopTrig.getPollData();
        //System.out.println("left top trigger " + leftTopTrig.getPollData());
        if(buttonVal != lastLTTrig)
        {
          gui.addText("left top trigger " + buttonVal);
          byte[] data = new byte[1];
          if(buttonVal == 0)
            data[0] = BUCKET_MASK;
          else
            data[0] = BUCKET_MASK | 0x01;
          udp.send(data);
          lastLTTrig = buttonVal;
        }
      
      buttonVal = rightTopTrig.getPollData();
      
        //System.out.println("right top trigger " + rightTopTrig.getPollData());
        if(buttonVal != lastRTTrig)
        {
          gui.addText("right top trigger " + buttonVal);
          byte[] data = new byte[1];
          if(buttonVal == 0)
            data[0] = ARM_MASK;
          else
            data[0] = ARM_MASK | 0x01;
          udp.send(data);
          lastRTTrig = buttonVal;
        }
      
      buttonVal = leftBottomTrig.getPollData();
      
        //System.out.println("left bottom trigger " + leftBottomTrig.getPollData());
        if(buttonVal != lastLBTrig)
        {
          gui.addText("left bottom trigger " + buttonVal);
          byte[] data = new byte[1];
          if(buttonVal == 0)
            data[0] = BUCKET_MASK;
          else
            data[0] = BUCKET_MASK | 0x02;
          udp.send(data);
          lastLBTrig = buttonVal;
        }
        
      buttonVal = rightBottomTrig.getPollData();
      
        //System.out.println("right bottom trigger " + rightBottomTrig.getPollData());
        if(buttonVal != lastRBTrig)
        {
          gui.addText("right Bottom trigger " + buttonVal);
          byte[] data = new byte[1];
          if(buttonVal == 0)
            data[0] = ARM_MASK;
          else
            data[0] = ARM_MASK | 0x02;
          udp.send(data);
          lastRBTrig = buttonVal;
        }       
      //////////////////////////////////////////////
      //------SUBROUTINE SHIT------
      //////////////////////////////////////////////   
        
      //auto-dump function--------------------------  
      buttonVal = dumpButton.getPollData();
      
      if(buttonVal != lastDumpButton)
      {
        if (lastDumpButton == 1)
          lastDumpButton = 0;
        else
        {
          gui.addText("Face Button 2: AutoDump function");
          byte[] data = new byte[1];
          data[0] = AUTODUMP_MASK;
          udp.send(data);
          lastDumpButton = 1;
        }        
      }
      
      //auto-Raise-Arm function-----------------------
      buttonVal = raiseArmButton.getPollData();
      
      if(buttonVal != lastRaiseArmButton)
      {
        if (lastRaiseArmButton == 1)
          lastRaiseArmButton = 0;
        else
        {
          gui.addText("Face Button 1: AUTO ON/OFF");
          byte[] data = new byte[1];
          data[0] = ROUTINE_MASK;
          udp.send(data);
          lastRaiseArmButton = 1;
        }        
      }
      
      //auto-Mining-position function------------------
      buttonVal = minePosButton.getPollData();
      
      if(buttonVal != lastMinePosButton)
      {
        if (lastMinePosButton == 1)
          lastMinePosButton = 0;
        else
        {
          gui.addText("Face Button 3: Auto Mining Position function");
          byte[] data = new byte[1];
          data[0] = MINING_POSITION_MASK;
          udp.send(data);
          lastMinePosButton = 1;
        }        
      }
      
      //auto-scoop function::::::MAY NOT END UP GETTING IMPLEMENTED-------
      buttonVal = motorStop.getPollData();
      
      if(buttonVal != lastScoopButton)
      {
        if (lastScoopButton == 1)
          lastScoopButton = 0;
        else
        {
          gui.addText("ESTOP BUTTON PRESSED");
          byte[] data = new byte[1];
          data[0] = ESTOP_MASK;
          udp.send(data);
          lastScoopButton = 1;
        }        
      }
      
      //Driving subroutines - on the POV hat.--------------------------
      //drive forward, probably until interrupted
      buttonVal = povHat.getPollData();
      if(buttonVal != lastPovHat && lastPovHat == Component.POV.CENTER)
      {
        if(buttonVal == Component.POV.UP)
        {
          gui.addText("Pov Hat UP - AutoDrive");
          byte[] data = new byte[1];
          data[0] = AUTODRIVE_MASK;
          udp.send(data);
          lastPovHat = buttonVal;
        }
      }
      
      
      //turn left 90 degrees-ish - not really sure why this is desirable
      //buttonVal = povHat.getPollData();      
      if(buttonVal != lastPovHat && lastPovHat == Component.POV.CENTER)
      {
        if(buttonVal == Component.POV.LEFT)
        {
          gui.addText("Pov Hat LEFT - AutoTurn90Left");
          byte[] data = new byte[1];
          data[0] = AUTO_TURN_LEFT;
          udp.send(data);
          lastPovHat = buttonVal;
        }
      }      
      
      //turn right 90 degrees-ish - not really sure why this is desirable
      //buttonVal = povHat.getPollData();
      if(buttonVal != lastPovHat && lastPovHat == Component.POV.CENTER)
      {
        if(buttonVal == Component.POV.RIGHT)
        {
          gui.addText("Pov Hat RIGHT - AutoTurn90Right");
          byte[] data = new byte[1];
          data[0] = AUTO_TURN_RIGHT;
          udp.send(data);
          lastPovHat = buttonVal;
        }
      }
      
      //turn right 90 degrees-ish - not really sure why this is desirable
      //buttonVal = povHat.getPollData();
      if(buttonVal != lastPovHat && lastPovHat == Component.POV.CENTER)
      {
        if(buttonVal == Component.POV.DOWN)
        {
          gui.addText("Pov Hat DOWN - AutoBackwards");
          byte[] data = new byte[1];
          data[0] = AUTODRIVE_BACK;
          udp.send(data);
          lastPovHat = buttonVal;
        }
      }
      
      //turn right 90 degrees-ish - not really sure why this is desirable
      //buttonVal = povHat.getPollData();
      if(buttonVal != lastPovHat && buttonVal == Component.POV.CENTER)
      {
          //gui.addText("Pov Hat CENTER");
          lastPovHat = buttonVal;
      }
           
  }
  
  ///////////////////////////////////////////////////////
  //this is what happens when the Estop button is pressed
  @Override
  public void actionPerformed(ActionEvent e) {
    if(e.getSource() == gui.eStop)
    {
      gui.addText("EStop pressed");
      byte[] data = new byte[1];
      data[0] = ESTOP_MASK;
      udp.send(data);
    }
    else if (e.getSource() == gui.dataRequestButton)
    {
        byte[] sdata = new byte[1];
        sdata[0] = DATA_REQUEST;
        udp.send(sdata);
        byte[] rdata = new byte[256];
        udp.receive(rdata);
        gui.battVoltField.setText( "Power Used: " + new String( rdata ) + "W" );
    }
    else
      System.out.println("unsupported");
  }
  
  @Override
  public void itemStateChanged(ItemEvent e) {
    if (e.getItemSelectable() == gui.disableBumperSwitch)
    {
      //send a byte. This will toggle the state on th arduino. default is false
      gui.addText("toggle bumper switch override");
      byte[] data = new byte[1];
      data[0] = OVERRIDE_COLLISION_DETECTION_MASK;
      udp.send(data);
    }
    else if (e.getItemSelectable() == gui.armPotOverride)
    {
      //send a byte. This will toggle the state on th arduino. default is false
      gui.addText("toggle arm pot override");
      byte[] data = new byte[1];
      data[0] = OVERRIDE_ARM_SENSORS_MASK;
      udp.send(data);
    }
  }  
   
  //Main
  public static void main(String[] args)
  {
    ControllerMain controller = new ControllerMain();
    controller.run();
  }
}
