package edu.uchicago.mpcs53112.BloomFilterTrie;

import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Anuved on 11/27/2016.
 */
public class BloomFilterTrie {

    private static final int DEFAULT_KMER_LENGTH = 5;
    private static final int DEFAULT_PREFIX_LENGTH = 3;
    private static final int KMER_PREFIX_RATIO = 7;

    private BFTVertex mRoot;
    private int mKmerLength;
    private HashMap<String, String> mColors;

    private HashSet<String> mStopwords = new HashSet<>();

    /* Create BFT with default settings */
    public BloomFilterTrie() {
        mRoot = new BFTVertex();
        mKmerLength = DEFAULT_KMER_LENGTH;
        Container.setPrefixLength(DEFAULT_PREFIX_LENGTH);
        mColors = new HashMap<>();
        mStopwords = getStopwords();
    }

    /* Create BFT with custom settings */
    public BloomFilterTrie(int kmerLength, int prefixLength) {
        mRoot = new BFTVertex();
        mKmerLength = kmerLength;
        mColors = new HashMap<>();
        mStopwords = getStopwords();

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

    /* Public interface for inserting string into BFT */
    public void insertComments(File inputFile) throws IOException {

        if(!inputFile.getName().endsWith(".json"))
            throw new IOException("Invalid input file format: must be a JSON file");

        // prepare buffered reader to read input file
//        BufferedReader inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8));

        String name = "name";
        String body = "body";
        try (BufferedReader inputReader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = inputReader.readLine()) != null) {

                // set up k-mer tuples
                String kMer = "";

                // process the line.
                JSONObject json = new JSONObject(line);
                String color = json.getString(name);
                String originalComment = json.getString(body);

//                System.out.println(color + ": " + comment);
                String comment = preprocess(originalComment);
//                System.out.println("Preprocessed: " + comment);

                if(comment.length() < mKmerLength)
                    continue;

                mColors.put(color, originalComment);
                // insert k-mers into root
                for (int i = 0; i < comment.length() - mKmerLength+1; i++) {
                    kMer = comment.substring(i, i+mKmerLength);
                    mRoot.insert(new Tuple(kMer, color));
                }
            }
        }
    }

    /* Public interface for checking whether a given K-mer is stored or not */
    public boolean containsQuery(String query) {
        query = preprocess(query);

        boolean found = false;

        // iterate k-mers
        String kMer;
        for (int i = 0; i < query.length() - mKmerLength+1; i++) {
            kMer = query.substring(i, i+mKmerLength);
            Tuple kMerTuple = new Tuple(kMer);
            found = found || mRoot.containsSequence(kMerTuple);
        }


        return found;
    }

    /* Public interface to search for comments based on input query */
    public ArrayList<String> search(String query) {
        query = preprocess(query);

        HashSet<String> commentsContaining = new HashSet<>();

        BitSet colorsContainingKmer = new BitSet();

        // iterate k-mers
        String kMer;
        for (int i = 0; i < query.length() - mKmerLength+1; i++) {
            kMer = query.substring(i, i+mKmerLength);
            colorsContainingKmer.or(mRoot.colorsContaining(kMer));
        }


        for (String color : mColors.keySet()) {
            int bitIndex = Tuple.colorHash(color);
            if(colorsContainingKmer.get(bitIndex))
                commentsContaining.add(mColors.get(color));
        }

        return new ArrayList<>(commentsContaining);
    }


    /* Getters */

    public BFTVertex getRoot() {
        return mRoot;
    }

    public int getKmerLength() {
        return mKmerLength;
    }

    public HashMap<String, String> getComments() { return mColors; }

    /* Auxiliary functions */
    private String preprocess(String input) {
        StringBuilder output = new StringBuilder();

        input = input.replaceAll("\\p{P}", "").toLowerCase();
        String[] inputArr = input.split(" ");
        for (int i = 0; i < inputArr.length; i++) {
            if(!mStopwords.contains(inputArr[i]))
                output.append(inputArr[i] + " ");
        }

        return output.toString().trim();
    }

    private HashSet<String> getStopwords() {

        HashSet<String> stopWords = new HashSet<>();

        stopWords.add("a");
        stopWords.add("about");
        stopWords.add("after");
        stopWords.add("again");
        stopWords.add("against");
        stopWords.add("all");
        stopWords.add("am");
        stopWords.add("an");
        stopWords.add("and");
        stopWords.add("any");
        stopWords.add("are");
        stopWords.add("arent");
        stopWords.add("as");
        stopWords.add("at");
        stopWords.add("be");
        stopWords.add("because");
        stopWords.add("been");
        stopWords.add("before");
        stopWords.add("being");
        stopWords.add("between");
        stopWords.add("both");
        stopWords.add("but");
        stopWords.add("by");
        stopWords.add("cant");
        stopWords.add("cannot");
        stopWords.add("could");
        stopWords.add("couldnt");
        stopWords.add("did");
        stopWords.add("didnt");
        stopWords.add("do");
        stopWords.add("does");
        stopWords.add("doing");
        stopWords.add("dont");
        stopWords.add("down");
        stopWords.add("during");
        stopWords.add("each");
        stopWords.add("few");
        stopWords.add("for");
        stopWords.add("from");
        stopWords.add("further");
        stopWords.add("had");
        stopWords.add("hadnt");
        stopWords.add("has");
        stopWords.add("hasnt");
        stopWords.add("have");
        stopWords.add("havent");
        stopWords.add("he");
        stopWords.add("hed");
        stopWords.add("hell");
        stopWords.add("her");
        stopWords.add("here");
        stopWords.add("heres");
        stopWords.add("hers");
        stopWords.add("herself");
        stopWords.add("him");
        stopWords.add("his");
        stopWords.add("how");
        stopWords.add("hows");
        stopWords.add("i");
        stopWords.add("id");
        stopWords.add("ill");
        stopWords.add("im");
        stopWords.add("if");
        stopWords.add("in");
        stopWords.add("into");
        stopWords.add("is");
        stopWords.add("isnt");
        stopWords.add("it");
        stopWords.add("its");
        stopWords.add("itself");
        stopWords.add("lets");
        stopWords.add("me");
        stopWords.add("most");
        stopWords.add("not");
        stopWords.add("of");
        stopWords.add("only");
        stopWords.add("or");
        stopWords.add("so");
        stopWords.add("to");
        stopWords.add("too");
        stopWords.add("than");
        stopWords.add("that");
        stopWords.add("the");
        stopWords.add("their");
        stopWords.add("there");
        stopWords.add("us");
        stopWords.add("until");
        stopWords.add("we");
        stopWords.add("were");
        stopWords.add("with");
        stopWords.add("which");
        stopWords.add("while");
        stopWords.add("you");

        return stopWords;
    }
}