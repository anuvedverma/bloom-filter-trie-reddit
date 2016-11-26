package edu.uchicago.mpcs56420;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.BitSet;

/**
 * Created by Anuved on 11/26/2016.
 */
public class CompressedContainerTest {

    private CompressedContainer mCompressedContainer;

    @Before
    public void initCompressedContainer() {
        mCompressedContainer = new CompressedContainer();

    }

    @Test
    public void testInsertAndContains() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // use reflection to test private insert method
        Method insert = CompressedContainer.class.getDeclaredMethod("insert", String.class);
        insert.setAccessible(true);

        // test method
        insert.invoke(mCompressedContainer, "aggc");
        insert.invoke(mCompressedContainer, "ctca");
        insert.invoke(mCompressedContainer, "gccc");
        insert.invoke(mCompressedContainer, "gcgc");
        insert.invoke(mCompressedContainer, "gtat");

        // test mPref values
        BitSet correctPref = new BitSet(CompressedContainer.getPrefSize());
        correctPref.set(2);
        correctPref.set(7);
        correctPref.set(9);
        correctPref.set(11);
        assert correctPref.equals(mCompressedContainer.getPref());

        // test mSuf values
        ArrayList<String> correctSuf = new ArrayList<>(Container.getCapacity());
        correctSuf.add("gc");
        correctSuf.add("ca");
        correctSuf.add("cc");
        correctSuf.add("gc");
        correctSuf.add("at");
        for (int i = 0; i < mCompressedContainer.getSuf().size(); i++)
            assert correctSuf.get(i).equals(mCompressedContainer.getSuf().get(i));

        // test mClust values
        ArrayList<Boolean> correctClust = new ArrayList<>(Container.getCapacity());
        correctClust.add(true);
        correctClust.add(true);
        correctClust.add(true);
        correctClust.add(false);
        correctClust.add(true);
        for (int i = 0; i < mCompressedContainer.getClust().size(); i++)
            assert correctClust.get(i).equals(mCompressedContainer.getClust().get(i));


        // use reflection to test private contains method
        Method contains = CompressedContainer.class.getDeclaredMethod("contains", String.class);
        contains.setAccessible(true);

        Object output;
        output = contains.invoke(mCompressedContainer, "aggc");
        assert ((Boolean) output == true);
        output = contains.invoke(mCompressedContainer, "ctca");
        assert ((Boolean) output == true);
        output = contains.invoke(mCompressedContainer, "gccc");
        assert ((Boolean) output == true);
        output = contains.invoke(mCompressedContainer, "gcgc");
        assert ((Boolean) output == true);
        output = contains.invoke(mCompressedContainer, "gtat");
        assert ((Boolean) output == true);
        output = contains.invoke(mCompressedContainer, "aatt");
        assert ((Boolean) output == false);
        output = contains.invoke(mCompressedContainer, "tatt");
        assert ((Boolean) output == false);
        output = contains.invoke(mCompressedContainer, "catt");
        assert ((Boolean) output == false);


        System.out.println(mCompressedContainer);
    }


    @Test
    public void testHammingWeight() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // use reflection to test private methods
//        Method method = CompressedContainer.class.getDeclaredMethod("hammingWeight", int.class);
//        method.setAccessible(true);

        // test method
//        Object output;
//        output = method.invoke(mCompressedContainer, 1);
//        assert ((Boolean) output == true);
//        output = method.invoke(mCompressedContainer, 2);
//        assert ((Boolean) output == true);
    }

}
