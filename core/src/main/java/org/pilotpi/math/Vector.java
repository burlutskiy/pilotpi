package org.pilotpi.math;

public class Vector
{
    private float x;
    private float y;
    private float z;

    public Vector(){
        x = y = z = 0f;
    }

    public Vector(float a, float b, float c){
        x = a;
        y = b;
        z = c;
    }

    public Vector(float f){
        x = y = z = f;
    }

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}
    
	public void addX(float x){
		this.x+=x;
	}
	public void addY(float y){
		this.y+=y;
	}
	public void addZ(float z){
		this.z+=z;
	}
	public void add(Vector v){
		this.x+=v.x;
		this.y+=v.y;
		this.z+=v.z;
	}
    public void devide(float f){
    	this.x /= f;
    	this.y /= f;
    	this.z /= f;
    }

	public void substract(Vector v) {
		this.x-=v.x;
		this.y-=v.y;
		this.z-=v.z;
	}

	public void multiply(Vector v) {
    	this.x *= v.x;
    	this.y *= v.y;
    	this.z *= v.z;
	}

	public void set(Vector v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	public void clear() {
		x = y = z = 0f;		
	}

	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;		
	}
}