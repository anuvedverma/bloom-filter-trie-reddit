package edu.uchicago.mpcs56420;

import com.google.common.hash.BloomFilter;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Anuved on 11/27/2016.
 */
public class BFTVertexTest {

    private BFTVertex mVertex;

    @Before
    public void initBFTVertex() {
        mVertex = new BFTVertex();

        Tuple tuple1 = new Tuple("aggctatgctca", "red");
        Tuple tuple2 = new Tuple("aggctgcattgt", "yellow");
        Tuple tuple3 = new Tuple("ctcatttgataa", "yellow");
        Tuple tuple4 = new Tuple("gccctgcattgt", "blue");
        Tuple tuple5 = new Tuple("gcgctatgctga", "blue");

        mVertex.insert(tuple1);
        mVertex.insert(tuple2);
        mVertex.insert(tuple3);
        mVertex.insert(tuple4);
        mVertex.insert(tuple5);

        // test init conditions
        ArrayList<CompressedContainer> compressedContainers = mVertex.getCompressedContainers();
        UncompressedContainer uncompressedContainer = mVertex.getUncompressedContainer();

        assert compressedContainers.size() == 0;
        assert uncompressedContainer.size() == 5;
        assert uncompressedContainer.numColors() == 5;

    }

    @Test
    public void testInsertWithoutBursting() {

        UncompressedContainer uncompressedContainer = mVertex.getUncompressedContainer();

        // test redundant insertion
        mVertex.insert(new Tuple("gcgctatgctga", "blue"));
        mVertex.insert(new Tuple("gcgctatgctga"));
        assert uncompressedContainer.size() == 5;
        assert uncompressedContainer.numColors() == 5;

        // test redundant suffix, novel color insertion
        mVertex.insert(new Tuple("aggctatgctca", "green"));
        assert uncompressedContainer.size() == 5;
        assert uncompressedContainer.numColors() == 6;

    }

    @Test
    public void testInsertWithBursting() {

        ArrayList<CompressedContainer> compressedContainers;
        UncompressedContainer uncompressedContainer;

        // test new insertion that causes burst
        Tuple burstTuple = new Tuple("gcgccaggaatc", "red");
        mVertex.insert(burstTuple); // BURST
        compressedContainers = mVertex.getCompressedContainers();
        uncompressedContainer = mVertex.getUncompressedContainer();

        for (int i = 0; i < compressedContainers.size(); i++)
            System.out.println(compressedContainers.get(i));
        assert compressedContainers.size() == 1;
        assert uncompressedContainer.size() == 0;
        assert uncompressedContainer.numColors() == 0;

        // test new insertion after burst causing CC size to increase
        Tuple increaseCCSizeTuple = new Tuple("aaaacaggaatc", "blue");
        mVertex.insert(increaseCCSizeTuple); // extends cc size
        assert compressedContainers.size() == 1;
        assert uncompressedContainer.size() == 1;
        assert uncompressedContainer.numColors() == 1;


        // generate a false positive prefix
        CompressedContainer bfInsertTest = mVertex.getCompressedContainers().get(0);
        String falsePositive = "";
        boolean falsePositiveFound = false;
        while(!falsePositiveFound) {
            falsePositive = randomPrefix(4);
            if(bfInsertTest.mayContain(falsePositive) && !bfInsertTest.containsPrefix(falsePositive))
                falsePositiveFound = true;}

        // test new insertion after burst with false positive prefix
        Tuple fpTuple = new Tuple(falsePositive + "gacaatgt", "red");

        // verify that it is falsePositive is actually a false positive
        assert bfInsertTest.getQuer().mightContain(falsePositive);
        assert mVertex.contains(fpTuple) == false;

        // store state before insert
        BloomFilter bfBeforeInsert = bfInsertTest.getQuer();

        // insert
        mVertex.insert(fpTuple);
        assert mVertex.getUncompressedContainer().contains(fpTuple) == false; // verify uncompressed container size doesn't change
        assert bfBeforeInsert.equals(bfInsertTest.getQuer()); // verify that BF doesn't change after insertion
        assert mVertex.contains(fpTuple); // verify that insert was done successfully


        // test insertion for color updating (1)
        Tuple newColorTuple = new Tuple("gcgccaggaatc", "blue");
        assert mVertex.containsSuffix(newColorTuple);
        assert mVertex.contains(newColorTuple) == false;
        mVertex.insert(newColorTuple); // (1) CC contains tuple: update color
        assert mVertex.contains(newColorTuple);


        // test insertion if prefix is present (2)
        Tuple existingPrefixTuple = new Tuple("gcgcaaggaatc", "red");
        boolean prefixFound = false;
        for (int i = 0; i < compressedContainers.size(); i++) {
            String sfpx = existingPrefixTuple.getPrefix(Container.getSfpxLength());
            if (compressedContainers.get(i).containsPrefix(sfpx)) {
                prefixFound = true;
                break;
            }
        }
        assert prefixFound;
        assert mVertex.contains(existingPrefixTuple) == false;
        mVertex.insert(existingPrefixTuple);
        assert mVertex.getUncompressedContainer().contains(existingPrefixTuple) == false; // verify that it doesn't go into uncompressed container
        assert mVertex.contains(existingPrefixTuple);

        printCompressedContainers(compressedContainers);
    }

