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
import java.util.Random;

/** A sparse vector of double values.
 * This class provides an efficient implementation of dense vs sparse, and sparse vs sparse computations.
 * @author Mauricio Araya
 */
public class SparseVector implements Serializable{

	protected double[] data;

	/**
	 * Indices to data
	 */
	protected int[] index;

	/**
	 * The size of the vector (with zeros)
	 */
	int size;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8494095501573100178L;

	public SparseVector(int length) {
		size = length;
		index = null;
		data = null;
	}

	public SparseVector(SparseVector cv, boolean shallow) {
		this(cv.size());
		if (shallow) {
			data = cv.data;
			index = cv.index;
			size = cv.size;

		} else {
			set(cv);
		}

	}

	public SparseVector(SparseVector cv) {
		this(cv, false);
	}

	public SparseVector(double[] list) {
		int idx = 0;
		size = list.length;
		index = new int[size];
		data = new double[size];
		int capacity = 0;
		for (double value : list) {
			if (list[idx] != 0.0) {
				index[capacity] = idx;
				data[capacity] = value;
				capacity++;
			}
			idx++;
		}
		shrink(capacity);
	}

	public SparseVector(int[] index2, double[] data2, int idx) {
		index = index2;
		data = data2;
		size = index2.length;
		shrink(idx);
	}

	public static SparseVector getHomogene(int siz, double val) {

		SparseVector newVec = new SparseVector(siz);
		if (val != 0) {
			newVec.index = new int[siz];
			newVec.data = new double[siz];
			for (int i = 0; i < siz; i++) {
				newVec.index[i] = i;
				newVec.data[i] = val;
			}
		}
		return newVec;
	}

	public static SparseVector getRandomUnitary(int dim) {
		double ran[] = new double[dim];
		for (int i = 0; i < dim; i++)
			ran[i] = Utils.gen.nextDouble();
		SparseVector retval = new SparseVector(ran);
		retval.normalize();
		return retval;
	}
	
	public static SparseVector getUniform(int siz) {
		SparseVector newVec = new SparseVector(siz);
		newVec.index = new int[siz];
		newVec.data = new double[siz];
		for (int i = 0; i < siz; i++) {
			newVec.index[i] = i;
			newVec.data[i] = 1.0 / siz;;
		}
		return newVec;
	}
	
	public void fill(double value) {
		data=new double[size];
		index=new int[size];
		for (int i = 0; i < size; i++) {
			index[i] = i;
			data[i] = value;
		}	
	}

	public void selectMax(SparseVector vec) {
		if (vec.size != size)
			Utils.error("Different SparseVector Sizes");
		if (index==null && vec.index==null) 
			return;
		int top;
		double[] newdata;
		int[] newindex;
		int idx;
		if (index == null) {
			top=vec.index.length;
			idx=0;
			newdata = new double[top];
			newindex = new int[top];
			for (int i=0;i<vec.index.length;i++){
				if (vec.data[i]>0){
					newindex[idx]=vec.index[i];
					newdata[idx]=vec.data[i];
				}
			}
		}
		else if (vec.index == null) {
			top=index.length;
			idx=0;
			newdata = new double[top];
			newindex = new int[top];
			for (int i=0;i<index.length;i++){
				if (data[i]>0){
					newindex[idx]=index[i];
					newdata[idx]=data[i];
				}
			}
		}
		else {
			top = Math.min(size, vec.data.length + data.length);
			newdata = new double[top];
			newindex = new int[top];
			int i = 0;
			int j = 0;
			idx = 0;
			while (i < index.length) {
				while (j < vec.index.length && index[i] > vec.index[j]) {
					if (vec.data[j]>0){
						newindex[idx] = vec.index[j];
						newdata[idx] = vec.data[j];
						idx++;
					}
					j++;
			}
			//System.out.println("i iter ("+i+","+j+")=("+index[i]+","+cv.index[j]+")");
			if (j < vec.index.length && index[i] == vec.index[j]) {
				double val;
				val = Math.max(data[i],vec.data[j]);
				//System.out.println("index["+idx+"]=other.index["+j+"] = this.index["+i+"] ="+index[i]);
				//System.out.println("data["+idx+"]=other.data["+j+"] + this.data["+i+"] = "+cv.data[j]+" + "+data[i]+" = "+val);
				newindex[idx] = index[i];
				newdata[idx] = val;
				idx++;
				j++;
			} else {
				//System.out.println("index["+idx+"]=this.index["+i+"]="+index[i]);
				//System.out.println("data["+idx+"]=this.data["+i+"]="+data[i]);
				if (data[j]>0){
					newindex[idx] = index[i];
					newdata[idx] = data[i];	
					idx++;
				}
			}
			i++;
			}
		}
		if (idx==0){
			index=null;
			data=null;
			return;
		}
		index=newindex;
		data=newdata;
		shrink(idx);
	}
	



