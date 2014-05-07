package org.pilotpi.math;

public class Vector
{
    private int x;
    private int y;
    private int z;

    public Vector()
    {
        x = y = z = 0;
    }

    public Vector(float a, float b, float c)
    {
        x = (int)a;
        y = (int)b;
        z = (int)c;
    }

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}
    
    
}