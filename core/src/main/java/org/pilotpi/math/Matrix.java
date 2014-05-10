package org.pilotpi.math;

public class Matrix {

	private double[][] matrix = {
			{0,0,0},
			{0,0,0},
			{0,0,0}
		};
	public Matrix(Vector vector1, Vector vector2, Vector vector3) {
		for (int i = 0; i < 3; i++) {
			matrix[0][i]+=vector1.intern()[i];
		}
		for (int i = 0; i < 3; i++) {
			matrix[1][i]+=vector2.intern()[i];
		}
		for (int i = 0; i < 3; i++) {
			matrix[2][i]+=vector3.intern()[i];
		}
	}
	public Matrix() {
	}
	public Vector getRow(int i) {
		// TODO Auto-generated method stub
		return null;
	}
}
