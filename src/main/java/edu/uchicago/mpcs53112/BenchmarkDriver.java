package edu.uchicago.mpcs53112;

import edu.uchicago.mpcs53112.Benchmark.BenchmarkArray;
import edu.uchicago.mpcs53112.Benchmark.BenchmarkHashMap;
import edu.uchicago.mpcs53112.BloomFilterTrie.BloomFilterTrie;
import org.github.jamm.MemoryMeter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Created by Anuved on 11/29/2016.
 */
public class BenchmarkDriver {
    public static void main(String[] args) {

        /* Hard-coded input and query directories for quick testing */
//        File dbFolder = new File("db/test-xsmall");
//        File dbFolder = new File("db/test-small");
//        File dbFolder = new File("db/test-med");
//        File dbFolder = new File("db/test-large");
//        File dbFolder = new File("db/test-xlarge");
        File dbFolder = new File(args[0]);
//        File queryFolder = new File("query/test-1");
        File queryFolder = new File(args[1]);


        /* Initialize BFT and benchmark data structures */
        BloomFilterTrie bloomFilterTrie = new BloomFilterTrie(5, 3);
        BenchmarkHashMap benchmarkHashMap = new BenchmarkHashMap(5);
        BenchmarkArray benchmarkArray = new BenchmarkArray(5);


        /* Initialize timers */
        long beforeTime;
        long afterTime;

        long msBFT = 0;
        long msHM = 0;
        long msAL = 0;

        /* Compare time to populate data structures */
        try {
            msBFT = 0;
            msHM = 0;
            msAL = 0;

            System.out.println("Populating data structures....");

            // bft
            beforeTime = System.currentTimeMillis();
            populateBFT(dbFolder, bloomFilterTrie);
            afterTime = System.currentTimeMillis();
            msBFT = afterTime - beforeTime;


            // hash-map
            beforeTime = System.currentTimeMillis();
            populateHashMap(dbFolder, benchmarkHashMap);
            afterTime = System.currentTimeMillis();
            msHM = afterTime - beforeTime;


            // array-list
            beforeTime = System.currentTimeMillis();
            populateArray(dbFolder, benchmarkArray);
            afterTime = System.currentTimeMillis();
            msAL = afterTime - beforeTime;


            System.out.println("Time to populate data structures:");
            System.out.println("BFT: " + msBFT + " milliseconds");
            System.out.println("HashMap: " + msHM + " milliseconds");
            System.out.println("ArrayList: " + msAL + " milliseconds");
            System.out.println();

        } catch (IOException e) {
            e.printStackTrace();
        }

        /* Compare memory usage of each data structure */
        MemoryMeter meter = new MemoryMeter();
        System.out.println("Size estimate of objects:");
        System.out.println("BFT: " + meter.measureDeep(bloomFilterTrie));
        System.out.println("HashMap: " + meter.measureDeep(benchmarkHashMap));
        System.out.println("ArrayList: " + meter.measureDeep(benchmarkArray));


        System.out.println();


        /* Compare average time to find query sequences */
        try {
            ArrayList<String> queries = readQueries(queryFolder);
            long numQueries = queries.size();
            msBFT = 0;
            msHM = 0;
            msAL = 0;

            for(String query : queries) {

                // bft
                beforeTime = System.currentTimeMillis();
                bloomFilterTrie.containsQuery(query);
                afterTime = System.currentTimeMillis();
                msBFT += (afterTime - beforeTime);

                // hash-map
                beforeTime = System.currentTimeMillis();
                benchmarkHashMap.containsKmer(query);
                afterTime = System.currentTimeMillis();
                msHM += (afterTime - beforeTime);


                // array-list
                beforeTime = System.currentTimeMillis();
                benchmarkArray.containsKmer(query);
                afterTime = System.currentTimeMillis();
                msAL += (afterTime - beforeTime);

            }

            System.out.println("Total time to run input queries:");
            System.out.println("BFT: " + (msBFT) + " milliseconds");
            System.out.println("HashMap: " + (msHM) + " milliseconds");
            System.out.println("ArrayList: " + (msAL) + " milliseconds");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static ArrayList<String> readQueries(File folder) throws IOException {
        ArrayList<String> queries = new ArrayList<>();

        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));

                String line;
                StringBuilder query = new StringBuilder();
                while ((line = inputReader.readLine()) != null)
                    query.append(line);

                queries.add(query.toString());

            }
        }

        return queries;
    }



    /* Methods to populates data structures */
    private static void populateBFT(File folder, BloomFilterTrie bft) throws IOException {
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                bft.insertComments(file);
            }
        }
    }

    private static void populateHashMap(File folder, BenchmarkHashMap bhm) throws IOException {
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                bhm.insertSequence(file);
            }
        }
    }

    private static void populateArray(File folder, BenchmarkArray ba) throws IOException {
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                ba.insertSequence(file);
            }
        }
    }


}
