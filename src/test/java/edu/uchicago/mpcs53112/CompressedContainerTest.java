package edu.uchicago.mpcs53112;

import edu.uchicago.mpcs53112.BloomFilterTrie.CapacityExceededException;
import edu.uchicago.mpcs53112.BloomFilterTrie.CompressedContainer;
import edu.uchicago.mpcs53112.BloomFilterTrie.Container;
import edu.uchicago.mpcs53112.BloomFilterTrie.Tuple;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.BitSet;

/**
 * Created by Anuved on 11/26/2016.
 */
public class CompressedContainerTest {

    private final int CONTAINER_CAPACITY = 5;
    private static int PREFIX_LENGTH = 4;

    private CompressedContainer mCompressedContainer;

    @Before
    public void initCompressedContainer() throws CapacityExceededException {
        Container.setAlphabet(" abcdefghijklmnopqrstuvwxyz");
        Container.setCapacity(CONTAINER_CAPACITY);
        Container.setPrefixLength(PREFIX_LENGTH);

        mCompressedContainer = new CompressedContainer();
        mCompressedContainer.insert(new Tuple("this"));
        mCompressedContainer.insert(new Tuple("test"));
        mCompressedContainer.insert(new Tuple("teet"));
        mCompressedContainer.insert(new Tuple("anim"));
        mCompressedContainer.insert(new Tuple("anny"));
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
        correctPref.set(41);
        correctPref.set(545);
        correctPref.set(548);
//        correctPref.set(11);
        assert correctPref.equals(mCompressedContainer.getPref());


        // test suf values
        ArrayList<String> correctSuf = new ArrayList<>(Container.getCapacity());
        correctSuf.add("im");
        correctSuf.add("ny");
        correctSuf.add("et");
        correctSuf.add("st");
        correctSuf.add("is");
        for (int i = 0; i < sufClustSize; i++)
            assert correctSuf.get(i).equals(mCompressedContainer.getSuf().get(i));

        // test clust values
        ArrayList<Boolean> correctClust = new ArrayList<>(Container.getCapacity());
        correctClust.add(true);
        correctClust.add(false);
        correctClust.add(true);
        correctClust.add(false);
        correctClust.add(true);
        for (int i = 0; i < sufClustSize; i++)
            assert correctClust.get(i).equals(mCompressedContainer.getClust().get(i));

        System.out.println(mCompressedContainer);
    }

    @Test
    public void testContainsPrefix() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        assert mCompressedContainer.containsPrefix("this");
        assert mCompressedContainer.containsPrefix("test");
        assert mCompressedContainer.containsPrefix("teet");
        assert mCompressedContainer.containsPrefix("anim");
        assert mCompressedContainer.containsPrefix("anny");
        assert mCompressedContainer.containsPrefix("that") == false;
        assert mCompressedContainer.containsPrefix("anym") == false;
        assert mCompressedContainer.containsPrefix("catt") == false;
    }

}
