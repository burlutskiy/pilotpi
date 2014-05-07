package org.pilotpi.examples;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class ControlGpioExample
{

    public ControlGpioExample()
    {
    }

    public static void main(String args[])
        throws InterruptedException
    {
        System.out.println("<--Pi4J--> GPIO Control Example ... started.");
        GpioController gpio = GpioFactory.getInstance();
        GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", PinState.HIGH);
        System.out.println("--> GPIO state should be: ON");
        for(int i = 1; i < 10; i++)
        {
            Thread.sleep(5000L);
            pin.low();
            System.out.println("--> GPIO state should be: OFF");
            Thread.sleep(5000L);
            pin.toggle();
            System.out.println("--> GPIO state should be: ON");
            Thread.sleep(5000L);
            pin.toggle();
            System.out.println("--> GPIO state should be: OFF");
            Thread.sleep(5000L); 
            System.out.println("--> GPIO state should be: ON for only 1 second");
            pin.pulse(1000L, true);
        }

        gpio.shutdown();
    }
}
