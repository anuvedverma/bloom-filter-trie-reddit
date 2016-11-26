package edu.uchicago.mpcs56420;

import java.util.BitSet;
import java.util.Comparator;

/**
 * Created by Anuved on 11/25/2016.
 */
public class Tuple implements Comparable<Tuple> {

    private static final int BIT_ARRAY_SIZE = 100;

    private String mSuffix;
    private BitSet mColors;

    public Tuple(String suffix, BitSet colors) {
        mSuffix = suffix;
        mColors = colors;
    }

    public Tuple(String suffix) {
        mSuffix = suffix;
        mColors = new BitSet();
//        mColors = new BitSet(BIT_ARRAY_SIZE);
    }

    public Tuple(String suffix, String... colors) {
        mSuffix = suffix;
        mColors = new BitSet();
        for(String color : colors)
            addColor(color);
    }

    public String getSuffix() {
        return mSuffix;
    }

    public void setSuffix(String suffix) {
        mSuffix = suffix;
    }

    public BitSet getColors() {
        return mColors;
    }

    public void setColors(BitSet colors) {
        mColors = colors;
    }

    public void addColor(String color) {
        int index = colorHash(color);
        mColors.set(index);
    }

    /* Add colors from another set of colors using bit-wise OR operation */
    public void addColors(BitSet colors) {
        mColors.or(colors);
    }

    /* Checks if all color-bits from input BitSet are already present */
    public boolean hasColors(BitSet colors) {
        boolean hasColors = true;
        for (int i = colors.nextSetBit(0); i != -1; i = colors.nextSetBit(i + 1)) {
            hasColors = hasColors && (mColors.get(i) == true);
            if (!hasColors) break;
        }

        return hasColors;
    }


    /*
    * Override equals and hash functions to ensure that tuples with identical suffixes are evaluated the same, even
    * if they are stored as different objects in memory.
    * */
    @Override
    public boolean equals(Object object) {
        if(mSuffix.equals(((Tuple) object).getSuffix()))
            return true;
        else return false;
    }

    @Override
    public int hashCode() {
        return mSuffix.hashCode();
    }

    public static int colorHash(String color) {
        // return Math.abs(color.hashCode() % BIT_ARRAY_SIZE);
        return Math.abs(color.hashCode());
    }

    @Override
    public String toString() {
        return mSuffix + ": " + mColors.toString();
    }

    @Override
    public int compareTo(Tuple tuple) {
        return (this.mSuffix).compareTo(tuple.getSuffix());
    }
}