// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Constants.java

package org.pilotpi.sensors.lps;


public interface Constants
{

    public static final byte LPS331_SA0_LOW = 0;
    public static final byte LPS331_SA0_HIGH = 1;
    public static final byte LPS331_SA0_AUTO = 2;
    public static final byte LPS331_REF_P_XL = 8;
    public static final byte LPS331_REF_P_L = 9;
    public static final byte LPS331_REF_P_H = 10;
    public static final byte LPS331_WHO_AM_I = 15;
    public static final byte LPS331_RES_CONF = 16;
    public static final byte LPS331_CTRL_REG1 = 32;
    public static final byte LPS331_CTRL_REG2 = 33;
    public static final byte LPS331_CTRL_REG3 = 34;
    public static final byte LPS331_INTERRUPT_CFG = 35;
    public static final byte LPS331_INT_SOURCE = 36;
    public static final byte LPS331_THS_P_L = 37;
    public static final byte LPS331_THS_P_H = 38;
    public static final byte LPS331_STATUS_REG = 39;
    public static final byte LPS331_PRESS_OUT_XL = 40;
    public static final byte LPS331_PRESS_OUT_L = 41;
    public static final byte LPS331_PRESS_OUT_H = 42;
    public static final byte LPS331_TEMP_OUT_L = 43;
    public static final byte LPS331_TEMP_OUT_H = 44;
    public static final byte LPS331_AMP_CTRL = 48;
    public static final byte LPS331_DELTA_PRESS_XL = 60;
    public static final byte LPS331_DELTA_PRESS_L = 61;
    public static final byte LPS331_DELTA_PRESS_H = 62;
    public static final byte LPS331AP_ADDRESS_SA0_LOW = 92;
    public static final byte LPS331AP_ADDRESS_SA0_HIGH = 93;
}
