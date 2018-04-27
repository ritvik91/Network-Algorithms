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

public class VirtualBitmap {

	final static int SIZE=100000000;
	final static int V_SIZE=200;
	final static int RAND_SIZE=1000000;
	int ones = 0;
	
	BitSet bitMap;
	int[] random;
	Map<String,int[]> Vs ;
	Map<String,Map<Integer,Integer>> outputMap;
	
	public VirtualBitmap() {
		bitMap = new BitSet(SIZE);
		random = new int[V_SIZE];
		Vs = new HashMap<String,int[]>();
		outputMap = new HashMap<String,Map<Integer,Integer>>();
		randomFill();
	}
	
	public void randomFill()
	{
		Random rand = new Random(System.currentTimeMillis());
		for(int i=0;i<V_SIZE;i++) {
			random[i] = ((int)rand.nextInt(RAND_SIZE));
		}
	}
	
	public BitSet setBit(BitSet bitMap, String Source, String Dest) {
		
		int rI = Dest.hashCode()%V_SIZE;
		rI = Math.abs(rI);
		Integer hm = (int) (random[rI] ^ getLongFromStringIP(Source));
		int i = hm.hashCode()%SIZE;
		i = Math.abs(i);
		bitMap.set(i);
		return bitMap;
	}
	
	public boolean getVsZero(String Source, String Dest) {
		int rI = Dest.hashCode()%V_SIZE;
		rI = Math.abs(rI);
		Integer hm = (int) (random[rI] ^ getLongFromStringIP(Source));
		int i = hm.hashCode()%SIZE;
		i = Math.abs(i);
		return bitMap.get(i);
	}
	
	public void fillBitMap(Map<String,Map<String,Integer>> actual) throws IOException {
		
		actual.forEach((source,v)->{
			bitMap = new BitSet(SIZE);
			v.forEach((dest,flow) -> {
				bitMap = setBit(bitMap,source,dest);
			});
		});
		
		actual.forEach((source,v)->{
			ones = 0;
			v.forEach((dest,flow) -> {
				if(getVsZero(source,dest))
					ones++;
			});
			int Szeros = V_SIZE - ones;
			int Bzeros = SIZE - bitMap.cardinality();
			double Vm = (double)Bzeros/SIZE;
			double Vs = (double)Szeros/V_SIZE;
			double estimator = V_SIZE*(Math.log(Vm)) - (V_SIZE*Math.log(Vs));
			
			Map<Integer,Integer> destMap = new HashMap<Integer,Integer>();
			destMap.put(v.size(), (int)estimator);
			
			outputMap.put(source, destMap);
		});
		
		plotWriteOutput();
	}
	
private void plotWriteOutput() throws IOException {
		
		File file = new File(Constants.OUTPUT_V_BITMAP);
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
		
		GraphPlot gp = new GraphPlot("Virtual Bitmap", "Virtual Bitmap", outputMap);
		gp.setVisible(true);
		
		bw.close();
		
	}

	public Long getLongFromStringIP(String IP) {
		String[] ip = IP.split("\\.");
		long result = 0;
		for (int i = 3; i >= 0; i--) {
			long res = Long.parseLong(ip[3 - i]);
			result |= res << (i * 8);
		}
		return result;
	}
}
