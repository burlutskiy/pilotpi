// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SerialExample.java

package org.pilotpi.examples;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.SerialPortException;

public class SerialExample
{

    public SerialExample()
    {
    }

    public static void main(String args[])
        throws InterruptedException
    {
        System.out.println("<--Pi4J--> Serial Communication UART ... started.");
        Serial serial = SerialFactory.createInstance();
        GpioController gpio = GpioFactory.getInstance();
        GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "Reset", PinState.LOW);
        pin.high();
        pin.low();
        System.out.println("UART reset.");
        serial.addListener(new SerialDataListener[] {
            new SerialDataListener() {

                public void dataReceived(SerialDataEvent serialdataevent)
                {
                }

            }

        });
        try
        {
            serial.open("/dev/ttyAMA0", 38400);
            serial.write((byte)-128);
            serial.write((byte)1);
            serial.write((byte)4);
            serial.write((byte)0);
            serial.write((byte)23);
            serial.write((byte)56);
            serial.flush();
            System.out.println("Servo initialized.");
            do
            {
                String command = System.console().readLine();
                short position = Short.parseShort(command);
                try
                {
                    serial.write((byte)-128);
                    serial.write((byte)1);
                    serial.write((byte)4);
                    serial.write((byte)0);
                    serial.write((byte)(position >> 7 & 0x7f));
                    serial.write((byte)(position & 0x7f));
                    serial.flush();
                }
                catch(IllegalStateException ex)
                {
                    ex.printStackTrace();
                }
            } while(true);
        }
        catch(SerialPortException ex)
        {
            System.out.println((new StringBuilder(" ==>> SERIAL SETUP FAILED : ")).append(ex.getMessage()).toString());
        }
    }
}
