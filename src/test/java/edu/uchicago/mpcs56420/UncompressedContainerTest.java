package edu.uchicago.mpcs56420;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by Anuved on 11/25/2016.
 */
public class UncompressedContainerTest {

    @Test
    public void testInsertSortedness() throws CapacityExceededException {
        Tuple tuple1 = new Tuple("tcgt", "red", "potato");
        Tuple tuple2 = new Tuple("agct", "potato", "red");
        Tuple tuple3 = new Tuple("acgt", "red", "green");
        Tuple tuple4 = new Tuple("cggt", "red", "green");
        Tuple tuple5 = new Tuple("cagt", "red", "green");

        UncompressedContainer uc = new UncompressedContainer();
        uc.insert(tuple1);
        uc.insert(tuple2);
        uc.insert(tuple3);
        uc.insert(tuple4);
        uc.insert(tuple5);

        ArrayList<Tuple> ucTuples = uc.getTuples();
        for (int i = 1; i < ucTuples.size(); i++)
            assert ucTuples.get(i).compareTo(ucTuples.get(i - 1)) > 0;

        System.out.println(uc);
    }

    @Test
    public void testInsertDuplicates() throws CapacityExceededException {
        Tuple tuple1 = new Tuple("tcgt", "red", "potato");
        Tuple tuple2 = new Tuple("agct", "potato", "red");
        Tuple tuple3 = new Tuple("acgt", "red", "green");
        Tuple tuple4 = new Tuple("agct", "red", "green");
        Tuple tuple5 = new Tuple("acgt", "red", "green");

        UncompressedContainer uc = new UncompressedContainer();
        uc.insert(tuple1);
        uc.insert(tuple2);
        uc.insert(tuple3);
        uc.insert(tuple4);
        uc.insert(tuple5);

        ArrayList<Tuple> ucTuples = uc.getTuples();
        assert ucTuples.size() == 3;
        assert ucTuples.get(0).getColors().cardinality() == 2;
        assert ucTuples.get(1).getColors().cardinality() == 3;

        System.out.println(uc);
    }

    @Test(expected = CapacityExceededException.class)
    public void testInsertCapacity() throws CapacityExceededException {

        UncompressedContainer uc = new UncompressedContainer();
        int capacity = Container.getCapacity();
        for (int i = 0; i < capacity; i++) {
            String suffix = "acgt" + i;
            Tuple tuple = new Tuple(suffix);
            uc.insert(tuple);
        }

        assert uc.size() == capacity;

        Tuple newTuple = new Tuple("acgt0", "red");
        uc.insert(newTuple);
        System.out.println(uc);
        assert uc.contains(new Tuple("acgt0", "red"));

        newTuple = new Tuple("cagt");
        uc.insert(newTuple);
    }

    @Test
    public void testContainsSuffix() throws CapacityExceededException {
        Tuple tuple1 = new Tuple("tcgt", "red", "potato");
        Tuple tuple2 = new Tuple("agct", "potato", "red");
        Tuple tuple3 = new Tuple("acgt", "red", "green");
        Tuple tuple4 = new Tuple("cggt", "red", "green");
        Tuple tuple5 = new Tuple("cagt", "red", "green");

        UncompressedContainer uc = new UncompressedContainer();
        uc.insert(tuple1);
        uc.insert(tuple2);
        uc.insert(tuple3);
        uc.insert(tuple4);
        uc.insert(tuple5);

        assert uc.containsSuffix(new Tuple("tcgt", "red", "potato"));
        assert uc.containsSuffix(new Tuple("tcgt"));
        assert uc.containsSuffix(new Tuple("cggt", "blue", "red"));
        assert uc.containsSuffix(new Tuple("acgt", "yellow"));
        assert uc.containsSuffix(new Tuple("cgga")) == false;

        System.out.println(uc);
    }

    @Test
    public void testContains() throws CapacityExceededException {
        Tuple tuple1 = new Tuple("tcgt", "red", "potato", "yellow");
        Tuple tuple2 = new Tuple("agct", "potato", "red");
        Tuple tuple3 = new Tuple("acgt", "red", "green");
        Tuple tuple4 = new Tuple("cggt", "red", "green");
        Tuple tuple5 = new Tuple("cagt", "red", "green");

        UncompressedContainer uc = new UncompressedContainer();
        uc.insert(tuple1);
        uc.insert(tuple2);
        uc.insert(tuple3);
        uc.insert(tuple4);
        uc.insert(tuple5);

        assert uc.contains(new Tuple("tcgt", "red", "potato", "yellow"));
        assert uc.contains(new Tuple("tcgt"));
        assert uc.contains(new Tuple("tcgt", "red"));
        assert uc.contains(new Tuple("cggt", "green", "red"));
        assert uc.contains(new Tuple("cggt", "green", "blue")) == false;
        assert uc.contains(new Tuple("acgt", "red"));
        assert uc.contains(new Tuple("acgt", "yellow")) == false;
        assert uc.contains(new Tuple("cgga")) == false;

        System.out.println(uc);
    }
}
