package edu.uchicago.mpcs56420;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Anuved on 11/28/2016.
 */
public class Benchmark {

    private ArrayList<String> mColors;
    private HashMap<String, Integer> mGenomeIndex;
    private int mKmerLength;

    public Benchmark(int kMerLength) {
        mColors = new ArrayList<>();
        mGenomeIndex = new HashMap<>();
        mKmerLength = kMerLength;
    }

    public void insertSequence(File inputFile) throws IOException {
        if(!inputFile.getName().endsWith(".fasta"))
            throw new IOException("Invalid input file format: must be a fasta file");

        // set up k-mer tuples
        StringBuilder kMer = new StringBuilder();

        // prepare buffered reader to read input file
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8));

        try {
            String color = inputReader.readLine(); // color is header
            mColors.add(color);

            // read first k-mer
            for (int i = 0; i < mKmerLength; i++) {
                int c = inputReader.read();

                // check for EOF
                if(c == -1) {
                    System.out.println("File too short for k-mer length! Exiting...");
                    System.exit(-1);
                    break;
                }
                // add character to k-mer
                String character = ((char) c + "").toLowerCase();
                if(Container.getAlphabet().contains(character))
                    kMer.append(character);
            }

            // insert first k-mer
            mGenomeIndex.put(kMer.toString(), Tuple.colorHash(color));
            System.out.println(kMer);


            // read subsequent k-mers
            String character;
            int c;
            while((c = inputReader.read()) != -1) {
                character = ((char) c + "").toLowerCase(); // cast to string and lowercase

                if(Container.getAlphabet().contains(character)) {
                    kMer.deleteCharAt(0);
                    kMer.append(character);

                    // insert k-mer
                    mGenomeIndex.put(kMer.toString(), Tuple.colorHash(color));
                    System.out.println(kMer);
                }
            }

            inputReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
