package edu.uchicago.mpcs53112;

import edu.uchicago.mpcs53112.Benchmark.BenchmarkHashMap;
import edu.uchicago.mpcs53112.BloomFilterTrie.BloomFilterTrie;
import edu.uchicago.mpcs53112.BloomFilterTrie.Container;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Anuved on 11/28/2016.
 */
public class BloomFilterTrieTest {

    private final int CONTAINER_CAPACITY = 5;
    private static int PREFIX_LENGTH = 3;

    @Before
    public void init() {
        Container.setAlphabet(" abcdefghijklmnopqrstuvwxyz");
        Container.setCapacity(CONTAINER_CAPACITY);
        Container.setPrefixLength(PREFIX_LENGTH);
    }


    @Test
    public void testCustomConstructor() {
        int prevPrefixLength = Container.getPrefixLength();

        BloomFilterTrie bft1 = new BloomFilterTrie(18, 4);
        assert Container.getPrefixLength() == 6;

        BloomFilterTrie bft2 = new BloomFilterTrie(20, 6);
        assert Container.getPrefixLength() == 10;

        Container.setPrefixLength(prevPrefixLength);
        assert Container.getPrefixLength() == 4;
    }

    @Test
    public void testInsert() throws IOException {
        BloomFilterTrie bft = new BloomFilterTrie(5, 3);

        File folder = new File("db/test-xsmall");
        populateBFT(folder, bft);

        assert bft.getRoot().getUncompressedContainer().size() == 0;
        assert bft.containsQuery("family members");
        assert bft.containsQuery("straight razor clipper towel");
        assert bft.containsQuery("Joseph Smith");
        assert bft.containsQuery("weiner length apart");
        assert bft.containsQuery("shiva blade");
        assert bft.containsQuery("animal planet") == false;
        assert bft.containsQuery("most of us") == false;
    }

    @Test
    public void testContains() throws IOException {
        BloomFilterTrie bft = new BloomFilterTrie(12, 4);

        File folder = new File("db/test-xsmall");
        populateBFT(folder, bft);

        assert bft.getRoot().getUncompressedContainer().size() == 0;

        HashMap<String, String> bftGenomes = bft.getComments();

        ArrayList<String> genomeColors = bft.search("AGGCTATGCTCA");
        assert bftGenomes.get(0).equals(genomeColors.get(0));

        genomeColors = bft.search("GCGCTATGCTGA");
        assert bftGenomes.get(4).equals(genomeColors.get(0));
    }


    @Test
    public void testOnDB() throws IOException {

        BloomFilterTrie bft = new BloomFilterTrie(5, 3);
        File folder = new File("db/test-xsmall");
        populateBFT(folder, bft);

        assert bft.containsQuery("family members");
        assert bft.containsQuery("straight razor clipper towel");
        assert bft.containsQuery("Joseph Smith");
        assert bft.containsQuery("weiner length apart");
        assert bft.containsQuery("shiva blade");
    }

    @Test
    public void testBenchmark() throws IOException {
        int kMerLength = 63;
        BenchmarkHashMap benchmarkHashMap = new BenchmarkHashMap(kMerLength);
        File file = new File("db/test-small/input1.fasta");
        benchmarkHashMap.insertSequence(file);
    }


    private void populateBFT(File folder, BloomFilterTrie bft) throws IOException {
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                bft.insertComments(file);
            }
        }
    }

}
