package org.pilotpi.api;

import javax.vecmath.Vector3f;

public interface Gyroscope {
	void initGyro();
	void readGyro(Vector3f v);
	void update();
}
