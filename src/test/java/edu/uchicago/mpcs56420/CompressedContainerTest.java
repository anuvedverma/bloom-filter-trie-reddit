package edu.uchicago.mpcs56420;

import edu.uchicago.mpcs56420.BloomFilterTrie.CapacityExceededException;
import edu.uchicago.mpcs56420.BloomFilterTrie.CompressedContainer;
import edu.uchicago.mpcs56420.BloomFilterTrie.Container;
import edu.uchicago.mpcs56420.BloomFilterTrie.Tuple;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.BitSet;

/**
 * Created by Anuved on 11/26/2016.
 */
public class CompressedContainerTest {

    private CompressedContainer mCompressedContainer;

    @Before
    public void initCompressedContainer() throws CapacityExceededException {
        mCompressedContainer = new CompressedContainer();
        mCompressedContainer.insert(new Tuple("aggc"));
        mCompressedContainer.insert(new Tuple("ctca"));
        mCompressedContainer.insert(new Tuple("ctca"));
        mCompressedContainer.insert(new Tuple("gccc", "red"));
        mCompressedContainer.insert(new Tuple("gccc"));
        mCompressedContainer.insert(new Tuple("gcgc"));
        mCompressedContainer.insert(new Tuple("gtat"));
    }

    @Test
    public void testInsert() {

        ArrayList<String> suf = mCompressedContainer.getSuf();
        for (int i = 0; i < suf.size(); i++) {
            System.out.println(suf.get(i));
        }

        // test mSufClustData values
        int sufClustSize = mCompressedContainer.size();
        assert sufClustSize == 5;

        // test mPref values
        BitSet correctPref = new BitSet(CompressedContainer.getPrefSize());
        correctPref.set(2);
        correctPref.set(7);
        correctPref.set(9);
        correctPref.set(11);
        assert correctPref.equals(mCompressedContainer.getPref());


        // test suf values
        ArrayList<String> correctSuf = new ArrayList<>(Container.getCapacity());
        correctSuf.add("gc");
        correctSuf.add("ca");
        correctSuf.add("cc");
        correctSuf.add("gc");
        correctSuf.add("at");
        for (int i = 0; i < sufClustSize; i++)
            assert correctSuf.get(i).equals(mCompressedContainer.getSuf().get(i));

        // test clust values
        ArrayList<Boolean> correctClust = new ArrayList<>(Container.getCapacity());
        correctClust.add(true);
        correctClust.add(true);
        correctClust.add(true);
        correctClust.add(false);
        correctClust.add(true);
        for (int i = 0; i < sufClustSize; i++)
            assert correctClust.get(i).equals(mCompressedContainer.getClust().get(i));

        System.out.println(mCompressedContainer);
    }

    @Test
    public void testContainsPrefix() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        assert mCompressedContainer.containsPrefix("aggc");
        assert mCompressedContainer.containsPrefix("ctca");
        assert mCompressedContainer.containsPrefix("gccc");
        assert mCompressedContainer.containsPrefix("gcgc");
        assert mCompressedContainer.containsPrefix("gtat");
        assert mCompressedContainer.containsPrefix(new Tuple("gtat", "red"));
        assert mCompressedContainer.containsPrefix("aatt") == false;
        assert mCompressedContainer.containsPrefix("tatt") == false;
        assert mCompressedContainer.containsPrefix("catt") == false;
        assert mCompressedContainer.containsPrefix(new Tuple("catt", "blue")) == false;

    }

}
