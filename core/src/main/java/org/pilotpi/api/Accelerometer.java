package org.pilotpi.api;

public interface Accelerometer {
	
	void init();
	void readXYZ(int[] xyz);
	void readXYZ(int[] xyz, int offset);
	int readX();
	int readY();
	int readZ();
	void update();
}
