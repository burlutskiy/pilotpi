/**
 * 
 */
package org.pilotpi.api;

import javax.vecmath.Vector3d;

/**
 * @author alexey
 *
 */
public class Vector extends javax.vecmath.Vector3d {

	public Vector(int i, int j, int k) {
		super(i,j,k);
	}

	public Vector(double f, double g, double h) {
		super(f,g,h);
	}

	public Vector() {
		super();
	}

	public void multiply(Vector3d v){
		this.x*=v.x;
		this.y*=v.y;
		this.z*=v.z;
	}
}
