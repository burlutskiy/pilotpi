package org.pilotpi.api;

public interface IMUListener {
	void update(double roll, double pitch, double yaw);
}
