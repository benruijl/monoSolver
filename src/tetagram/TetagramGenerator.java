package tetagram;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
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

		OutputStream fstream = new FileOutputStream("tetagrams.dat");
		DataOutputStream out = new DataOutputStream(fstream);
		out.writeInt(tetagrams.size());

		/* Print the map */
		for (Map.Entry<String, Integer> entry : tetagrams.entrySet()) {
			System.out.println(entry.getKey() + " " + (float) entry.getValue()
					/ (file.length() - 3));
			out.writeChars(entry.getKey());
			out.writeDouble(Math.log(entry.getValue()
					/ (double) (file.length() - 3)));
		}

		out.close();

		System.exit(0);
	}

}
