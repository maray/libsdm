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

/** Sparse Matrix Implementation. Time-efficient implementation of sparse matrices. Compatible 
 * with SparseVector and DenseVector. This data structure contains the matrix duplicated, one in
 * sparse vector-row form and in sparse vector-column form. This increase in storage space dramatically 
 * improves the speed of transposed multiplications.
 * 
 * @author Mauricio Araya
 */

public class SparseMatrix implements Serializable {

	protected int cols;
	protected int rows;
	
	protected SparseVector dataCol[];
	protected SparseVector dataRow[];

	private static final long serialVersionUID = -1837335507372896623L;
	
	
	/** Creates an empty matrix.
	 * @param rows 
	 * @param cols 
	 */
	public SparseMatrix(int rows, int cols) {
		this.cols=cols;
		this.rows=rows;
		dataCol=new SparseVector[cols];
		for (int c=0;c<cols;c++){
			dataCol[c]=new SparseVector(rows);
		}
		dataRow=new SparseVector[rows];
		for (int r=0;r<rows;r++){
			dataRow[r]=new SparseVector(cols);
		}
	}

	/* Functions to create only the matrices, not for general use!, use mult! */
	
	/** Assign a column to the matrix. This function is SLOW, useful for creating data structures,
	 * yet not for be included algorithms.
	 * @param c column number
	 * @param rv the sparse vector to put in the column
	 */
	public void assignColumn(int c, SparseVector rv) {
		dataCol[c]=rv.copy();
		for (int r=0;r<rows;r++){
			dataRow[r].assign(c, rv.get(r));
		}
	}
	
	/** Get a row of the matrix. DO NOT MODIFY.
	 * @param r the row number
	 */
	public SparseVector getRow(int r) {
		return dataRow[r];
	}

	/** Get a column of the matrix. DO NOT MODIFY.
	 * @param c the column number
	 */
	
	public SparseVector getColumn(int c) {
		return dataCol[c];
	}

	/** Get an element of the matrix.
	 * @param r the row number
	 * @param c the row number
	 */
	public double get(int r, int c) {
		return dataCol[c].get(r);
	}

	/** Modify an element of the matrix. This function is SLOW, useful for creating data structures,
	 * yet not for be included algorithms.
	 * @param r the row number
	 * @param c column number
	 * @param val value to assign
	 */
	public void assign(int r, int c, double val) {
		dataCol[c].assign(r,val);
		dataRow[r].assign(c,val);
	}
	
	/** Matrix Multiplication. Right multiplication of a matrix.
	 * @param matrix the other matrix to multiply
	 * @return the resulting matrix.
	 */
	public SparseMatrix mult(SparseMatrix matrix) {
		if (cols!=matrix.rows)
			Utils.error("Wrong Dimensions in Matrix by Matrix Mult");
		SparseMatrix retval=new SparseMatrix(rows,matrix.cols);
		for (int c=0;c<matrix.cols;c++){
			//System.out.println(matrix.dataCol[c].norm(1));
			for (int r=0;r<rows;r++){
				//System.out.println(dataRow[r] +" VS "+matrix.dataCol[c]);
				retval.assign(r, c, dataRow[r].dot(matrix.dataCol[c]));
			}
		}
		return retval;
	}

	/** Transposed Matrix Multiplication. Right multiplication of a transposed matrix.
	 * @param matrix the other matrix to multiply
	 * @return the resulting matrix.
	 */
	public SparseMatrix transmult(SparseMatrix matrix) {
		if (cols!=matrix.cols)
			Utils.error("Wrong Dimensions in Matrix by Matrix Mult");
		SparseMatrix retval=new SparseMatrix(rows,matrix.rows);
		for (int c=0;c<matrix.rows;c++){
			//System.out.println(matrix.dataRow[c].norm(1));
			for (int r=0;r<rows;r++){
				//System.out.println(dataRow[r] +" VS "+matrix.dataCol[c]);
				retval.assign(r, c, dataRow[r].dot(matrix.dataRow[c]));
			}
		}
		return retval;
	}
	
