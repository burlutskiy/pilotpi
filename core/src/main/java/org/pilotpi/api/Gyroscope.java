package org.pilotpi.api;

import javax.vecmath.Vector3f;

public interface Gyroscope {
	void init();
	void readGyro(Vector3f v);
	void update();
}
