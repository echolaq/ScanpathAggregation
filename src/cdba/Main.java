package cdba;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;


public class Main {
	public static void main(String[] args) {
		//the path of data file
		String cluster_center_file = "E:\\Workspace\\ScanpathAggregation\\data\\cluster_center.txt";
		String fix_label_file = "E:\\Workspace\\ScanpathAggregation\\data\\fix_label.txt";
		String fix_data_file ="E:\\Workspace\\ScanpathAggregation\\data\\fix_data.txt";
		String output_file = "E:\\Workspace\\ScanpathAggregation\\data\\output.txt";
		
		//initialize the clustering results (AOIs) and processed scanpaths
		Vector<Cluster> cls = new Vector<Cluster>();
		Vector<Scanpath> sps = new Vector<Scanpath>();
		Initialization.initializeSP(cluster_center_file, fix_label_file, fix_data_file,sps,cls);
		
		//obtain the maximum ocurrence count of each AOI in individual scanpaths
		int[] upLim = Initialization.upLim(sps,cls);
		//obtain AOIs that are located first in individual scanpaths (potential first AOIs in the reference scanpath)
		Vector<Cluster> firstAOI = Initialization.initializeNextAOI(sps,cls);
		//initialize reference scanpath as each individual scanpath and choose the final scanpath that can best match our definition
		Vector<Vector<Cluster>> patterns = new Vector<Vector<Cluster>>();
		double dist = 99999;
		int flag = -1;
		for(int i=0;i<sps.size();i++){
			Vector<Cluster> sp = CDBAalgorithm.generalization(sps, sps.get(i),firstAOI,upLim);
			double temp = AverageDTW.averageDistance(sps, sp);
			if(temp < dist){
				dist = temp;
				flag = i;
			}
			patterns.add(sp);
		}
		
		//calculate the average DTA between the final representative scanpath and each individual scanpath
		double aveDTW = AverageDTW.averageDistance(sps, patterns.get(flag));
		System.out.println(aveDTW);
		//output the representative scanpaths
		writePats(patterns.get(flag),output_file);
		
	}
	
	public static void writePats(Vector<Cluster> cls,String filename){
		File pat_file = new File(filename);
		
		try{
			if (!pat_file.exists()) {
				pat_file.createNewFile();
			}
			FileWriter fw = new FileWriter(pat_file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			String file_content ="";
			for(int i=0;i<cls.size();i++){
				Cluster cl_temp = cls.get(i);
				file_content = file_content + cl_temp.getCID()+" "+ cl_temp.getX() + " " + cl_temp.getY() + " ";
			}
			bw.write(file_content);
			bw.close();
			fw.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

}
