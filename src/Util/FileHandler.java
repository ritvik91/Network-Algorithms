package Util;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
/**
 * The class handles all IO operations for file handling
 * @author RITVIK
 *
 */
public class FileHandler {

	String INPUT ;
	String OUTPUT;
	
	public FileHandler(String inputPath, String outputPath) {
		INPUT = inputPath;
		OUTPUT = outputPath;
	}
	
	/**
	 * Method to read from file
	 * @return map of source destination and flow size
	 * @throws IOException
	 */
	public Map<String,Map<String,Integer>> read() throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader(INPUT));
		Map<String,Integer> destMap;
		Map<String,Map<String,Integer>> inputMap = new HashMap<String,Map<String,Integer>>();
		
		String readLine = br.readLine();
		readLine = br.readLine();
		while (readLine!=null) {
			String[] input = readLine.split("\\s+");

			if(inputMap.containsKey(input[0].trim())) {
				destMap = inputMap.get(input[0].trim());
				destMap.put(input[1].trim(), Integer.valueOf(input[2].trim()));
			}else {
				destMap = new HashMap<String,Integer>();
				destMap.put(input[1].trim(), Integer.valueOf(input[2].trim()));
				inputMap.put(input[0].trim(),destMap);
			}
			System.out.println(input[0] + " size- " + inputMap.get(input[0]).size());
			readLine = br.readLine();
		}
		br.close();
		return inputMap;
	}
	
	public void write() {
		
	}
	
	/*public void readWriteFile(String filePath, String outputFilePath) throws Exception{

		FileInputStream inputStream = null;
		Scanner sc = null;
		String value;
		int count;
		ArrayList<String> output;

		try {
			inputStream = new FileInputStream(filePath);
			sc = new Scanner(inputStream, "UTF-8");
			File file = new File(outputFilePath);
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			if (!file.exists()) {
				file.createNewFile();
			}
			while (sc.hasNextLine()) {
				String readLine = sc.nextLine();
				String[] input = readLine.split(" ");
				if(input.length==1 && input[0].equalsIgnoreCase("stop"))
					break;
				else if(input.length==1){
					count = Integer.parseInt(input[0]);
					output = manager.removeMax(count);
					String delim="";
					for(String o : output){
						bw.write(delim + o.substring(1));
						delim=", ";
					}
					bw.write("\r\n");
				}else if(input.length == 2){
					value = input[0];
					count = Integer.parseInt(input[1]);
					manager.insert(value, count);
				}else{
					throw new Exception("error in input");
				}
			}
			if (sc.ioException() != null) {
				throw sc.ioException();
			}
			fw.close();
			bw.close();
			
		} catch(Exception e){
			throw new Exception(e.getMessage());
		}finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (sc != null) {
				sc.close();
			}
		}

	}*/

}
