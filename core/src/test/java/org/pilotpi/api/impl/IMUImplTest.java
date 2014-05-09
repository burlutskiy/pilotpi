package org.pilotpi.api.impl;

import java.util.concurrent.Executors;

import javax.vecmath.Vector3f;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.pilotpi.api.Accelerometer;
import org.pilotpi.api.Gyroscope;
import org.pilotpi.api.Magnetometer;

@RunWith(MockitoJUnitRunner.class)
public class IMUImplTest {

	@InjectMocks
	IMUImpl imuImpl = new IMUImpl();
	
	@Mock
	Gyroscope gyroscope;
	@Mock
	Magnetometer magnetometer;
	@Mock
	Accelerometer accelerometer;
	
	@Test
	public void test() throws InterruptedException{
		Answer<Object> gyroAnswer = new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Vector3f gyro = (Vector3f) invocation.getArguments()[0];
				gyro.set(1,1,1);
				return null;
			}
		};
		Answer<Object> magAnswer = new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Vector3f mag = (Vector3f) invocation.getArguments()[0];
				mag.set(1,1,1);
				return null;
			}
		};
		Answer<Object> accAnswer = new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Vector3f acc = (Vector3f) invocation.getArguments()[0];
				acc.set(1,1,1);
				return null;
			}
		};
		Mockito.doAnswer(gyroAnswer).when(gyroscope).readGyro((Vector3f) Mockito.any());
		Mockito.doAnswer(magAnswer).when(magnetometer).readMag((Vector3f) Mockito.any());
		Mockito.doAnswer(accAnswer).when(accelerometer).readAcc((Vector3f) Mockito.any());
		
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				imuImpl.stop();
			}
		});
		imuImpl.init();
		imuImpl.start();
		Assert.assertTrue(imuImpl.getRoll() != 0);
		Assert.assertTrue(imuImpl.getPitch() != 0);
		Assert.assertTrue(imuImpl.getYaw() != 0);
	}
}
