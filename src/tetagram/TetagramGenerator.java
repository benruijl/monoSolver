package tetagram;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TetagramGenerator {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		/* Reads the file. */
		String file = new Scanner(new File("kjv10.txt")).useDelimiter("\\Z")
				.next();
		
		file = file.replaceAll("[^A-Za-z]", "");
		
		Map<String, Integer> tetagrams = new HashMap<String, Integer>();
		
		for (int i = 0; i < file.length() - 4; i++) {
			String tet = file.substring(i, i + 4);
			
			if (tetagrams.containsKey(tet)) {
				Integer count = tetagrams.get(tet);
				tetagrams.put(tet, count + 1);
			} else {
				tetagrams.put(tet, 1);
			}
		}
		
		FileWriter fstream = new FileWriter("tetagrams.txt");
        BufferedWriter out = new BufferedWriter(fstream);
		
		/* Print the map */
		for (Map.Entry<String, Integer> entry : tetagrams.entrySet()) {
			System.out.println(entry.getKey() + " " + (float)entry.getValue() / (file.length() -3));
			out.write(entry.getKey() + " " + (float)entry.getValue() / (file.length() - 3) + "\r\n");
		}
		
		out.close();
		
		System.exit(0);
	}
	
}
