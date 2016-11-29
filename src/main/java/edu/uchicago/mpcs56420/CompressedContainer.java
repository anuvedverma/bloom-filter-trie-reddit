package edu.uchicago.mpcs56420;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.BitSet;

/**
 * Created by Anuved on 11/25/2016.
 */
public class CompressedContainer extends Container {

	// pref bit-array size is 2 ^ (number of bits needed to represent suffix-prefix prefix)
	private static final int PREF_SIZE = (int) Math.pow(2, (double) Container.numBitsNeededForAlphabet() * Container.getPrfxPrefixLength());

    // data stored in compressed container
	private BloomFilter<String> mQuer;
	private BitSet mPref;
    private ArrayList<SufClustData> mSufClustData; // a SufClustData object stores a single suf, clust, and childVertex element

	public CompressedContainer() {
		mQuer = BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8), getCapacity());
		mPref = new BitSet(PREF_SIZE);
        mSufClustData = new ArrayList<>(getCapacity());
	}

    /* Uses bloom filter to efficiently test whether or not suffix-prefix is contained */
    public boolean mayContain(String sfpx) {
        return mQuer.mightContain(sfpx);
    }

    /* Returns the child vertex associated with a given suffix-prefix (sfpx) */
    public BFTVertex getChildOf(String sfpx) {
        int indexOfChild = indexOf(sfpx);

        if(indexOfChild > -1)
            return mSufClustData.get(indexOfChild).getChildVertex();

        return null;
    }

    /* Tests whether a prefix of input tuple exists in CompressedContainer */
    public boolean containsPrefix(Tuple tuple) {
        String sfpx = tuple.getPrefix(getPrefixLength());
        return containsPrefix(sfpx);
    }

	/* Algorithm to efficiently check if CompressedContainer stores a given suffix-prefix (sfpx) */
	public boolean containsPrefix(String sfpx) {

        if(sfpx.isEmpty())
            return true;

		String sfpxPrefix = sfpx.substring(0, getPrfxPrefixLength());
		String sfpxSuffix = sfpx.substring(getPrfxPrefixLength());
		int prefIndex = getIndexInPref(sfpxPrefix);

		if(mPref.get(prefIndex) == true) {
			int clusterNum = hammingWeight(prefIndex);
			int start = rank(clusterNum);
			int pos = start;
			while(pos < mSufClustData.size() && (pos == start || mSufClustData.get(pos).isClusterStart() == false)) {
                if(mSufClustData.get(pos).getSfpxSuffix().equals(sfpxSuffix)) return true;
				pos++;
			}
		}

		return false;
	}

    /* Inserts the suffix-prefix of an input tuple into the CompressedContainer */
    @Override
    public void insert(Tuple newTuple) throws CapacityExceededException {
        String sfpx = newTuple.getPrefix(getPrefixLength());
        try {
            insert(sfpx);
        } catch (CapacityExceededException e) {
            throw new CapacityExceededException(e.getLastTuple());
        }
    }

    /* Algorithm for inserting a suffix-prefix (sfpx) into the CompressedContainer */
    private void insert(String sfpx) throws CapacityExceededException {

        if(containsPrefix(sfpx))
            return;

        if((mSufClustData.size() == getCapacity()))
            throw new CapacityExceededException();

        String sfpxPrefix = sfpx.substring(0, getPrfxPrefixLength());
        String sfpxSuffix = sfpx.substring(getPrfxPrefixLength());
        int prefIndex = getIndexInPref(sfpxPrefix);

        boolean wasPrefIndexSet = mPref.get(prefIndex);
        mPref.set(prefIndex);

        int clusterNum = hammingWeight(prefIndex);
        int clusterPos = rank(clusterNum);

        // sfpxPrefix was not present
        if(!wasPrefIndexSet) {
            SufClustData sufClustData = new SufClustData(sfpxSuffix, true);
            mSufClustData.add(clusterPos, sufClustData);
            addToBloomFilter(sfpx);
            return;
        }

        // sfpxPrefix was already present
        if(wasPrefIndexSet) {

            // if sfpxSuffix starts its cluster...
            boolean isNewSfxLessThanPos = sfpxSuffix.compareTo(mSufClustData.get(clusterPos).getSfpxSuffix()) < 0;
            if(isNewSfxLessThanPos) {
                SufClustData sufClustData = new SufClustData(sfpxSuffix, true);
                mSufClustData.add(clusterPos, sufClustData);
                mSufClustData.get(clusterPos+1).setClusterStart(false);
                addToBloomFilter(sfpx);
                return;
            }

            // if sfpxSuffix does not start its cluster...
            clusterPos++;

            // if clusterPos is at end of bit-array
            if(clusterPos >= mSufClustData.size()) {
                SufClustData sufClustData = new SufClustData(sfpxSuffix, false);
                mSufClustData.add(clusterPos, sufClustData);
                addToBloomFilter(sfpx);
                return;
            }

            // while sfpxSuffix is greater than previous suffix...
            boolean isNewSfxGreaterThanPrevPos = sfpxSuffix.compareTo(mSufClustData.get(clusterPos-1).getSfpxSuffix()) > 0;
            while (isNewSfxGreaterThanPrevPos) {
                // if clusterPos is at end of bit-array OR if next cluster is reached (ie. if sfpxSuffix is greatest in its cluster)...
                if(clusterPos >= mSufClustData.size() || mSufClustData.get(clusterPos).isClusterStart()) {
                    SufClustData sufClustData = new SufClustData(sfpxSuffix, false);
                    mSufClustData.add(clusterPos, sufClustData);
                    addToBloomFilter(sfpx);
                    return;
                }

                clusterPos++;
                isNewSfxGreaterThanPrevPos = sfpxSuffix.compareTo(mSufClustData.get(clusterPos - 1).getSfpxSuffix()) > 0;
            }

            // if sfpxSuffix is less than previous suffix...
            SufClustData sufClustData = new SufClustData(sfpxSuffix, false);
            mSufClustData.add(clusterPos-1, sufClustData);
            addToBloomFilter(sfpx);
            return;
        }
    }

    /************* Auxiliary functions *********************/

	private int hammingWeight(int index) {
		int hammingWeight = 0;
		for (int i = mPref.nextSetBit(0); i > -1 && i <= index; i = mPref.nextSetBit(i + 1))
			hammingWeight++;
		return hammingWeight;
	}

	private int rank(int clusterNum) {
        int clustSize = mSufClustData.size();
        for (int clusterPos = 0; clusterPos < clustSize; clusterPos++) {
            if(mSufClustData.get(clusterPos).isClusterStart())
                clusterNum--;
            if(clusterNum == 0)
                return clusterPos;
        }

        return mSufClustData.size();
    }

	/* Converts prefix string to its location on mPref bit-array */
	private int getIndexInPref(String sfpxPrefix) {
		int prefIndex = 0;
		for (int i = 0; i < sfpxPrefix.length(); i++) // binary index = sum((|A|^i) * c)
			prefIndex += Math.pow(getAlphabetSize(), getPrfxPrefixLength() - (i + 1)) * getAlphabet().indexOf(sfpxPrefix.charAt(i));

		return prefIndex;
	}

    /* Algorithm to return index of the child vertex that holds a given suffix's prefix */
    private int indexOf(String sfpx) {
        String sfpxPrefix = sfpx.substring(0, getPrfxPrefixLength());
        String sfpxSuffix = sfpx.substring(getPrfxPrefixLength());
        int prefIndex = getIndexInPref(sfpxPrefix);

        if(mPref.get(prefIndex) == true) {
            int clusterNum = hammingWeight(prefIndex);
            int start = rank(clusterNum);
            int pos = start;
            while(pos < mSufClustData.size() && (pos == start || !mSufClustData.get(pos).isClusterStart())) {
                if(mSufClustData.get(pos).getSfpxSuffix().equals(sfpxSuffix)) return pos;
                pos++;
            }
        }

        return -1;
    }

    /* Adds suffix-prefix (sfpx) to Bloom filter mQuer */
    private void addToBloomFilter(String sfpx) {
        mQuer.put(sfpx);
    }


    /* Getters */
    public int size() { return mSufClustData.size(); }

    public BloomFilter<String> getQuer() { return mQuer.copy(); }

    public BitSet getPref() { return (BitSet) mPref.clone(); }

    public ArrayList<SufClustData> getSufClustData() { return new ArrayList<>(mSufClustData); }

    public ArrayList<String> getSuf() {
        ArrayList<String> suf = new ArrayList<>(getCapacity());
        for (int i = 0; i < mSufClustData.size(); i++)
            suf.add(mSufClustData.get(i).getSfpxSuffix());

        return suf;
    }

    public ArrayList<Boolean> getClust() {
        ArrayList<Boolean> clust = new ArrayList<>(getCapacity());
        for (int i = 0; i < mSufClustData.size(); i++)
            clust.add(mSufClustData.get(i).isClusterStart());

        return clust;
    }

    public ArrayList<BFTVertex> getChildVertices() {
        ArrayList<BFTVertex> childVertices = new ArrayList<>(getCapacity());
        for (int i = 0; i < mSufClustData.size(); i++)
            childVertices.add(mSufClustData.get(i).getChildVertex());

        return childVertices;
    }

    public static int getPrefSize() { return PREF_SIZE; }


    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        // print quer
        output.append("quer: " + mQuer + "\n");

        // print pref
        output.append("pref: " + mPref + "\n");

        // print suf
        output.append("suf: [");
        for (int i = 0; i < mSufClustData.size(); i++)
            output.append(mSufClustData.get(i).getSfpxSuffix() + " ");
        output.append("]\n");

        // print clust
        output.append("clust: [");
        for (int i = 0; i < mSufClustData.size(); i++)
            output.append(mSufClustData.get(i).isClusterStart() + " ");
        output.append("]\n");

        // print child vertices
//        output.append("child vertices:\n");
//        for (int i = 0; i < mSufClustData.size(); i++)
//            output.append(mSufClustData.get(i).getChildVertex() + " ");
//        output.append("\n");

        return output.toString();
    }
}
