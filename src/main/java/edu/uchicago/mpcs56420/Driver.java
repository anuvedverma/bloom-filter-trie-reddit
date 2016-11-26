package edu.uchicago.mpcs56420;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by Anuved on 11/25/2016.
 */
public class Driver {
	public static void main(String[] args) {


		ArrayList<String> test = new ArrayList<>();
		test.add(0, "one");
		test.add(1, "three");
//		test.add(0, "two");
		for (int i = 0; i < test.size(); i++) {
			System.out.print(test.get(i) + " ");
		}

		/* Hard-coded input directory for quick testing */
		File folder = new File("input");
		File[] listOfFiles = folder.listFiles();

		/* Iterate through all files in input directory and add to BFT */
		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
//            System.out.println(file.getName());
			if (file.isFile() && file.getName().endsWith(".txt")) {
				try {
					String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
//                    System.out.println(content);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