	private void shrink(int capacity) {
		if (capacity == 0 || index==null){
			index=null;
			data=null;
			return;
		}
		if (capacity == data.length)
			return;
		data = Arrays.copyOf(data, capacity);
		index = Arrays.copyOf(index, capacity);
	}

	private void setQuick(int i, double d) {
		if (d == 0.0) {
			if (index == null) {
				return;
			}
			int pos = Arrays.binarySearch(index, i);
			if (pos < 0)
				return;
			//System.out.println("pos="+pos);
			double[] newdata = new double[data.length - 1];
			int[] newindex = new int[index.length - 1];
			System.arraycopy(index, 0, newindex, 0, pos);
			System.arraycopy(data, 0, newdata, 0, pos);
			System.arraycopy(index, pos + 1, newindex, pos, index.length - pos - 1);
			System.arraycopy(data, pos + 1, newdata, pos, data.length - pos - 1);
			if (newindex.length==0){
				newindex=null;
				newdata=null;
			}
			data = newdata;
			index = newindex;
			return;
		}
		if (index == null) {
			index = new int[1];
			data = new double[1];
			index[0] = i;
			data[0] = d;
			return;
		}
		int pos = Arrays.binarySearch(index, i);
		if (pos < 0) {
			pos = -pos - 1;
			double[] newdata = new double[data.length + 1];
			int[] newindex = new int[index.length + 1];
			System.arraycopy(index, 0, newindex, 0, pos);
			System.arraycopy(data, 0, newdata, 0, pos);
			newindex[pos] = i;
			newdata[pos] = d;
			System.arraycopy(index, pos, newindex, pos + 1, index.length - pos);
			System.arraycopy(data, pos, newdata, pos + 1, data.length - pos);
			data = newdata;
			index = newindex;
		} else {
			data[pos] = d;
		}
	}

	private double getQuick(int s) {
		if (index==null) return 0;
		int pos = Arrays.binarySearch(index, s);
		//System.out.println(pos);
		if (pos < 0)
			return 0;
		return data[pos];
	}

	protected void set(SparseVector res) {
		if (res.data==null){
			data=null;
			index=null;
			size = res.size;
			return;
		}
		data = new double[res.data.length];
		data = Arrays.copyOf(res.data, res.data.length);
		index = new int[res.index.length];
		index = Arrays.copyOf(res.index, res.index.length);
		size = res.size;
	}

	public Double naiveHash() {
		if (index==null) return new Double(0);
		double sum = 0;
		for (int i = 0; i < data.length; i++) {
			sum += data[i] * (index[i] + 1);
		}
		return new Double(sum);
	}

	public String toString(int v1, int v2) {
		String retval = "";
		//DecimalFormat df = new DecimalFormat("0.00E00");
		DecimalFormat df = new DecimalFormat("0.000");
		for (int i = v1; i < v2; i++) {
			retval += df.format(getQuick(i)) + " ";
		}
		return retval;
	}

	public String toString() {
		return this.toString(0, size);
	}

	public double dot(SparseVector cv) {

		double res = 0;
		if (index == null || cv.index == null)
			return res;
		if (index.length == size && cv.index.length == cv.size) {
			for (int k = 0; k < size; k++)
				res += data[k] * cv.data[k];
			return res;
		}
		int i = 0;
		int j = 0;
		while (i < index.length) {
			while (index[i] > cv.index[j]) {
				j++;
				if (j == cv.index.length)
					return res;
			}
			if (index[i] == cv.index[j])
				res += data[i] * cv.data[j];
			i++;
		}
		return res;
	}

