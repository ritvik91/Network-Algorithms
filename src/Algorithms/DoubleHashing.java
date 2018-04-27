package Algorithms;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import Util.FileHandler;
import Util.GraphPlot;

public class DoubleHashing {

	Map<String, Map<String,Integer>> hashTable;
	String inputPath;
	String outputPath;
	
	public DoubleHashing() {
		hashTable = new HashMap<String,Map<String,Integer>>();
	}
	
	public DoubleHashing(String inputPath, String outputPath) {
		hashTable = new HashMap<String,Map<String,Integer>>();
		this.inputPath = inputPath;
		this.outputPath = outputPath;
	}
	
	public void hashTable(Map<String,Map<String,Integer>> hashTable) throws IOException {
		
		System.out.println("size - "+ hashTable.get("0.195.194.219").size());
		
		Map<String,Map<Integer,Integer>> result = new HashMap<>();
		hashTable.forEach((k,v) -> {
			Map<Integer,Integer> temp = new HashMap<Integer,Integer>();
			temp.put(v.size(), v.size());
			result.put(k, temp);});
		
		GraphPlot gp = new GraphPlot("Hashing", "Two Level Hashing", result);
		gp.setVisible(true);
		
		File file = new File(outputPath);
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		
		if (!file.exists()) {
			file.createNewFile();
		}
		
		for(Entry<String,Map<String,Integer>> e : hashTable.entrySet()) {
			bw.write(e.getKey() +"\t" + e.getValue().size());
			bw.newLine();
		}

		bw.close();
	}
}
