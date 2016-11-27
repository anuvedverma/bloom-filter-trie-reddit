package edu.uchicago.mpcs56420;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by Anuved on 11/27/2016.
 */
public class BFTVertexTest {

    private BFTVertex mVertex;

    @Before
    public void initBFTVertex() {
        mVertex = new BFTVertex();
        assert BFTVertex.NUM_VERTICES == 1;

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
    }

    @Test
    public void testInsert() {

        ArrayList<CompressedContainer> compressedContainers = mVertex.getCompressedContainers();
        UncompressedContainer uncompressedContainer = mVertex.getUncompressedContainer();

        // test init conditions
        assert compressedContainers.size() == 0;
        assert uncompressedContainer.size() == 5;
        assert uncompressedContainer.numColors() == 5;

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
    public void testBursting() {
        ArrayList<CompressedContainer> compressedContainers = mVertex.getCompressedContainers();
        UncompressedContainer uncompressedContainer = mVertex.getUncompressedContainer();

        // test init conditions
        assert compressedContainers.size() == 0;
        assert uncompressedContainer.size() == 5;
        assert uncompressedContainer.numColors() == 5;

        // test new insertion/burst
        mVertex.insert(new Tuple("gcgccaggaatc", "red")); // BURST
        compressedContainers = mVertex.getCompressedContainers();
        for (int i = 0; i < compressedContainers.size(); i++)
            System.out.println(compressedContainers.get(i));

        uncompressedContainer = mVertex.getUncompressedContainer();
        assert compressedContainers.size() == 1;
        assert uncompressedContainer.size() == 0;
        assert uncompressedContainer.numColors() == 0;


        for (int i = 0; i < compressedContainers.size(); i++) {
            System.out.println("CC #" + (i+1) + ":");
            System.out.println(compressedContainers);
            ArrayList<BFTVertex> childVertices = compressedContainers.get(i).getChildVertices();
            for (int j = 0; j < childVertices.size(); j++)
                System.out.println(childVertices.get(j).getUncompressedContainer());
        }

    }



}