	public SparseVector copy() {
		return (new SparseVector(this));
	}

	public double norm(double d) {
		if (index==null) return 0;
		// Infinite Norm
		if (d == Double.POSITIVE_INFINITY) {
			double mv = 0;
			if (data.length == size)
				mv = Double.NEGATIVE_INFINITY;
			for (double val : data) {
				if (Math.abs(val) > mv)
					mv = Math.abs(val);
			}
			return mv;
		}
		double sum = 0;
		switch ((int) d) {
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
			System.out.println("Norm not supported (norm " + d + ")");
			return 0;
		}
	}

	public void scale(double d) {
		if (index==null) return;
		if (d == 0.0) {
			data = null;
			index = null;
			return;
		}
		for (int i = 0; i < data.length; i++) {
			data[i] *= d;
		}
		return;
	}

	public double get(int s) {
		if (s >= size) {
			Utils.error("Get exceeds vector dimension");
			return Double.NaN;
		}
		return getQuick(s);
	}

	public int size() {
		return size;
	}

	public void addInternal(SparseVector cv,boolean add) {
		if (cv.size != size)
			Utils.error("Different SparseVector Sizes");
		if (index == null) {
			set(cv);
			return;
		}
		if (cv.index == null) {
			return;
		}
		int top = Math.min(size, cv.data.length + data.length);
		double[] newdata = new double[top];
		int[] newindex = new int[top];
		int i = 0;
		int j = 0;
		int idx = 0;
		while (i < index.length) {
			while (j < cv.index.length && index[i] > cv.index[j]) {
				//System.out.println("index["+idx+"]=other.index["+j+"]="+cv.index[j]);
				//System.out.println("data["+idx+"]=other.data["+j+"]="+cv.data[j]);
				newindex[idx] = cv.index[j];
				newdata[idx] = cv.data[j];
				idx++;
				j++;
			}
			//System.out.println("i iter ("+i+","+j+")=("+index[i]+","+cv.index[j]+")");
			if (j < cv.index.length && index[i] == cv.index[j]) {
				double val;
				if (add) val = data[i] + cv.data[j];
				else val = data[i] -cv.data[j];
				if (val != 0) {
					//System.out.println("index["+idx+"]=other.index["+j+"] = this.index["+i+"] ="+index[i]);
					//System.out.println("data["+idx+"]=other.data["+j+"] + this.data["+i+"] = "+cv.data[j]+" + "+data[i]+" = "+val);
					newindex[idx] = index[i];
					newdata[idx] = val;
					idx++;
				}
				j++;
			} else {
				//System.out.println("index["+idx+"]=this.index["+i+"]="+index[i]);
				//System.out.println("data["+idx+"]=this.data["+i+"]="+data[i]);
				newindex[idx] = index[i];
				newdata[idx] = data[i];
				
				idx++;
			}
			i++;
		}
		if (idx==0){
			index=null;
			data=null;
			return;
		}
		index=newindex;
		data=newdata;
		shrink(idx);
	}

	public void minus(SparseVector cv) {
		addInternal(cv,false);
	}

	public void add(SparseVector cv) {
		addInternal(cv,true);
	}
	
	public double max() {
		if (index==null) return 0;
		double mv = 0;
		if (data.length == size)
			mv = Double.NEGATIVE_INFINITY;
		for (double val : data) {
			if (val > mv)
				mv = val;
		}
		return mv;
	}

	public double min() {
		if (index==null) return 0;
		double mv = 0;
		if (data.length == size)
			mv = Double.POSITIVE_INFINITY;
		for (double val : data) {
			if (val < mv)
				mv = val;
		}
		return mv;
	}

	/*public boolean compare(SparseVector res) {
		
		if (data.length!=res.data.length)
			return false;
		for (int i = 0; i < data.length; i++) {
			if (index[i] != res.index[i])
				return false;
			if (data[i] != res.data[i]) {
				return false;
			}
		}
		return true;
	}*/


