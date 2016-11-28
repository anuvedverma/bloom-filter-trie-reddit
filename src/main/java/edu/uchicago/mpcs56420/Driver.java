package edu.uchicago.mpcs56420;

import org.apache.commons.lang.StringUtils;

import static edu.uchicago.mpcs56420.Container.getPrfxPrefixLength;

/**
 * Created by Anuved on 11/25/2016.
 */
public class Driver {
	public static void main(String[] args) {

		String sfpx = "agc";
		String sfpxPrefix = sfpx.substring(0, getPrfxPrefixLength());
		System.out.println(sfpxPrefix);
		String sfpxSuffix = sfpx.substring(getPrfxPrefixLength());
		System.out.println(sfpxSuffix);

		char c = '?';
		sfpx = StringUtils.rightPad(sfpx, 4, c);
		System.out.println(sfpx);

//		BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8), 100, .1);
//		ArrayList<String> items = new ArrayList<>();
//		for (int i = 0; i < 5; i++) {
//			items.add("item" + (i+1));
//			bloomFilter.put(items.get(i));
//		}
//
//		int numFalsePositives = -5;
//		int numIterations = 1000000;
//		for (int i = 1; i <= numIterations; i++) {
//			if(bloomFilter.mightContain("item" + i)) {
//				numFalsePositives++;
//				System.out.println("Contains: item" + i);
//			}
//		}
//		System.out.println("False Positive Rate: " + ((double) numFalsePositives / (double) numIterations));


		/* Hard-coded input directory for quick testing */
//		File folder = new File("input");
//		File[] listOfFiles = folder.listFiles();
//
//		/* Iterate through all files in input directory and add to BFT */
//		for (int i = 0; i < listOfFiles.length; i++) {
//			File file = listOfFiles[i];
////            System.out.println(file.getName());
//			if (file.isFile() && file.getName().endsWith(".txt")) {
//				try {
//					String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
////                    System.out.println(content);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}

	}
}