    @Test
    public void testInsertBurstEndOfKmer() {

        BFTVertex vertex = new BFTVertex();

        Tuple tuple1 = new Tuple("aggcatga", "red");
        Tuple tuple2 = new Tuple("gcgc", "blue");
        Tuple tuple3 = new Tuple("gccc", "blue");
        Tuple tuple4 = new Tuple("ctca", "yellow");
        Tuple tuple5 = new Tuple("aggataga", "yellow");

        vertex.insert(tuple1);
        vertex.insert(tuple2);
        vertex.insert(tuple3);
        vertex.insert(tuple4);
        vertex.insert(tuple5);

        // test init conditions
        ArrayList<CompressedContainer> compressedContainers = vertex.getCompressedContainers();
        UncompressedContainer uncompressedContainer = vertex.getUncompressedContainer();

        assert compressedContainers.size() == 0;
        assert uncompressedContainer.size() == 5;
        assert uncompressedContainer.numColors() == 5;

        Tuple tuple6 = new Tuple("tagcatag", "blue");
        vertex.insert(tuple6); // BURST

        compressedContainers = vertex.getCompressedContainers();
        uncompressedContainer = vertex.getUncompressedContainer();

        assert compressedContainers.size() == 1;
        assert uncompressedContainer.size() == 1;
        assert uncompressedContainer.numColors() == 1;

        // setup for level 2 burst
        Tuple tuple11 = new Tuple("gcgcagta", "red");
        Tuple tuple22 = new Tuple("gcgctgaa", "blue");
        Tuple tuple33 = new Tuple("gcgctggc", "blue");
        Tuple tuple44 = new Tuple("gcgcgcat", "yellow");
        Tuple tuple55 = new Tuple("gcgc", "violet");
        Tuple tuple66 = new Tuple("gcgcaata", "blue");

        BFTVertex gcgcChild = compressedContainers.get(0).getChildOf("gcgc");
        vertex.insert(tuple11);
        vertex.insert(tuple22);
        vertex.insert(tuple33);
        vertex.insert(tuple44);
        vertex.insert(tuple55);
        assert gcgcChild.getUncompressedContainer().size() == 4;


        // GCGCCHILD VERTEX PRE-BURST
        assert vertex.getCompressedContainers().size() == 1;
        vertex.insert(tuple66);
        assert vertex.getCompressedContainers().size() == 1;
        assert gcgcChild.getUncompressedContainer().size() == 5;
        assert vertex.getUncompressedContainer().size() == 1;

        // GCGCHILD BURST
        Tuple tuple77 = new Tuple("taga");
        Tuple tuple88 = new Tuple("gcgc", "gray");
        gcgcChild.insert(tuple77);
        vertex.insert(tuple88);
        assert gcgcChild.getUncompressedContainer().size() == 1;

        System.out.println(vertex);
        System.out.println(gcgcChild);
        printCompressedContainers(gcgcChild.getCompressedContainers());
    }


