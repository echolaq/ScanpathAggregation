package cdba;

import java.util.Vector;


public class Cluster {
	private int cID;         //cluster/AOI ID
	private double x;
	private double y;        //cluster/AOI center position
	Vector<Cluster> cands;    //the Candidate set of the cluster
	
	Cluster(int cID, double x, double y){
		this.x = x;
		this.y = y;
		this.cID = cID;
		cands = new Vector<Cluster>();
	}
	
	Cluster(double x, double y){
		this.x = x;
		this.y = y;
		cands = new Vector<Cluster>();
	}
	
	Cluster(){
		
	}
	
	public void addCand(Cluster c){
		cands.add(c);
	}
	
	public Vector<Cluster> getCands(){
		return cands;
	}
	
	public int getCID(){
		return cID;
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
}