package org.pilotpi.api;

public interface IMUListener {
	void update(float roll, float pitch, float yaw);
}
