package edu.uchicago.mpcs56420.Benchmark;

import edu.uchicago.mpcs56420.BloomFilterTrie.Container;
import edu.uchicago.mpcs56420.BloomFilterTrie.Tuple;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.BitSet;

/**
 * Created by Anuved on 11/29/2016.
 */
public class BenchmarkArray {

    private ArrayList<String> mGenomeKmer;
    private int mKmerLength;

    public BenchmarkArray(int kmerLength) {
        mGenomeKmer = new ArrayList<>();
        mKmerLength = kmerLength;
    }

    /* Public interface for inserting FASTA sequence into Benchmark */
    public void insertSequence(File inputFile) throws IOException {
        if(!inputFile.getName().endsWith(".fasta"))
            throw new IOException("Invalid input file format: must be a fasta file");

        // set up k-mer tuples
        StringBuilder kMer = new StringBuilder();

        // prepare buffered reader to read input file
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8));

        try {
            String color = inputReader.readLine(); // color is header

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
            mGenomeKmer.add(kMer.toString());


            // read subsequent k-mers
            String character;
            int c;
            while((c = inputReader.read()) != -1) {
                character = ((char) c + "").toLowerCase(); // cast to string and lowercase

                if(Container.getAlphabet().contains(character)) {
                    kMer.deleteCharAt(0);
                    kMer.append(character);

                    // insert k-mer
                    mGenomeKmer.add(kMer.toString());
                }
            }

            inputReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Public interface for checking whether a given K-mer is stored or not */
    public boolean containsKmer(String sequence) {
        sequence = sequence.toLowerCase();
        for (int i = 0; i < mGenomeKmer.size(); i++) {
            if(mGenomeKmer.get(i).equals(sequence))
                return true;
        }

        return false;
    }

    public void printArray() {
        for (int i = 0; i < mGenomeKmer.size(); i++) {
            System.out.println(mGenomeKmer.get(i));
        }
    }
}
