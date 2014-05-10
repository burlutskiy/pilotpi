package org.pilotpi.math;

public class Vector
{
    private double[] vector = {0,0,0};

    public Vector(){
    }

    public Vector(double a, double b, double c){
    	vector[0] = a;
    	vector[1] = b;
    	vector[2] = c;
    }

    public Vector(double f){
    	vector[0] = vector[1] = vector[2] = f;
    }

	public double getX() {
		return vector[0];
	}

	public void setX(double x) {
		vector[0] = x;
	}

	public double getY() {
		return vector[1];
	}

	public void setY(double y) {
		vector[1] = y;
	}

	public double getZ() {
		return vector[2];
	}

	public void setZ(double z) {
		vector[2] = z;
	}
    
	public void addX(double x){
		vector[0]+=x;
	}
	public void addY(double y){
		vector[1]+=y;
	}
	public void addZ(double z){
		vector[2]+=z;
	}
	public double[] intern(){
		return vector;
	}
	public void add(Vector v)
	{
		for (int i = 0; i < v.intern().length; i++) {
			vector[i]+=v.intern()[i];
		}
	}
    public void devide(double f){
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

	public void set(double x, double y, double z) {
		vector[0] = x;
		vector[1] = y;
		vector[2] = z;		
	}
}