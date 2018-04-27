package Algorithms;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import Util.Constants;

public class BloomFilter {

	final int SIZE = 15000000;
	final int Number_Of_Hash = 3;
	final int Random_IPS = 10000;
	int[] hashD;
	int falsePositive;
	int falseNegative;
	int ratio;
	BitSet bitMap ;
	Map<String,Map<Integer,Integer>> outputMap;
	int numberOfElementsinFilter = 0;
	
	public BloomFilter() {
		bitMap = new BitSet(SIZE);
		outputMap = new HashMap<String,Map<Integer,Integer>>();
		
		Random rand = new Random(System.currentTimeMillis());
		this.outputMap = new HashMap<String,Map<Integer,Integer>>();
		this.hashD = new int[Number_Of_Hash];
		for(int i=0;i<Number_Of_Hash;i++) {
			hashD[i] = rand.nextInt(Integer.MAX_VALUE);
		}
	}
	
	public BitSet setBit(BitSet bitMap, String SourceDestinationPair) {
		for(int i=0;i<Number_Of_Hash;i++) {
			int c = ((int)(hashD[i]*SourceDestinationPair.hashCode())) % SIZE;
			c = Math.abs(c);
			bitMap.set(c);
		}
		return bitMap;
	}
	
	public boolean isPresent(String IP) {

		for(int i=0;i<Number_Of_Hash;i++) {
			int c = ((int)(hashD[i]*IP.hashCode())) % SIZE;
			c = Math.abs(c);
			if(!bitMap.get(c))
				return false;
		}
		return true;
	}
	
	public String randomIP() {
		Random rand = new Random(System.currentTimeMillis());
		StringBuilder ip = new StringBuilder();
		for(int i=0;i<4;i++) {
			ip.append(String.valueOf(rand.nextInt(256)));
			ip.append(".");
		}
		return ip.toString().substring(0,ip.length()-1);
	}
	
	public void fillFilter(Map<String,Map<String,Integer>> actual) {
		
		actual.forEach((k,v)->{
			v.forEach((dest,flow) -> {
				String SourceDestinationPair = k + Constants.SEPERATOR + dest;
				bitMap = setBit(bitMap,SourceDestinationPair);
				numberOfElementsinFilter++;
			});
		});
		
		for(int i=0;i<Random_IPS;i++) {
			String source = randomIP();
			String dest = randomIP();
			String SourceDestinationPair = source + Constants.SEPERATOR + dest;
			
			boolean isPresentInFilter = isPresent(SourceDestinationPair);
			if(isPresentInFilter && !(actual.containsKey(source) && actual.get(source).containsKey(dest)))
				falsePositive++;
			else if(!isPresentInFilter && (actual.containsKey(source) && actual.get(source).containsKey(dest)))
				falseNegative++;
		}
		
		System.out.println("false Positive - " +falsePositive);
		System.out.println("false neg -" + falseNegative);
		System.out.println(" ratio - " + (double)falsePositive/Random_IPS);
		System.out.println("Size - "+SIZE+" number of Hashes - "+Number_Of_Hash+ " elements in Filter -" + numberOfElementsinFilter);
	}
}
