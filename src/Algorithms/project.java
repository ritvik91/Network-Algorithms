package Algorithms;

import java.io.IOException;
import java.util.Map;

import Util.Constants;
import Util.EmptyCountSetException;
import Util.FileHandler;

public class project {

	public static void main(String[] args) {
		FileHandler fh = new FileHandler(Constants.INPUT_PATH, Constants.OUTPUT_Count_Min);
		DoubleHashing dh = new DoubleHashing("traffic.txt","outputTwoLevelHashing.txt");
		
		try {
			Map<String,Map<String,Integer>> actual = fh.read();
			
			dh.hashTable(actual);

			probalisticCounter pc = new probalisticCounter();
			pc.pcEstimator(actual);
			
			BloomFilter bf = new BloomFilter();
			bf.fillFilter(actual);
			
			countMin cm = new countMin("outputCountMin.txt", 3, Constants.VIRTUAL_SIZE);
			cm.BuildCountMin(actual);
			
			VirtualBitmap vb = new VirtualBitmap();
			vb.fillBitMap(actual);
			
			VirtualFM vfm = new VirtualFM();
			vfm.buildAndEstimate(actual);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
