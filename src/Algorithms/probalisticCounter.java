package Algorithms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import Util.Constants;
import Util.GraphPlot;

public class probalisticCounter {

	final int SIZE = 2000;
	BitSet bitMap ;
	Map<String,Map<Integer,Integer>> outputMap;
	
	public probalisticCounter() {
		bitMap = new BitSet(SIZE);
		outputMap = new HashMap<String,Map<Integer,Integer>>();
	}
	
	public BitSet setBit(BitSet bitMap, String SourceDestinationPair) {
		int b = (int)SourceDestinationPair.hashCode()%SIZE;
		b = Math.abs(b);
		bitMap.set(b);
		return bitMap;
	}
	
	public void pcEstimator(Map<String,Map<String,Integer>> actual) throws IOException {
				
				actual.forEach((source,v)->{
					bitMap = new BitSet(SIZE);
					v.forEach((dest,flow) -> {
						String SourceDestinationPair = source + Constants.SEPERATOR + dest;
						bitMap = setBit(bitMap,SourceDestinationPair);
					});
					int zeros = SIZE - bitMap.cardinality();
					double V = (double)zeros/SIZE;
					double estimator = -1*SIZE*(Math.log(V));
					
					Map<Integer,Integer> destMap = new HashMap<Integer,Integer>();
					destMap.put(v.size(), (int)estimator);
					
					outputMap.put(source, destMap);
				});
				
				plotWriteOutput();
	}
	
	private void plotWriteOutput() throws IOException {
		
		File file = new File(Constants.OUTPUT_Prob_Counting);
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
		
		GraphPlot gp = new GraphPlot("Probalistic Counting", "Probalistic Counting", outputMap);
		gp.setVisible(true);
		
		bw.close();
		
	}
}
