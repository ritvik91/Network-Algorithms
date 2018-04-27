package Algorithms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import Util.Constants;
import Util.GraphPlot;

public class VirtualFM {

	private static final int PHYSICAL_MAP_SIZE = 32;
	private static final int FM_SKETCH_SIZE = 32;
	private static final double PHI = 0.77351;
	
	int[] bitMap;
	int[] vBitMap;
	Map<String,Map<Integer,Integer>> outputMap;
	
	public VirtualFM() {
		this.bitMap = new int[PHYSICAL_MAP_SIZE];
		this.vBitMap = new int[PHYSICAL_MAP_SIZE];
		this.outputMap = new HashMap<String,Map<Integer,Integer>>();

		Random r = new Random();
		for (int i = 0; i < vBitMap.length; i++) {
			vBitMap[i] = r.nextInt(PHYSICAL_MAP_SIZE);
		}
	}
	
	public void buildAndEstimate(Map<String,Map<String,Integer>> actual) throws IOException {
		
		actual.forEach((source,v)->{
			int actualCardinality = v.size();
			this.bitMap = new int[PHYSICAL_MAP_SIZE];
			v.forEach((dest,flow) -> {
				fillPhysicalBitMap(source, dest);	
			});
			double z = getZerosInBitMap() / PHYSICAL_MAP_SIZE;
			int estimated  = (int)(PHYSICAL_MAP_SIZE * (Math.pow(2 , z)) / PHI); 
			
			Map<Integer,Integer> destMap = new HashMap<Integer,Integer>();
			destMap.put(actualCardinality, (int)estimated);
			
			outputMap.put(source, destMap);
		});

		plotWriteOutput();
	}
	
	private double getZerosInBitMap(){
		double numberOfZeros = 0;
		for(int i = 0 ; i < bitMap.length ; i++){
			int fmSketch = bitMap[i];
			int setBits = 0;
			while(fmSketch > 0) {
				if((fmSketch & 1) == 1){break;}
				else setBits++;
				fmSketch >>= 1;
			}
			numberOfZeros += setBits;
		}
		
		return numberOfZeros;
	}
	
	private void fillPhysicalBitMap(String sourceIP, String destinationIP) {
		int virtualBitMapIndex = destinationIP.hashCode() % FM_SKETCH_SIZE;

		long physicalBitHashValue = (Long.parseLong(sourceIP.replace(
				".", "")) ^ vBitMap[virtualBitMapIndex]);
		int physicalBitMapIndex = (int)(Long.valueOf(physicalBitHashValue).hashCode() % PHYSICAL_MAP_SIZE);
		
		int fmSketch = bitMap[physicalBitMapIndex];
		bitMap[physicalBitMapIndex] = (fmSketch | (1 << getFMSketchBitToSet(destinationIP)));
	}
	
	/**
	 * function for geometric hashing to get the bit to be set in FM Sketch
	 * @param destinationIP
	 * @return
	 */
	private int getFMSketchBitToSet(String destinationIP){	
		int hashedValue = destinationIP.hashCode();
		int rightMostSetBit =  (int)((Math.log10(hashedValue & -hashedValue)) / Math.log10(2));
		return rightMostSetBit;		
	}

	/**
	 * function to plot the result graph and write output to file
	 * @throws IOException
	 */
private void plotWriteOutput() throws IOException {
		
		File file = new File(Constants.OUTPUT_V_FM);
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		if (!file.exists()) {
			file.createNewFile();
		}
		
		
		outputMap.forEach((source,v)->{
			v.forEach((actual,estimate)->{
				try {
					bw.write(source +"\t" + actual + "\t" + estimate);
					bw.newLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		});
		
		GraphPlot gp = new GraphPlot("Virtual FM", "Virtual FM", outputMap);
		gp.setVisible(true);
		
		bw.close();
		
	}
}