	/* end expensive functions */

	
	/** Multiplication of a SparseVector. Right multiplication of the vector vec, and
	 * scales the result by gamma. (gamma*M*vec).
	 * @param gamma a scaling factor (scalar)
	 * @param vec the vector to multiply
	 * 
	 * @return a new SparseVector with the result 
	 * */
	public SparseVector mult(double gamma,SparseVector vec) {
		double res;
		int idx=0;
		int[] vindex=new int[rows];
		double[] vdata=new double[rows];
		for (int r=0;r<rows;r++){
			res=gamma*dataRow[r].dot(vec);
			if (res!=0){
				vindex[idx]=r;
				vdata[idx]=res;
				idx++;
			}
		}
		return new SparseVector(vindex,vdata,idx);
	}
	
	/** Transposed Multiplication of a SparseVector. Right multiplication of the vector vec 
	 * with this matrix transposed, and scales the result by gamma. (gamma*M'*vec).
	 * @param gamma a scaling factor (scalar)
	 * @param vec the vector to multiply
	 * 
	 * @return a new SparseVector with the result 
	 * */
	public SparseVector transmult(double gamma, SparseVector vec) {
		double res;
		int idx=0;
		//System.out.println(vec.size());
		int[] vindex=new int[cols];
		double[] vdata=new double[cols];
		for (int c=0;c<cols;c++){
			res=gamma*dataCol[c].dot(vec);
			if (res!=0){
				vindex[idx]=c;
				vdata[idx]=res;
				idx++;
			}
		}
		return new SparseVector(vindex,vdata,idx);
	}

	/** Multiplication of a DenseVector. Right multiplication of the vector vec, and
	 * scales the result by gamma. (gamma*M*vec).
	 * @param gamma a scaling factor (scalar)
	 * @param vec the vector to multiply
	 * 
	 * @return a new DenseVector with the result 
	 * */
	public DenseVector mult(int gamma, DenseVector vec) {
		DenseVector res = new DenseVector(rows);
		for (int r=0;r<rows;r++)
			res.set(r,gamma*dataRow[r].dot(vec));
		return res;
	}

	/** Transposed Multiplication of a DenseVector. Right multiplication of the vector vec 
	 * with this matrix transposed, and scales the result by gamma. (gamma*M'*vec).
	 * @param gamma a scaling factor (scalar)
	 * @param vec the vector to multiply
	 * 
	 * @return a new DenseVector with the result 
	 * */

	public DenseVector transmult(double gamma, DenseVector vec) {
		DenseVector res = new DenseVector(cols);
		for (int c=0;c<cols;c++)
			res.set(c,gamma*dataCol[c].dot(vec));
		return res;
	}
	
	/** Get a uniform (column) matrix of size r*c.
	 * TODO: This works only for square matrices right now.
	 * @param r rows
	 * @param c columns
	 * @return a new uniform SparseMatrix
	 */
	public static SparseMatrix getUniform(int r, int c) {
		SparseMatrix uni = new SparseMatrix(r, c);
		for (int i = 0; i < r; i++)
			uni.dataRow[i]=SparseVector.getUniform(r);
		for (int j = 0; j < c; j++)
			uni.dataCol[j]=SparseVector.getUniform(r);
		return (uni);
	}

	/** Get a square identity matrix of size n*n.
	 * 
	 * @param n the rows=columns size
	 * @return a new identity SparseMatrix
	 */
	public static SparseMatrix getIdentity(int n) {
		SparseMatrix id = new SparseMatrix(n, n);
		for (int i = 0; i < n; i++){
			id.dataRow[i].assign(i, 1.0);
			id.dataCol[i].assign(i, 1.0);
		}
		return (id);
	}
	
	/** Creates a diagonal matrix.
	 * 
	 * @param n the rows=columns size
	 * @return a new identity SparseMatrix
	 */
	public static SparseMatrix getDiagonal(SparseVector v) {
		SparseMatrix di = new SparseMatrix(v.size, v.size);
		for (int i = 0; i < v.size; i++){
			di.dataRow[i].assign(i, v.get(i));
			di.dataCol[i].assign(i, v.get(i));
		}
		return (di);
	}


}