	public int compare(SparseVector cv, double delta) {
		int i = 0;
		int j = 0;
		if (index == null && cv.index == null)
			return 0;
		if (index == null) {
			double m = max();
			if (m > delta)
				return 1;
			if (m < -delta)
				return -1;
			return 0;
		}
		if (cv.index == null) {
			double m = cv.max();
			if (m > delta)
				return -1;
			if (m < -delta)
				return 1;
			return 0;
		}
		while (i < index.length) {
			while (j < cv.index.length && index[i] > cv.index[j]) {
				if (cv.data[j] > delta)
					return -1;
				if (cv.data[j] < -delta)
					return 1;
				j++;
				if (j == cv.index.length)
					break;
			}
			if (index[i] == cv.index[j]) {
				if (data[i] > cv.data[j] + delta)
					return 1;
				if (data[i] < cv.data[j] - delta)
					return -1;
			} else {
				if (data[j] > delta)
					return 1;
				if (data[j] < -delta)
					return -1;
			}
			i++;
		}
		return 0;
	}

	// COMPLEX Check... tolerance of 0!
	// for (int i = 0; i < v.size(); i++) {
	// if (v.get(i) > vprime.get(i) + delta)
	// return 1;
	// if (v.get(i) < vprime.get(i) - delta)
	// return -1;
	// }
	// return 0;
	// }

	//public int compareTo(SparseVector arg0) {
	//	return compareTo(arg0, 0.0);
	//}

	/*
	 * public double cumulate() { double cum=0; for (int i=0;i<v.size();i++){
	 * cum+=v.get(i); v.set(i,cum); } return cum; }
	 */

	public int sample() {
		return sample(Utils.gen);
	}

	public void normalize() {
		double norm = norm(1);
		scale(1.0 / norm);
	}

	public int sample(Random gen) {
		if (index==null) return -1;
		double cum = 0;
		double prob = gen.nextDouble();
		for (int i = 0; i < data.length; i++) {
			cum += data[i];
			if (cum >= prob)
				return index[i];
		}
		return -1;
	}

	public void assign(int s, double v) {
		// Check who use this...
		// TODO Do not use set if you can... check!
		if (s >= size) {
			Utils.error("Set exceeds vector dimension");
			return;
		}
		setQuick(s, v);
	}
	
	/*
	 * 
	 * public SparseVector getEntropyTangent(double base) { SparseDoubleMatrix1D
	 * logv = (SparseDoubleMatrix1D) v.copy(); logv.assign(Functions.lg(base));
	 * SparseVector ret = new SparseVector(logv.size()); ret.v=logv;
	 * ret.scale(-1.0); return(ret); }
	 */
	public SparseVector getEntropyTangent(double base) {
		
		SparseVector vect = SparseVector.getHomogene(size, Double.POSITIVE_INFINITY);
		if (index==null) return vect;
		for (int i=0;i<index.length;i++){
			vect.data[index[i]]=-Math.log(data[i])/Math.log(base);
		}
		return(vect);
	}

	public void deborder(double d) {
		if (index==null){
			SparseVector v = SparseVector.getHomogene(size, d);
			data=v.data;
			index=v.index;
			return;
		}
		DenseVector ra=new DenseVector(size);
		ra.fill(d);
		ra.selectMax(this);
		this.data=ra.getArray();
		this.index=new int[size];
		for (int i=0;i<size;i++){
			index[i]=i;
		}
	}

	public double[] getArray() {
		//System.out.println(this);
		//System.out.println(index.length);
		//System.out.println(data.length);
		//System.out.println(size);
		double arr[]=new double[size];
		if (index==null) return arr;
		int idx=0;
		for (int i=0;i<size;i++){
			if (idx>=index.length) break;
			if (index[idx]==i){
				arr[i]=data[idx];
				idx++;
				
			}else
				arr[i]=0;
			
		}
		return arr;
	}

	public void applyMask(int[] mask) {
		if (index==null) return;
		int i=0;
		int j=0;
		int cap=0;
		double newdata[]=new double[data.length];
		int newindex[]=new int[index.length];
		while (i<index.length){
			while (mask.length > j && index[i] > mask[j])
				j++;
			if (mask.length == j) 
				break;
			if (index[i]==mask[j]){
				newdata[cap]=data[i];
				newindex[cap]=index[i];
				cap++;
			}
			i++;
		}
		data=newdata;
		index=newindex;
		shrink(cap);
	}

