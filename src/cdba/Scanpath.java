package cdba;

import java.util.Vector;


public class Scanpath {
	private Vector<Point> ptSequence;      //fixation-level scanpath
	private Vector<Cluster> cSequence;     //cluster/AOI-level scanpath

	Scanpath(Vector<Point> pts,int tID){
		ptSequence = pts;
		cSequence = new Vector<Cluster>();
	}
	
	Scanpath(Vector<Cluster> cls){
		cSequence = cls;
	}
	
	public Vector<Cluster> getClusters(){
		return cSequence;
	}
	
	public int getTrSize(){
		return cSequence.size();
	}
	
	
	//transform a fixation-level scanpath into a cluster/AOI-level trajectory
	public void clusterSequence(Vector<Cluster> clusters){
		int cNum = 1;
		int cID = ptSequence.get(0).getCID();
		cSequence.add(clusters.get(cID));
		for(int i=1;i<ptSequence.size();i++){	
			cID = ptSequence.get(i).getCID();
			if(cID!=cSequence.get(cNum-1).getCID()){
				cSequence.add(clusters.get(cID));
				cNum++;
			}		
		}
	}
	
	public Vector<Point> getPts(){
		return ptSequence;
	}
}

