/*
 * Copyright (C) 2010-2013 Mauricio Araya
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package libsdm.common; 

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Arrays;

/** A dense array of double values.
 * This class provides an efficient implementation of dense vs sparse computations.
 * @author Mauricio Araya
 */
public class DenseVector implements Serializable{


	private static final long serialVersionUID = 8494095501573100178L;
	
	/** Print numbers in scientific format */
	public static boolean scientific=false;
	
	protected double[] data;
	
	/** Creates a dense vector from a double array.
	 * 
	 * @param array double array to copy
	 */
	public DenseVector(double[] array) {   	
		data=new double[array.length];
		System.arraycopy(array, 0, data, 0, array.length);

	}
	
	/** Creates an empty dense vector.
	 * 
	 * @param length the size of the vector
	 */
	public DenseVector(int length) {
		data=new double[length];
	}
	
	/** Creates a dense vector from another dense vector.
	 * 
	 * @param vector dense vector to copy
	 */
	public DenseVector(DenseVector vector) {
		this(vector,false);
	}

	/** Creates a dense vector from another dense vector.
	 * 
	 * @param vector dense vector
	 * @param shallow makes a shallow copy if true
	 */ 
	protected DenseVector(DenseVector vector, boolean shallow) {
		if (shallow){
			data=vector.data;
		}else{
			data=new double[vector.data.length];
			System.arraycopy(vector.data, 0, data, 0, vector.data.length);
		}
	}
	
	/** String representation of a segment of the vector.
	 *  
	 * @param from where it starts
	 * @param to where it ends
	 * @return
	 */
	public String toString(int from, int to) {
		String retval="";
		DecimalFormat df;
		if (scientific)
			df = new DecimalFormat("0.00E00");
		else
			df = new DecimalFormat("0.000");

		for (int i=from;i<to;i++){
			retval+=df.format(data[i]) + " ";
		}
		return retval;
	}
	
	@Override
	public String toString() {
		return this.toString(0,data.length);
	}

	/** Set a value of the vector
	 * 
	 * @param i index of the vector
	 * @param v value of the element
	 */
	public void set(int i, double v) {
		data[i]=v;
	}

	/** Get a value of the vector.
	 * 
	 * @param i element
	 * @return the value of i
	 */
	public double get(int i) {
		return data[i];
	}

	/** Copy the vector
	 * 
	 * @return the copy
	 */
	public DenseVector copy() {
		return new DenseVector(data);
	}

	/** Subtracts v to the current vector
	 * 
	 * @param v the vector to subtract
	 */
	public void minus(DenseVector v) {
		for (int i=0; i< data.length;i++){
			data[i]=data[i] - v.data[i];
		}

	}

	/** Compute the norm of the vector.
	 * The supported norms are: 
	 * 	- infinity = Double.POSITIVE_INFINITY
	 * 	- euclidean = 2
	 *  - taxicab = 1   
	 * 
	 * @param p the selected p-norm
	 * @return the norm value
	 */
	public double norm(double p) {
		if (p == Double.POSITIVE_INFINITY) {
			double mv = 0;
			mv = Double.NEGATIVE_INFINITY;
			for (double val : data) {
				if (Math.abs(val) > mv)
					mv = Math.abs(val);
			}
			return mv;
		}
		double sum = 0;
		switch ((int) p) {
		case 1:
			for (double val : data) {
				sum += Math.abs(val);
			}
			return sum;
		case 2:
			for (double val : data) {
				sum += val * val;
			}
			return sum;
		default:
			System.out.println("Norm not supported (norm " + p + ")");
			return 0;
		}
	}

	/** Scales the vector.
	 * 
	 * @param gamma the scalar to multiply
	 */
	public void scale(double gamma) {
		for (int i=0;i<data.length;i++) {
			data[i] *= gamma;
		}

	}

	/** 
	 * Efficient addition of a sparse vector values.
	 * @param rv the sparse vector to add
	 */
	public void add(SparseVector rv) {
		if (rv.index==null) return;
		for (int i=0;i<rv.index.length;i++){
			data[rv.index[i]]+=rv.data[i];
		}
	}

	/** 
	 * Efficient dot product with a sparse vector.
	 * @param rv the sparse vector
	 */
	public double dot(SparseVector rv) {
		double sum=0;
		if (rv.index==null) return 0;
		for (int i=0;i<rv.index.length;i++){
			sum+=data[rv.index[i]]*rv.data[i];
		}
		return sum;
	}

	/** Get the array of elements in double format.
	 * This method returns the reference to the internal data structure, not a copy.
	 * @return the reference of the internal double format representation of the vector.
	 */
	public double[] getArray() {
		return data;
	}

	/** Fill the vector with a constant value
	 * 
	 * @param val the constant value.
	 */
	public void fill(double val) {
		Arrays.fill(data, val);
	}

	/** Efficient selection of the max values from this vector and a sparse vector.
	 * 
	 * @param vec vector
	 */
	public void selectMax(SparseVector vec) {
		boolean notest=false;
		if (vec.index==null) notest=true; 
		int idx=0;
		for (int i=0;i<data.length;i++){
			if (notest || idx >= vec.index.length || i!=vec.index[idx])
				data[i]=Math.max(data[i], 0);
			else{
				data[i]=Math.max(data[i], vec.data[idx]);
				idx++;
			}
		}

	}

	/** Size of the vector.
	 * 
	 * @return the size
	 */
	public int size() {
		return data.length;
	}

	/** Minimum value
	 * 
	 * @return minimum value
	 */
	public double min() {
		double res=Double.POSITIVE_INFINITY;
		for (int i=0;i<data.length;i++){
			if (data[i]< res)
				res=data[i];
		}
		return res;
	}

	/** Compare with a dense vector (with tolerance)
	 * 
	 * @param v the vector to compare to
	 * @param delta the tolerance
	 * @return 0 if is delta-close, 1 if the first non delta-close element is larger that v, and -1 otherwise.  
	 */
	public int compare(DenseVector v, double delta) {
		for (int i=0;i<data.length;i++){
			if (data[i] > v.data[i] + delta)
				return 1;
			if (data[i] < v.data[i] - delta)
				return -1;
		}
		return 0;
	}

	/** Copy the elements of v into this vector
	 * 
	 * @param v
	 */
	protected void set(DenseVector v) {
		System.arraycopy(v.data, 0, data, 0, v.data.length);

	}

	/** Adds the values of a dense vector to this vector.
	 * 
	 * @param v
	 */
	public void add(DenseVector v) {
		for (int i=0;i<data.length;i++){
			data[i]+=v.data[i];
		}
	}

	/** Selection of the max values from this vector and other vector.
	 * 
	 * @param vector
	 */
	public void selectMax(DenseVector vector) {
		for (int i=0;i<data.length;i++){
			data[i]=Math.max(data[i],vector.data[i]);
		}
	}

	/* Static Methods */
	/** Returns an homogeneous vector.
	 * 
	 */
	public static DenseVector getHomogeneous(int size, double val) {
		DenseVector res = new DenseVector(size);
		for (int i=0; i< size;i++){
			res.data[i]=val;
		}
		return res;

	}

	/* Potentially useful Commented Methods */ 
    /*
	public Double naiveHash(){
  		double sum=0;
  		for (int i=0;i<data.length;i++){
  			sum+=data[i]*i;
  		}
  		return new Double(sum);
  	}*/

}