	public double getEntropy(double base) {
		if (index==null) return Double.NEGATIVE_INFINITY;
		double sum=0;
		for (int i=0;i<data.length;i++){
			sum+=data[i]*Math.log(data[i])/Math.log(base);
		}
		return -sum;
	}

	public void elementMult(SparseVector row) {
		if (index==null) return;
		int i=0;
		int j=0;
		int cap=0;
		double newdata[]=new double[data.length];
		int newindex[]=new int[index.length];
		while (i<index.length){
			while (row.index.length > j && index[i] > row.index[j])
				j++;
			//System.out.println("("+i+""+j+")=("+index[i]+","+index[j]+")");
			if (row.index.length == j) break;
			if (index[i]==row.index[j]){
				newindex[cap]=index[i];
				newdata[cap]=data[i]*row.data[j];
				cap++;
			}
			i++;
		}
		data=newdata;
		index=newindex;
		shrink(cap);

	}

	public double dot(DenseVector ra) {
		if (index==null) return 0;
		double res=0;
		for (int i=0;i<index.length;i++){
			res+=ra.data[index[i]]*data[i];
		}
		return res;
	}
	
	// Test
	public static void main(String[] args) throws Exception {
		SparseVector v1=new SparseVector(10);
		System.out.println("=== Vector Tests ===");
		v1.assign(3, 0.4);
		v1.assign(6, 0.3);
		v1.assign(7, 0.3);
		System.out.println("v1 = "+v1);
		SparseVector v2=new SparseVector(10);
		v2.set(v1);
		System.out.println("v2 = "+v2);
		v2.assign(6, 0.0);
		System.out.println("v2 = "+v2);
		System.out.println("v1 dot v2 (0.25) = "+v1.dot(v2));
		v1.elementMult(v2);
		System.out.println("v1 elemmult v2 = "+v1);
	
		v1.scale(0.5);
		System.out.println("v1 scale(0.5) = "+v1);
		v1.deborder(0.0001);
		System.out.println("v1 deborder = "+v1);
		v1.normalize();
		System.out.println("normalized v1 ="+v1);
		System.out.println("v1 entropy ="+v1.getEntropy(Math.E));
		System.out.println("v1 tangent entropy ="+v1.getEntropyTangent(Math.E));
		
		
		v2.assign(3, 0.0);
		System.out.println("v2 = "+v2);
		v2.assign(7, 0.0);
		System.out.println("v2 = "+v2);
		System.out.println("dot (0) = "+v1.dot(v2));
		v2.assign(8, 0.0);
		
		
		SparseVector v3=SparseVector.getHomogene(10, 1.0/10.0);
		System.out.println("v3 = "+v3);
		SparseVector v5=new SparseVector(10);
		v5.assign(3, 0.4);
		v5.assign(6, 0.3);
		v5.assign(7, 0.3);
		System.out.println("v5 = "+v5);
		v3.add(v5);
		System.out.println("v3 (+ v5) ="+v3);
		v3.applyMask(v5.index);
		System.out.println("masked v3 (v5) ="+v3);
		System.out.println("Comp v5 and v3 = "+v5.compare(v3,0));
		v3.minus(v5);
		System.out.println("v3 (- v5) ="+v3);
		SparseVector v4 = v3.copy();
		v4.normalize();
		System.out.println("v4 normalized(v3) ="+v4);
		System.out.println("v4 max="+v4.max()+" min="+v4.min());
		System.out.println("v4 get(1)="+v4.get(1)+" get(6)="+v4.get(6));
		System.out.println("v4 sample()="+v4.sample());
		System.out.println("v4 sample()="+v4.sample());
		System.out.println("v4 sample()="+v4.sample());
		System.out.println("v4 sample()="+v4.sample());
		System.out.println("v4 sample()="+v4.sample());
		System.out.println("v4 sample()="+v4.sample());
		v4.scale(-1);
		System.out.println("norm v4 (1) ="+v4.norm(1));
		System.out.println("norm v4 (2) ="+v4.norm(2));
		System.out.println("norm v4 (infty) ="+v4.norm(Double.POSITIVE_INFINITY));
	}

	public double[] getData() {
		return data;
	}
	
	public int[] getIndex(){
		return index;
	}



}