    @Test
    public void testContainsSuffix() {

        // set up new tuples (first 5 have been created in @Before)
        Tuple tuple1 = new Tuple("aggctatgctca", "pink");
        Tuple tuple2 = new Tuple("aggctgcattgt", "orange");
        Tuple tuple3 = new Tuple("ctcatttgataa", "grey");
        Tuple tuple4 = new Tuple("gccctgcattgt", "green");
        Tuple tuple5 = new Tuple("gcgctatgctga", "teal");
        Tuple tuple6 = new Tuple("gcgccaggaatc", "violet"); // tuple6 does not exist in mVertex yet

        // test contains before burst
        assert mVertex.containsSuffix(tuple1);
        assert mVertex.containsSuffix(tuple2);
        assert mVertex.containsSuffix(tuple3);
        assert mVertex.containsSuffix(tuple4);
        assert mVertex.containsSuffix(tuple5);
        assert mVertex.containsSuffix(tuple6) == false;


        // new insertion/burst
        mVertex.insert(tuple6); // BURST
        assert mVertex.containsSuffix(tuple1);
        assert mVertex.containsSuffix(tuple2);
        assert mVertex.containsSuffix(tuple3);
        assert mVertex.containsSuffix(tuple4);
        assert mVertex.containsSuffix(tuple5);
        assert mVertex.containsSuffix(tuple6);
        assert mVertex.containsSuffix(new Tuple("aggctatgctcaat", "green")) == false;
        assert mVertex.containsSuffix(new Tuple("ttgagacattag", "brown")) == false;
        assert mVertex.containsSuffix(new Tuple("gagagacattag")) == false;
        assert mVertex.containsSuffix(new Tuple("gagagacattag")) == false;
        assert mVertex.containsSuffix(new Tuple("aagagacata")) == false;
    }

    @Test
    public void testContains() {

        // set up new tuples (suffixes of the first 5 have been created in @Before)
        Tuple tuple1 = new Tuple("aggctatgctca", "red");
        Tuple tuple2 = new Tuple("aggctgcattgt", "orange"); // same suffix as 2, but w/ different color
        Tuple tuple3 = new Tuple("ctcatttgataa", "yellow");
        Tuple tuple4 = new Tuple("gccctgcattgt", "green"); // same suffix as 4, but w/ different color
        Tuple tuple5 = new Tuple("gcgctatgctga", "blue");
        Tuple tuple6 = new Tuple("gcgccaggaatc", "red"); // tuple6 does not exist in mVertex yet


        // test contains before burst
        assert mVertex.contains(tuple1);
        assert mVertex.contains(tuple2) == false;
        assert mVertex.contains(tuple3);
        assert mVertex.contains(tuple4) == false;
        assert mVertex.contains(tuple5);
        assert mVertex.contains(tuple6) == false;


        // new insertion/burst
        mVertex.insert(tuple6); // BURST
        assert mVertex.contains(tuple1);
        assert mVertex.contains(new Tuple("aggctgcattgt", "yellow")); // test original color
        assert mVertex.contains(tuple2) == false; // test new (non-existent) color
        assert mVertex.contains(tuple3);
        assert mVertex.contains(new Tuple("gccctgcattgt", "blue"));
        assert mVertex.contains(tuple4) == false;
        assert mVertex.contains(tuple5);
        assert mVertex.contains(tuple6);
        assert mVertex.contains(new Tuple("aggctatgctcaat")) == false;
        assert mVertex.contains(new Tuple("aggctgcattgt", "yellow"));
        assert mVertex.contains(new Tuple("aggctatgctca", "yellow")) == false;
        assert mVertex.contains(new Tuple("aggctatgctca", "red"));
        assert mVertex.contains(new Tuple("gagagacattag")) == false;
        assert mVertex.contains(new Tuple("gcgccaggaatc", "red"));
        assert mVertex.contains(new Tuple("gcgccaggaatc", "violet")) == false;
        assert mVertex.contains(new Tuple("aagagacata")) == false;
    }


    private void printCompressedContainers(ArrayList<CompressedContainer> compressedContainers) {
        for (int i = 0; i < compressedContainers.size(); i++) {
            System.out.println("CC #" + (i+1) + ":");
            System.out.println(compressedContainers);
            ArrayList<BFTVertex> childVertices = compressedContainers.get(i).getChildVertices();
            for (int j = 0; j < childVertices.size(); j++)
                System.out.println(childVertices.get(j).getUncompressedContainer());
        }
    }

    private String randomPrefix(int prefixSize) {
        Random random = new Random();
        String randomPrefix = "";
        for (int j = 0; j < prefixSize; j++) {
            int r = random.nextInt(4);
            switch (r) {
                case 0:
                    randomPrefix += "a";
                    break;
                case 1:
                    randomPrefix += "c";
                    break;
                case 2:
                    randomPrefix += "g";
                    break;
                case 3:
                    randomPrefix += "t";
                    break;
                default:
                    break;
            }
        }

        return randomPrefix;
    }

}
