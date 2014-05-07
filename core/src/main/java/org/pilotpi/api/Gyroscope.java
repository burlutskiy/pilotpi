package org.pilotpi.api;

public interface Gyroscope {
	void init();
	void readXYZ(int[] xyz);
	void readXYZ(int[] xyz, int offset);
	int readX();
	int readY();
	int readZ();
	void update();
}
