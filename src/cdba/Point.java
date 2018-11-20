package cdba;

public class Point {
	private int tID;                //the id of the scanpath which the fixation point belongs to
	private int cID;                //the id of the cluster/AOI which the fixation point belongs to
	private double time;            //fixation duration
	private double x;
	private double y;               //fixation position
	
	Point(int tID, double x,double y,double time,int cID){
		this.tID = tID;
		this.x = x;
		this.y = y;
		this.time = time;
		this.cID = cID;
	}
	
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public int getTID(){
		return tID;
	}
	
	public int getCID(){
		return cID;
	}
	
	public double getTime(){
		return time;
	}
}
