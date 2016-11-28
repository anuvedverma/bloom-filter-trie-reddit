package edu.uchicago.mpcs56420;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.BitSet;

/**
 * Created by Anuved on 11/27/2016.
 */
public class BloomFilterTrie {

    private static final int DEFAULT_KMER_LENGTH = 63;
    private static final int DEFAULT_PREFIX_LENGTH = 9;
    private static final int KMER_PREFIX_RATIO = 7;

    private BFTVertex mRoot;
    private int mKmerLength;
    private ArrayList<String> mColors;

    /* Create BFT with default settings */
    public BloomFilterTrie() {
        mRoot = new BFTVertex();
        mKmerLength = DEFAULT_KMER_LENGTH;
        Container.setPrefixLength(DEFAULT_PREFIX_LENGTH);
        mColors = new ArrayList<>();
    }

    /* Create BFT with custom settings */
    public BloomFilterTrie(int kmerLength, int prefixLength) {
        mRoot = new BFTVertex();
        mKmerLength = kmerLength;
        mColors = new ArrayList<>();

        // adjust & optimize k-mer and prefix lengths to make them compatible
        if (prefixLength < mKmerLength / 2) {
            while (kmerLength % prefixLength != 0)
                prefixLength++;
        } else {
            while (kmerLength % prefixLength != 0)
                prefixLength--;
        }

        Container.setPrefixLength(prefixLength);
    }

    /* Public interface for inserting FASTA sequence into BFT */
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
            Tuple firstKmer = new Tuple(kMer.toString(), color);
            mRoot.insert(firstKmer);
            System.out.println(firstKmer);
//            mRoot.insert(new Tuple(kMer.toString(), color));


            // read subsequent k-mers
            String character;
            int c;
            while((c = inputReader.read()) != -1) {
                character = ((char) c + "").toLowerCase(); // cast to string and lowercase

                if(Container.getAlphabet().contains(character)) {
                    kMer.deleteCharAt(0);
                    kMer.append(character);

                    // insert k-mer
                    Tuple nextKmer = new Tuple(kMer.toString(), color);
                    mRoot.insert(nextKmer);
                    System.out.println(nextKmer);
//                mRoot.insert(new Tuple(kMer.toString(), color));

                }
            }

            inputReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Public interface methods */
    public boolean contains(String sequence) {
        return mRoot.containsSequence(new Tuple(sequence.toLowerCase()));
    }

    public boolean genomeContains(String color, String sequence) {
        Tuple query = new Tuple(sequence, color);
        return mRoot.contains(query);
    }

    public String[] genomesContaining(String query) {
        return new String[1];
    }

    private BitSet genomesContaining(Tuple tuple) {
        return null;
    }


    /* Getters */

    public BFTVertex getRoot() {
        return mRoot;
    }

    public int getKmerLength() {
        return mKmerLength;
    }
}