package edu.uchicago.mpcs56420;

import edu.uchicago.mpcs56420.BloomFilterTrie.Tuple;
import org.junit.Test;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;

/**
 * Created by Anuved on 11/25/2016.
 */
public class TupleTest {

    @Test
    public void testAddColorTrue() {
        String testString = "agtc";
        Tuple tuple1 = new Tuple(testString, "red", "potato");
        Tuple tuple2 = new Tuple(testString, "potato", "red");
        assert tuple1.getColors().equals(tuple2.getColors());
    }

    @Test
    public void testAddColorFalse() {
        String testString = "agtc";
        Tuple tuple1 = new Tuple(testString, "red", "green");
        Tuple tuple2 = new Tuple(testString, "potato", "red");
        assert tuple1.getColors().equals(tuple2.getColors()) == false;
    }

    @Test
    public void testAddColors() {
        String testString = "agtc";
        String color1 = "red";
        String color2 = "green";
        String color3 = "potato";

        Tuple tuple1 = new Tuple(testString, color1, color2);
        Tuple tuple2 = new Tuple(testString, color3, color1);

        System.out.println(tuple1);
        System.out.println(tuple2);

        tuple1.addColors(tuple2.getColors());
        System.out.println(tuple1);

        // set correct color bit-sets
        BitSet correctColors = new BitSet();
        correctColors.set(Tuple.colorHash(color1));
        correctColors.set(Tuple.colorHash(color2));
        correctColors.set(Tuple.colorHash(color3));

        assert tuple1.getColors().equals(correctColors);
    }

    /* Test tuple equality based on suffix string */
    @Test
    public void testEquals() {
        String testString = "agtc";
        Tuple tuple1 = new Tuple(testString, "red");
        Tuple tuple2 = new Tuple(testString, "green");
        assert (tuple1.equals(tuple2));
    }

    @Test
    public void testNotEquals() {
        Tuple tuple1 = new Tuple("agtc", "red");
        Tuple tuple2 = new Tuple("agtt", "red");
        assert (tuple1.equals(tuple2) == false);
    }

    /* Test tuple hash codes based on suffix */
    @Test
    public void testHashCodeSame() {
        String testString = "agtc";
        Tuple tuple1 = new Tuple(testString, "red");
        Tuple tuple2 = new Tuple(testString, "green");

        // test hashset behaviour
        HashSet<Tuple> hashSet = new HashSet<>();
        hashSet.add(tuple1);
        assert (hashSet.contains(tuple2) == true);
        hashSet.add(tuple2);
        assert (hashSet.size() == 1);

        // test arraylist behaviour
        ArrayList<Tuple> arrayList = new ArrayList<>();
        arrayList.add(tuple1);
        assert (arrayList.contains(tuple2) == true);
        arrayList.add(tuple2);
        assert (arrayList.size() == 2);
    }

    @Test
    public void testHashCodeDifferent() {
        Tuple tuple1 = new Tuple("agtc", "red");
        Tuple tuple2 = new Tuple("agtt", "green");

        // test hashset behaviour
        HashSet<Tuple> hashSet = new HashSet<>();
        hashSet.add(tuple1);
        assert (hashSet.contains(tuple2) == false);
        hashSet.add(tuple2);
        assert (hashSet.size() == 2);

        // test arraylist behaviour
        ArrayList<Tuple> arrayList = new ArrayList<>();
        arrayList.add(tuple1);
        assert (arrayList.contains(tuple2) == false);
        arrayList.add(tuple2);
        assert (arrayList.size() == 2);
    }

    @Test
    public void testEmitPrefix() {
        Tuple tuple = new Tuple("aggctatgctca", "red", "blue");
        assert tuple.emitPrefix(4).equals("aggc");
        assert tuple.getSequence().equals("tatgctca");
        assert tuple.emitPrefix(4).equals("tatg");
        assert tuple.getSequence().equals("ctca");
    }

}
