package edu.uchicago.mpcs56420;

import org.apache.commons.lang.StringUtils;

import java.util.BitSet;

/**
 * Created by Anuved on 11/25/2016.
 */
public class Tuple implements Comparable<Tuple> {

	private static final int BIT_ARRAY_SIZE = 100;

	private String mSequence; // referred to as "suffix" in the paper... renamed to "sequence" for clarity
	private BitSet mColors;

	public Tuple(String sequence, BitSet colors) {
		mSequence = sequence;
		mColors = colors;
	}

	public Tuple(String sequence) {
		mSequence = sequence;
//		mColors = new BitSet();
        mColors = new BitSet(BIT_ARRAY_SIZE);
	}

	public Tuple(String sequence, String... colors) {
		mSequence = sequence;
		mColors = new BitSet();
		for(String color : colors)
			addColor(color);
	}

	public Tuple(Tuple tuple) {
		mSequence = tuple.getSequence();
		mColors = tuple.getColors();
	}

	public String getSequence() {
		return mSequence;
	}

	public void setSequence(String sequence) {
		mSequence = sequence;
	}

	public BitSet getColors() { return (BitSet) mColors.clone(); }

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

	/* Returns the number of unique colors */
	public int numColors() { return mColors.cardinality(); }

	/* Checks if all color-bits from input BitSet are already present */
	public boolean hasColors(BitSet colors) {
		boolean hasColors = true;
		for (int i = colors.nextSetBit(0); i != -1; i = colors.nextSetBit(i + 1)) {
			hasColors = hasColors && (mColors.get(i) == true);
			if (!hasColors) break;
		}

		return hasColors;
	}


	/* Gets prefix of suffix string for compression and indexing algorithm */
	public String getPrefix(int sfpxLength) {
		if(mSequence.isEmpty())
			return "";

		String sfpx = mSequence.substring(0, sfpxLength);
		return sfpx;
	}

	/* Truncates suffix string for compression and indexing algorithm */
	public String emitPrefix(int sfpxLength) {
		String sfpx = mSequence.substring(0, sfpxLength);
		mSequence = mSequence.substring(sfpxLength);
		return sfpx;
	}

    /* Gets suffix of suffix string for compression and indexing algorithm */
    public String getSuffix(int sfpxLength) {
        String sfsf = mSequence.substring(sfpxLength);
        return sfsf;
    }


	/*
	* Override equals and hash functions to ensure that tuples with identical suffixes are evaluated the same, even
	* if they are stored as different objects in memory.
	* */
	@Override
	public boolean equals(Object object) {
		if(mSequence.equals(((Tuple) object).getSequence()))
			return true;
		else return false;
	}

	@Override
	public int hashCode() {
		return mSequence.hashCode();
	}

	public static int colorHash(String color) {
		 return Math.abs(color.hashCode() % BIT_ARRAY_SIZE);
//		return Math.abs(color.hashCode());
	}

	@Override
	public String toString() {
		return mSequence + ": " + mColors.toString();
	}

	@Override
	public int compareTo(Tuple tuple) {
		return (this.mSequence).compareTo(tuple.getSequence());
	}
}