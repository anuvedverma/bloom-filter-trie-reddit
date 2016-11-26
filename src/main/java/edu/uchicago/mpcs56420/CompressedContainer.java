package edu.uchicago.mpcs56420;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.BitSet;

/**
 * Created by Anuved on 11/25/2016.
 */
public class CompressedContainer extends Container {

	// pref bit-array size is 2 ^ (number of bits needed to represent suffix-prefix prefix)
	private static final int PREF_SIZE = (int) Math.pow(2, (double) Container.numBitsNeededForAlphabet() * Container.getSfpxPrefixSize());

	private BloomFilter<String> mQuer;
	private BitSet mPref;
	private ArrayList<String> mSuf;
	private ArrayList<Boolean> mClust;

	public CompressedContainer() {
		mQuer = BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8), getCapacity());
		mPref = new BitSet(PREF_SIZE);
		mSuf = new ArrayList<>(getCapacity());
		mClust = new ArrayList<>(getCapacity());
	}

    /* Getters */
    public BloomFilter<String> getQuer() { return mQuer.copy(); }

    public BitSet getPref() { return (BitSet) mPref.clone(); }

    public ArrayList<String> getSuf() { return new ArrayList<>(mSuf); }

    public ArrayList<Boolean> getClust() { return new ArrayList<>(mClust); }

    public static int getPrefSize() { return PREF_SIZE; }

    @Override
	public boolean contains(Tuple tuple) {
		return false;
	}

	@Override
	public boolean containsSuffix(Tuple tuple) {
		return false;
	}

	@Override
	public void insert(Tuple newTuple) throws CapacityExceededException {

	}

	/* Algorithm to efficiently check if CompressedContainer stores a given suffix's prefix */
	private boolean contains(String sfpx) {
		String sfpxPrefix = sfpx.substring(0, getSfpxPrefixSize());
		String sfpxSuffix = sfpx.substring(getSfpxPrefixSize());
		int prefIndex = getIndexOnPref(sfpxPrefix);

		if(mPref.get(prefIndex) == true) {
			int clusterNum = hammingWeight(prefIndex);
			int start = rank(clusterNum);
			int pos = start;
			while(pos <= mSuf.size() && (pos == start || mClust.get(pos) == false)) {
				if(mSuf.get(pos).equals(sfpxSuffix)) return true;
				pos++;
			}
		}

		return false;
	}

	private void insert(String sfpx) {
		String sfpxPrefix = sfpx.substring(0, getSfpxPrefixSize());
		String sfpxSuffix = sfpx.substring(getSfpxPrefixSize());
		int prefIndex = getIndexOnPref(sfpxPrefix);

		boolean wasPrefIndexSet = mPref.get(prefIndex);
		mPref.set(prefIndex);

		int clusterNum = hammingWeight(prefIndex);
		int clusterPos = rank(clusterNum);

        // sfpxPrefix was not present
        if(!wasPrefIndexSet) {
            mSuf.add(clusterPos, sfpxSuffix);
            mClust.add(clusterPos, true);
            return;
        }

        // sfpxPrefix was already present
        if(wasPrefIndexSet) {

            // if sfpxSuffix starts its cluster...
            boolean isNewSfxLessThanPos = sfpxSuffix.compareTo(mSuf.get(clusterPos)) < 0;
            if(isNewSfxLessThanPos) {
                mSuf.add(clusterPos, sfpxSuffix);
                mClust.add(clusterPos+1, false);
                return;
            }

            // if sfpxSuffix does not start its cluster...
            clusterPos++;

            // if clusterPos is at end of bit-array
            if(clusterPos >= mClust.size()) {
                mSuf.add(clusterPos, sfpxSuffix);
                mClust.add(clusterPos, false);
                return;
            }

            // while sfpxSuffix is greater than previous suffix...
            boolean isNewSfxGreaterThanPrevPos = sfpxSuffix.compareTo(mSuf.get(clusterPos-1)) > 0;
            while (isNewSfxGreaterThanPrevPos) {
                // if next cluster is reached (ie. if sfpxSuffix is greatest in its cluster)...
                if(mClust.get(clusterPos)) {
                    mSuf.add(clusterPos, sfpxSuffix);
                    mClust.add(clusterPos, false);
                    return;
                }
                clusterPos++;
                isNewSfxGreaterThanPrevPos = sfpxSuffix.compareTo(mSuf.get(clusterPos-1)) > 0;
            }

            // if sfpxSuffix is less than previous suffix...
            mSuf.add(clusterPos-1, sfpxSuffix);
            mClust.add(clusterPos-1, false);
            return;
        }
	}

	/**** Auxiliary functions ****/

	private int hammingWeight(int index) {
		int hammingWeight = 0;
		for (int i = mPref.nextSetBit(0); i > -1 && i <= index; i = mPref.nextSetBit(i + 1))
			hammingWeight++;
		return hammingWeight;
	}

	private int rank(int clusterNum) {
        for (int clusterPos = 0; clusterPos < mClust.size(); clusterPos++) {
            if(mClust.get(clusterPos) == true)
                clusterNum--;
            if(clusterNum == 0)
                return clusterPos;
        }

        return mClust.size();
    }

	/* Converts prefix string to its location on mPref bit-array */
	private int getIndexOnPref(String sfpxPrefix) {
		int prefIndex = 0;
		for (int i = 0; i < sfpxPrefix.length(); i++) // binary index = sum((|A|^i) * c)
			prefIndex += Math.pow(getAlphabetSize(), getSfpxPrefixSize() - (i + 1)) * getAlphabet().indexOf(sfpxPrefix.charAt(i));

		return prefIndex;
	}

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        // print quer
        output.append("quer: " + mQuer + "\n");

        // print pref
        output.append("pref: " + mPref + "\n");

        // print suf
        output.append("suf: ");
        for (int i = 0; i < mSuf.size(); i++)
            output.append(mSuf.get(i) + " ");
        output.append("\n");

        // print clust
        output.append("clust: ");
        for (int i = 0; i < mClust.size(); i++)
            output.append(mClust.get(i) + " ");
        output.append("\n");

        return output.toString();
    }
}
