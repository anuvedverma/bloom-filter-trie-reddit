package edu.uchicago.mpcs53112.Benchmark;

import edu.uchicago.mpcs53112.BloomFilterTrie.Container;
import edu.uchicago.mpcs53112.BloomFilterTrie.Tuple;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Anuved on 11/28/2016.
 */
public class BenchmarkHashMap {

    private static final int BIT_ARRAY_SIZE = 100;

    private ArrayList<String> mColors;
    private HashMap<String, BitSet> mGenomeIndex;
    private int mKmerLength;

    public BenchmarkHashMap(int kMerLength) {
        mColors = new ArrayList<>();
        mGenomeIndex = new HashMap<>();
        mKmerLength = kMerLength;
    }

    /* Public interface for inserting FASTA sequence into Benchmark */
    public void insertSequence(File inputFile) throws IOException {
        if(!inputFile.getName().endsWith(".json"))
            throw new IOException("Invalid input file format: must be a JSON file");

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
            mGenomeIndex.put(kMer.toString(), new BitSet(BIT_ARRAY_SIZE));


            // read subsequent k-mers
            String character;
            int c;
            while((c = inputReader.read()) != -1) {
                character = ((char) c + "").toLowerCase(); // cast to string and lowercase

                if(Container.getAlphabet().contains(character)) {
                    kMer.deleteCharAt(0);
                    kMer.append(character);

                    // insert k-mer
                    if(mGenomeIndex.containsKey(kMer.toString()))
                        mGenomeIndex.get(kMer.toString()).set(Tuple.colorHash(color));
                    else
                        mGenomeIndex.put(kMer.toString(), new BitSet(BIT_ARRAY_SIZE));
                }
            }

            inputReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Public interface for checking whether a given K-mer is stored or not */
    public boolean containsKmer(String sequence) {
        if(mGenomeIndex.containsKey(sequence.toLowerCase()))
            return true;
        return false;
    }

    /* Public interface for checking whether a given genome contains a given K-mer */
    public boolean containsKmer(String color, String sequence) {
        if(mGenomeIndex.containsKey(sequence)) {
            BitSet bs = mGenomeIndex.get(sequence.toLowerCase());
            int pos = Tuple.colorHash(color);
            if(bs.get(pos))
                return true;

        }

        return false;
    }

    /* Public interface for checking which genomes contain a given K-mer */
    public ArrayList<String> genomesContainingKmer(String kMerQuery) {
        HashSet<String> genomesContaining = new HashSet<>();

        if(mGenomeIndex.containsKey(kMerQuery.toLowerCase())) {
            BitSet colorsContainingKmer = mGenomeIndex.get(kMerQuery);

            for (int i = 0; i < mColors.size(); i++) {
                int bitIndex = Tuple.colorHash(mColors.get(i));
                if(colorsContainingKmer.get(bitIndex))
                    genomesContaining.add(mColors.get(i));
            }
        }

        return new ArrayList<>(genomesContaining);

    }

    public void printHashMap() {
        for(String kMer : mGenomeIndex.keySet()) {
            System.out.println(kMer);
        }
    }
}
