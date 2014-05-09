package org.pilotpi.math;

public class Vector
{
    private float[] vector = {0,0,0};

    public Vector(){
    }

    public Vector(float a, float b, float c){
    	vector[0] = a;
    	vector[1] = b;
    	vector[2] = c;
    }

    public Vector(float f){
    	vector[0] = vector[1] = vector[2] = f;
    }

	public float getX() {
		return vector[0];
	}

	public void setX(float x) {
		vector[0] = x;
	}

	public float getY() {
		return vector[1];
	}

	public void setY(float y) {
		vector[1] = y;
	}

	public float getZ() {
		return vector[2];
	}

	public void setZ(float z) {
		vector[2] = z;
	}
    
	public void addX(float x){
		vector[0]+=x;
	}
	public void addY(float y){
		vector[1]+=y;
	}
	public void addZ(float z){
		vector[2]+=z;
	}
	public float[] intern(){
		return vector;
	}
	public void add(Vector v)
	{
		for (int i = 0; i < v.intern().length; i++) {
			vector[i]+=v.intern()[i];
		}
	}
    public void devide(float f){
    	vector[0] /= f;
    	vector[1] /= f;
    	vector[2] /= f;
    }

	public void substract(Vector v) {
		for (int i = 0; i < v.intern().length; i++) {
			vector[i]-=v.intern()[i];
		}
	}

	public void multiply(Vector v) {
		for (int i = 0; i < v.intern().length; i++) {
			vector[i]*=v.intern()[i];
		}
	}

	public void set(Vector v) {
		for (int i = 0; i < v.intern().length; i++) {
			vector[i] = v.intern()[i];
		}
	}

	public void clear() {
		for (int i = 0; i < 3; i++) {
			vector[i] = 0;
		}
	}

	public void set(float x, float y, float z) {
		vector[0] = x;
		vector[1] = y;
		vector[2] = z;		
	}
}