package cdba;

import java.util.Vector;

import Jama.Matrix;

public class CDBAalgorithm {
	//initialize reference scanpaths as one individual scanpath
	public static Vector<Cluster> initialClusters(Vector<Point> pts){
		Vector<Cluster> vc = new Vector<Cluster>();
		for(int i=0;i<pts.size();i++){
			Cluster c = new Cluster(pts.get(i).getX(),pts.get(i).getY());
			vc.add(c);
		}
		return vc;
	}
	
	
	//obtain the optimal match between components of reference scanpaths and individual scanpaths based on the accumulation matrix of DTW
	public static void assocMatrix(Matrix A, Vector<Vector<Point>> assoc, Scanpath sp){
		int row = A.getRowDimension();
		int col = A.getColumnDimension();
		int i = row-1;
		int j = col-1;
		assoc.get(i).add(sp.getPts().get(j));
		while(i!=0&&j!=0){
			double x1 = A.get(i-1,j-1);
			double x2 = A.get(i-1, j);
			double x3 = A.get(i, j-1);
			int index = 1;
			double min = x1;
			if(x3<min){
				min = x3;
				index = 3;
			}
			else if(x2<min){
				min = x2;
				index = 2;
			}
			switch(index){
			case 1:
				i--;
				j--;
				break;
			case 2:
				i--;
				break;
			case 3:
				j--;
				break;
			}
			assoc.get(i).add(sp.getPts().get(j));
		}
		if(i==0){
			while(j!=0){
				assoc.get(0).add(sp.getPts().get(j-1));
				j--;
			}
		}
		else{
			while(i!=0){
				assoc.get(i-1).add(sp.getPts().get(0));
				i--;
			}
		}
	}
	
	//compute DTW and update the reference scanpath
	public static Vector<Cluster> averageSeq(Vector<Scanpath> sps, Vector<Cluster> iniAve, Vector<Cluster> clusters,int[] upLim){
		int num = sps.size();
		int length = iniAve.size();
		Vector<Cluster> ave = new Vector<Cluster>();
		Matrix[] dtwDistMatrix = new Matrix[num];
		Vector<Vector<Point>> assoc = new Vector<Vector<Point>>();
		int[] occurCount = new int[upLim.length];
		for(int i=0;i<length;i++){
			Vector<Point> temp = new Vector<Point>();
			assoc.add(temp);
		}
		for(int i=0;i<num;i++){
			dtwDistMatrix[i] = AverageDTW.distanceDTW(iniAve,sps.get(i).getPts());
			assocMatrix(dtwDistMatrix[i],assoc,sps.get(i));
		}
		//update the first AOI of the reference scanpath
		Vector<Point> temp = assoc.get(0);
		double min = 99999;
		Cluster cand = new Cluster();
		for(int j=0;j<clusters.size();j++){
			double total = 0;
			int index = clusters.get(j).getCID();
			if(upLim[index] == occurCount[index])
				continue;
			for(int k=0;k<temp.size();k++){
				total += getDist(clusters.get(j), temp.get(k));
			}
			total = total/temp.size();
			if(total<min){
				min = total;
				cand = clusters.get(j);
			}
		}
		ave.add(cand);
		occurCount[cand.getCID()]++;
		//update the following AOIs of the reference scanpath
		for(int i=1;i<length;i++){
			temp = assoc.get(i);
			min = 99999;
			Vector<Cluster> cands = ave.lastElement().getCands();
			for(int j=0;j<cands.size();j++){
				double total = 0;
				int index = cands.get(j).getCID();
				if(upLim[index] == occurCount[index])
					continue;
				for(int k=0;k<temp.size();k++){
					total += getDist(cands.get(j), temp.get(k));
				}
				total = total/temp.size();
				if(total<min){
					min = total;
					cand = cands.get(j);
				}
			}
			int index = cand.getCID();
			if(index>=0){
				if(index!=ave.lastElement().getCID()){
					occurCount[index]++;
					ave.add(cand);
				}		
			}
		}
		return ave;
	}
	
	//calculate the euclidean distance between the AOI of the reference scanpath and the fixation of individual scanpaths
	public static double getDist(Cluster p,Point q){
		double xDiff = p.getX()-q.getX();
		double yDiff = p.getY()-q.getY();
		double dist = Math.sqrt(xDiff*xDiff+yDiff*yDiff);
		return dist;
	}
	
	//repeat the computation step and update step until the reference scanpath does not change or the iteration number exceeds a threshold
	public static Vector<Cluster> generalization(Vector<Scanpath> sps, Scanpath iniAve, Vector<Cluster> clusters, int[] upLim){
		Vector<Cluster> oldVC = new Vector<Cluster>();
		//oldVC.addAll(iniAve.getClusters());
		oldVC.addAll(initialClusters(iniAve.getPts()));
		Vector<Cluster> newVC = new Vector<Cluster>();
		
		newVC.addAll(averageSeq(sps,oldVC,clusters,upLim));	
		int iter_count = 0;
		while(!oldVC.equals(newVC)&&iter_count<100){
			oldVC.clear();
			oldVC.addAll(newVC);
			newVC.clear(); 
			newVC.addAll(averageSeq(sps,oldVC,clusters,upLim));
			iter_count++;
		}
		return newVC;
	}
}

