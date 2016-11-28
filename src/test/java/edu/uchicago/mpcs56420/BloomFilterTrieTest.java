package edu.uchicago.mpcs56420;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Anuved on 11/28/2016.
 */
public class BloomFilterTrieTest {

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
    public void testInsertSequence() throws IOException {
        BloomFilterTrie bft = new BloomFilterTrie(12, 4);

        File folder = new File("db/test-xsmall");
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                bft.insertSequence(file);
            }
        }

        assert bft.getRoot().getUncompressedContainer().size() == 0;
        assert bft.contains("AGGCTATGCTCA");
        assert bft.contains("AGGCTATG");
        assert bft.contains("AGGCTATGCT");
        assert bft.contains("AGGCTATGCTCATAG") == false;
    }

}
