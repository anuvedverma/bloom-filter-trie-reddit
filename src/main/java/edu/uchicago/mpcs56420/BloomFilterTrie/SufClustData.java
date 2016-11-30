package edu.uchicago.mpcs56420.BloomFilterTrie;

/**
 * Created by Anuved on 11/26/2016.
 */
public class SufClustData {

    private String mSfpxSuffix;
    private boolean mStartsCluster;
    private BFTVertex mChildVertex;

    public SufClustData(String sfpxSuffix, Boolean startsCluster) {
        mSfpxSuffix = sfpxSuffix;
        mStartsCluster = startsCluster;
        mChildVertex = new BFTVertex();
    }

    /* Getters */
    public String getSfpxSuffix() {
        return mSfpxSuffix;
    }

    public boolean isClusterStart() {
        return mStartsCluster;
    }

    public BFTVertex getChildVertex() {
        return mChildVertex;
    }


    /* Setters */
    public void setClusterStart(Boolean startsCluster) {
        mStartsCluster = startsCluster;
    }
}
