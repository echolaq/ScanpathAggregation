package cdba;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import Jama.Matrix;

public class Initialization {
	public static void initializeSP(String cluster_center_file, String fix_label_file, String fix_data_file,  Vector<Scanpath> sps, Vector<Cluster> cls){
		// Initialize all clusters/AOIs (clustering results)
		File file = new File(cluster_center_file);
		if(!file.exists()){
			System.out.println("Data File Not Exists");
			System.exit(2);
		}
		try{
			int cID = 0;
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			while((line = br.readLine())!=null&&line.trim()!=""){
				String[] params = line.split(" ");
				double x=Double.parseDouble(params[0]);
				double y=Double.parseDouble(params[1]);
				Cluster cl = new Cluster(cID,x,y);
				cls.add(cl);
				cID++;
			}
			br.close();
			fr.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		//get the cluster/AOI label for each fixation point (clustering results)
		Vector<Integer> labels_vec = new Vector<Integer>();
		File labelFile = new File(fix_label_file);
		if(!labelFile.exists()){
			System.out.println("Data File Not Exists");
			System.exit(2);
		}
		try{
			FileReader fr = new FileReader(labelFile);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			line = br.readLine();
			String[] ls = line.split(" ");
			for(int i=0;i<ls.length;i++){
				int la = Integer.parseInt(ls[i]);
				labels_vec.add(la);
			}		
			br.close();
			fr.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		//Initialize scanpath based on the fixation data provided by eye tracking data set and the clustering results
		file = new File(fix_data_file);
		int tID = 0;
		if(!file.exists()){
			System.out.println("Data File Not Exists");
			System.exit(2);
		}
		try{
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			int ind = 0;
			while((line = br.readLine())!=null&&line.trim()!=""){
				if(line=="")
					break;
				String[] params = line.split(" ");
				Vector<Point> pts = new Vector<Point>();
				for(int i=0;i+2<params.length;i=i+3){
					double x=Double.parseDouble(params[i]);
					double y=Double.parseDouble(params[i+1]);
					double time=Double.parseDouble(params[i+2]);
					int cID = labels_vec.get(ind);
					Point pt = new Point(tID,x,y,time,cID);
					ind = ind + 1;
					pts.add(pt);
				}
				if(pts.size()>0){
					Scanpath sp = new Scanpath(pts,tID);
					sps.add(sp);
					tID++;
				}
			}
			br.close();
			fr.close();
			for(int i=0;i<sps.size();i++){
				sps.get(i).clusterSequence(cls);
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	
	//compute the maximum occurrence count of each AOI in all the scanpahts
	public static int[] upLim(Vector<Scanpath> sps,Vector<Cluster> clusters){
		int[] upLim = new int[clusters.size()];
		for(int i=0;i<clusters.size();i++){
			upLim[i] = 0;
		}
		for(int i=0;i<sps.size();i++){
			Vector<Cluster> cs = sps.get(i).getClusters();
			int[] temp = new int[clusters.size()];
			for(int j=0;j<cs.size();j++){
				temp[cs.get(j).getCID()]+=1;
			}
			for(int j=0;j<clusters.size();j++){
				if(temp[j] > upLim[j])
					upLim[j] = temp[j];
			}
		}
		return upLim;
	}
		
	//find the potential next AOI for each AOI, i.e., construct the initial candidate set for each AOI and return AOIs that are located first in individual AOIs)
	public static Vector<Cluster> initializeNextAOI(Vector<Scanpath> sps,Vector<Cluster> cls){
		Matrix A = new Matrix(cls.size()+1,cls.size(),0);
		for(int i=0;i<sps.size();i++){
			Scanpath sp = sps.get(i);
			int cID = sp.getClusters().get(0).getCID();
			A.set(cls.size(), cID, 1);
			for(int j=1;j<sp.getTrSize();j++){
				int start = sp.getClusters().get(j-1).getCID();
				int end = sp.getClusters().get(j).getCID();
				A.set(start, end, 1);
			}
		}
		for(int i=0;i<cls.size();i++){
			cls.get(i).addCand(cls.get(i));
			for(int j=0;j<cls.size();j++){
				if(A.get(i, j)>0){
					cls.get(i).addCand(cls.get(j));
				}
			}
		}
		Vector<Cluster> firstAOI = new Vector<Cluster>();
		for(int i=0;i<cls.size();i++){
			if(A.get(cls.size(), i)>0){
				firstAOI.add(cls.get(i));
			}
		}
		return firstAOI;
	}
}
