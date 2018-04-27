package Algorithms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import Util.Constants;
import Util.EmptyCountSetException;
import Util.FileHandler;
import Util.GraphPlot;

public class countMin {

	String outputPath;
	int depth;
	int width;
	int[][] countArray;
	int[] hashD;
	Map<String,Map<Integer,Integer>> outputMap;
	Map<Integer,Integer> graph = new HashMap<Integer,Integer>();
	
	
	public countMin(String outputPath, int depth, int width) throws IOException {
		this.outputPath = outputPath;
		this.depth = depth;
		this.width = width;
		countArray = new int[depth][width];
		this.hashD = new int[depth];
		Random rand = new Random(System.currentTimeMillis());
		this.outputMap = new HashMap<String,Map<Integer,Integer>>();
		
		for(int i=0;i<depth;i++) {
			hashD[i] = rand.nextInt(Integer.MAX_VALUE);
		}
		
	}
	
	private void set(String SourceDestinationPair, int count) {
		for(int i=0;i<depth;i++) {
			int c = ((int)(hashD[i]*SourceDestinationPair.hashCode())) % width;
			c = Math.abs(c);
			countArray[i][c] += count;
		}
	}
	
	private int getCount(String SourceDestinationPair) {
		
		int result = Integer.MAX_VALUE;
		
		for(int i=0;i<depth;i++) {
			int c = ((int)(hashD[i]*SourceDestinationPair.hashCode())) % width;
			c = Math.abs(c);
			result = Math.min(result, countArray[i][c]);
		}
		return result;
	}
	
	public void BuildCountMin(Map<String,Map<String,Integer>> actual) throws IOException, EmptyCountSetException {
		
		//actual-estimated map for source|Destination pair
		Map<Integer,Integer> destMap;
		
		actual.forEach((k,v)->{
			v.forEach((dest,flow) -> {
//				String SourceDestinationPair = k + Constants.SEPERATOR + dest;
				set(k + Constants.SEPERATOR + dest,flow);
			});
		});

		File file = new File(outputPath);
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		
		if (!file.exists()) {
			file.createNewFile();
		}
		
		
		for(Entry<String, Map<String,Integer>> e : actual.entrySet()) {
			for(Entry<String,Integer> dest : e.getValue().entrySet()) {
				
//				String SourceDestinationPair = e.getKey() + Constants.SEPERATOR + dest.getKey();
				int estimate = getCount(e.getKey() + Constants.SEPERATOR + dest.getKey());
				
				graph.put(dest.getValue(),estimate);
				bw.write(e.getKey() + Constants.SEPERATOR + dest.getKey() +"\t" + dest.getValue() + "\t" + estimate);
				bw.newLine();
			}
		}
		bw.close();
		plotWriteOutput();
		
	}
	
	private void plotWriteOutput() throws EmptyCountSetException, IOException {
		if(graph==null || graph.size()==0)
			throw new EmptyCountSetException("count array is not set");
		
		GraphPlot gp = new GraphPlot("Count Min", graph);
		gp.setVisible(true);
		
		File file = new File(outputPath);
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		
		if (!file.exists()) {
			file.createNewFile();
		}
		
		
		for(Entry<String,Map<Integer,Integer>> e : outputMap.entrySet()) {
			for(Entry<Integer,Integer> Flow : e.getValue().entrySet()) {
				bw.write(e.getKey() +"\t" + Flow.getKey() + "\t" + Flow.getValue());
				bw.newLine();
			}
		}
		
		bw.close();
		
	}
	
}
