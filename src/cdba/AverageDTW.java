package cdba;

import java.util.Vector;
import Jama.Matrix;

public class AverageDTW {
	
	//calculate DTW between reference scanpaths (composed of AOIs) and one individual scanpath (composed of fixations)
	public static Matrix distanceDTW(Vector<Cluster> sp1,Vector<Point> sp2){
		int row = sp1.size();
		int col = sp2.size();
		Matrix A = new Matrix(row,col);
		//calculate the euclidean distance matrix (cost matrix)
		for(int i=0;i<row;i++)
			for(int j=0;j<col;j++)
			{
				double xDiff = sp1.get(i).getX()-sp2.get(j).getX();
				double yDiff = sp1.get(i).getY()-sp2.get(j).getY();
				A.set(i, j, Math.sqrt(xDiff*xDiff+yDiff*yDiff));
			}
		//calculate the accumulation matrix
		for(int i=1;i<row;i++){
			double x1 = A.get(i-1,0);
			double x2 = A.get(i,0);
			A.set(i,0,x1+x2);
		}
		
		for(int j=1;j<col;j++){
			double x1 = A.get(0,j-1);
			double x2 = A.get(0,j);
			A.set(0,j,x1+x2);
		}
		
		for(int i=1;i<row;i++)
			for(int j=1;j<col;j++){
				double x1 = A.get(i-1,j-1) + A.get(i,j);
				double x2 = A.get(i-1, j) + A.get(i,j);
				double x3 = A.get(i, j-1) + A.get(i, j);
				A.set(i,j,Math.min(Math.min(x1, x2), x3));
			}
//		A.print(5, 0);
		return A;
	}
	
	//calculate the average DTW between the reference scanpaths (of AOIs) and all the individual scanpaths (of fixations)
	public static double averageDistance(Vector<Scanpath> sps, Vector<Cluster> sp){
		double totalDist = 0;
		for(int j=0;j<sps.size();j++){
			Matrix result = distanceDTW(sp, sps.get(j).getPts());
			int row = result.getRowDimension();
			int col = result.getColumnDimension();
			//double aveDTW = result.get(row-1, col-1)/Math.max(tr.size(), trs.get(j).getPts().size());
			double temp = result.get(row-1, col-1);
			totalDist += result.get(row-1, col-1);
		}
		return totalDist/sps.size();
	}
}

