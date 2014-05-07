// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Constants.java

package org.pilotpi.sensors.lsm;


public interface Constants
{
//	public static final byte  D_SA0_HIGH_ADDRESS              =0b0011101; // D with SA0 high
//	public static final byte  D_SA0_LOW_ADDRESS               =0b0011110; // D with SA0 low or non-D magnetometer
//	public static final byte  NON_D_MAG_ADDRESS               =0b0011110; // D with SA0 low or non-D magnetometer
//	public static final byte  NON_D_ACC_SA0_LOW_ADDRESS       =0b0011000; // non-D accelerometer with SA0 low
//	public static final byte  NON_D_ACC_SA0_HIGH_ADDRESS      =0b0011001; // non-D accelerometer with SA0 high
//
//	public static final byte  TEST_REG_NACK -1
//	public static final byte  D_WHO_ID    0x49
//	public static final byte  DLM_WHO_ID  0x3C
	
    public static final byte TEMP_OUT_L = 5;
    public static final byte TEMP_OUT_H = 6;
    public static final byte STATUS_M = 7;
    public static final byte INT_CTRL_M = 18;
    public static final byte INT_SRC_M = 19;
    public static final byte INT_THS_L_M = 20;
    public static final byte INT_THS_H_M = 21;
    public static final byte OFFSET_X_L_M = 22;
    public static final byte OFFSET_X_H_M = 23;
    public static final byte OFFSET_Y_L_M = 24;
    public static final byte OFFSET_Y_H_M = 25;
    public static final byte OFFSET_Z_L_M = 26;
    public static final byte OFFSET_Z_H_M = 27;
    public static final byte REFERENCE_X = 28;
    public static final byte REFERENCE_Y = 29;
    public static final byte REFERENCE_Z = 30;
    public static final byte CTRL0 = 31;
    public static final byte CTRL1 = 32;
    public static final byte CTRL_REG1_A = 32;
    public static final byte CTRL2 = 33;
    public static final byte CTRL_REG2_A = 33;
    public static final byte CTRL3 = 34;
    public static final byte CTRL_REG3_A = 34;
    public static final byte CTRL4 = 35;
    public static final byte CTRL_REG4_A = 35;
    public static final byte CTRL5 = 36;
    public static final byte CTRL_REG5_A = 36;
    public static final byte CTRL6 = 37;
    public static final byte CTRL_REG6_A = 37;
    public static final byte HP_FILTER_RESET_A = 37;
    public static final byte CTRL7 = 38;
    public static final byte REFERENCE_A = 38;
    public static final byte STATUS_A = 39;
    public static final byte STATUS_REG_A = 39;
    public static final byte OUT_X_L_A = 40;
    public static final byte OUT_X_H_A = 41;
    public static final byte OUT_Y_L_A = 42;
    public static final byte OUT_Y_H_A = 43;
    public static final byte OUT_Z_L_A = 44;
    public static final byte OUT_Z_H_A = 45;
    public static final byte FIFO_CTRL = 46;
    public static final byte FIFO_CTRL_REG_A = 46;
    public static final byte FIFO_SRC = 47;
    public static final byte FIFO_SRC_REG_A = 47;
    public static final byte IG_CFG1 = 48;
    public static final byte INT1_CFG_A = 48;
    public static final byte IG_SRC1 = 49;
    public static final byte INT1_SRC_A = 49;
    public static final byte IG_THS1 = 50;
    public static final byte INT1_THS_A = 50;
    public static final byte IG_DUR1 = 51;
    public static final byte INT1_DURATION_A = 51;
    public static final byte IG_CFG2 = 52;
    public static final byte INT2_CFG_A = 52;
    public static final byte IG_SRC2 = 53;
    public static final byte INT2_SRC_A = 53;
    public static final byte IG_THS2 = 54;
    public static final byte INT2_THS_A = 54;
    public static final byte IG_DUR2 = 55;
    public static final byte INT2_DURATION_A = 55;
    public static final byte CLICK_CFG = 56;
    public static final byte CLICK_CFG_A = 56;
    public static final byte CLICK_SRC = 57;
    public static final byte CLICK_SRC_A = 57;
    public static final byte CLICK_THS = 58;
    public static final byte CLICK_THS_A = 58;
    public static final byte TIME_LIMIT = 59;
    public static final byte TIME_LIMIT_A = 59;
    public static final byte TIME_LATENCY = 60;
    public static final byte TIME_LATENCY_A = 60;
    public static final byte TIME_WINDOW = 61;
    public static final byte TIME_WINDOW_A = 61;
    public static final byte Act_THS = 62;
    public static final byte Act_DUR = 63;
    public static final byte CRA_REG_M = 0;
    public static final byte CRB_REG_M = 1;
    public static final byte MR_REG_M = 2;
    public static final byte SR_REG_M = 9;
    public static final byte IRA_REG_M = 10;
    public static final byte IRB_REG_M = 11;
    public static final byte IRC_REG_M = 12;
    public static final byte WHO_AM_I_M = 15;
    public static final byte WHO_AM_I = 15;
    public static final byte TEMP_OUT_H_M = 49;
    public static final byte TEMP_OUT_L_M = 50;
    public static final byte OUT_X_H_M = -1;
    public static final byte OUT_X_L_M = -2;
    public static final byte OUT_Y_H_M = -3;
    public static final byte OUT_Y_L_M = -4;
    public static final byte OUT_Z_H_M = -5;
    public static final byte OUT_Z_L_M = -6;
    public static final byte DLH_OUT_X_H_M = 3;
    public static final byte DLH_OUT_X_L_M = 4;
    public static final byte DLH_OUT_Y_H_M = 5;
    public static final byte DLH_OUT_Y_L_M = 6;
    public static final byte DLH_OUT_Z_H_M = 7;
    public static final byte DLH_OUT_Z_L_M = 8;
    public static final byte DLM_OUT_X_H_M = 3;
    public static final byte DLM_OUT_X_L_M = 4;
    public static final byte DLM_OUT_Z_H_M = 5;
    public static final byte DLM_OUT_Z_L_M = 6;
    public static final byte DLM_OUT_Y_H_M = 7;
    public static final byte DLM_OUT_Y_L_M = 8;
    public static final byte DLHC_OUT_X_H_M = 3;
    public static final byte DLHC_OUT_X_L_M = 4;
    public static final byte DLHC_OUT_Z_H_M = 5;
    public static final byte DLHC_OUT_Z_L_M = 6;
    public static final byte DLHC_OUT_Y_H_M = 7;
    public static final byte DLHC_OUT_Y_L_M = 8;
    public static final byte D_OUT_X_L_M = 8;
    public static final byte D_OUT_X_H_M = 9;
    public static final byte D_OUT_Y_L_M = 10;
    public static final byte D_OUT_Y_H_M = 11;
    public static final byte D_OUT_Z_L_M = 12;
    public static final byte D_OUT_Z_H_M = 13;
    public static final byte D_SA0_HIGH_ADDRESS = 29;
    public static final byte D_SA0_LOW_ADDRESS = 30;
    public static final byte NON_D_MAG_ADDRESS = 30;
    public static final byte NON_D_ACC_SA0_LOW_ADDRESS = 24;
    public static final byte NON_D_ACC_SA0_HIGH_ADDRESS = 25;
    public static final byte TEST_REG_NACK = -1;
    public static final byte D_WHO_ID = 73;
    public static final byte DLM_WHO_ID = 60;
}
