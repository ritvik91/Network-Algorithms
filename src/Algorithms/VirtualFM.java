package Algorithms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import Util.Constants;
import Util.GraphPlot;

public class VirtualFM {

	private static final int PHYSICAL_MAP_SIZE = 32;
	private static final int FM_SKETCH_SIZE = 32;
	private static final double PHI = 0.77351;
	
	int[] physicalBitMap;
	int[] vBitMap;
	Map<String,Map<Integer,Integer>> outputMap;
	
	public VirtualFM() {
		this.physicalBitMap = new int[PHYSICAL_MAP_SIZE];
		this.vBitMap = new int[PHYSICAL_MAP_SIZE];
		this.outputMap = new HashMap<String,Map<Integer,Integer>>();
		initialize_virtual_bit_map();
	}
	
	private void initialize_virtual_bit_map() {
		Random r = new Random();
		for (int i = 0; i < vBitMap.length; i++) {
			vBitMap[i] = r.nextInt(PHYSICAL_MAP_SIZE);
		}
	}
	
	public void buildAndEstimate(Map<String,Map<String,Integer>> actual) throws IOException {
		
		actual.forEach((source,v)->{
			int actualCardinality = v.size();
			this.physicalBitMap = new int[PHYSICAL_MAP_SIZE];
			v.forEach((dest,flow) -> {
				fillPhysicalBitMap(source, dest);	
			});
			double zAvg = getZerosInPhysicalBitMap() / PHYSICAL_MAP_SIZE;
			int estimated  = (int)(PHYSICAL_MAP_SIZE * (Math.pow(2 , zAvg)) / PHI); 
			if(estimated > 2.5){
				estimated = (int)(PHYSICAL_MAP_SIZE * (Math.pow(2 , zAvg) - Math.pow(2, -1.75 * zAvg)) / PHI);
			}
			
			Map<Integer,Integer> destMap = new HashMap<Integer,Integer>();
			destMap.put(actualCardinality, (int)estimated);
			
			outputMap.put(source, destMap);
		});

		plotWriteOutput();
	}
	
	private double getZerosInPhysicalBitMap(){
		double numberOfZeros = 0;
		for(int i = 0 ; i < physicalBitMap.length ; i++){
			int fmSketch = physicalBitMap[i];
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
		int virtualBitMapIndex = (destinationIP.hashCode() & 0x7fffffff) % FM_SKETCH_SIZE;

		long physicalBitHashValue = (Long.parseLong(sourceIP.replace(
				".", "")) ^ vBitMap[virtualBitMapIndex]);
		int physicalBitMapIndex = (String.valueOf(physicalBitHashValue)
				.hashCode() & 0x7fffffff) % PHYSICAL_MAP_SIZE;
		
		int fmSketch = physicalBitMap[physicalBitMapIndex];
		physicalBitMap[physicalBitMapIndex] = (fmSketch | (1 << calculateGeometricHashing(destinationIP)));
	}
	
	private int calculateGeometricHashing(String destinationIP){	
		int hashedValue = (destinationIP.hashCode() & 0x7fffffff);
		int rightMostSetBit =  (int)((Math.log10(hashedValue & -hashedValue)) / Math.log10(2));
		return rightMostSetBit;		
	}

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
