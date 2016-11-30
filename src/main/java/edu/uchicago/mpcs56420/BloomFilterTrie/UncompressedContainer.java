package edu.uchicago.mpcs56420.BloomFilterTrie;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Anuved on 11/25/2016.
 */
public class UncompressedContainer extends Container {

	private ArrayList<Tuple> mTuples;

	public UncompressedContainer() { mTuples = new ArrayList<>(); }

	public int size() { return mTuples.size(); }

	public ArrayList<Tuple> getTuples() { return new ArrayList<>(mTuples); }
//	public ArrayList<Tuple> getTuples() { return mTuples; }

    public int numColors() {
        int numColors = 0;
        for (Tuple tuple : mTuples)
            numColors += tuple.numColors();
        return numColors;
    }

	public boolean contains(Tuple tuple) {
		int index = Collections.binarySearch(mTuples, tuple);

		if(index < 0)
			return false;

		return mTuples.get(index).hasColors(tuple.getColors());
	}

	public boolean containsSequence(Tuple tuple) {
		if(Collections.binarySearch(mTuples, tuple) > -1)
			return true;

		return containsPartial(tuple.getSequence());
	}

	public Tuple getTuple(Tuple tuple) {
		int index = Collections.binarySearch(mTuples, tuple);
		if(index > -1)
			return mTuples.get(index);

		return null;
	}

    public boolean containsSequence(String suffix) {
        Tuple testTuple = new Tuple(suffix);
        return containsSequence(testTuple);
    }


    /* Inserts new tuple if not at capacity, else throws exception */
	@Override
	public void insert(Tuple newTuple) throws CapacityExceededException {

		if(!containsSequence(newTuple) && mTuples.size() == getCapacity())
			throw new CapacityExceededException(newTuple);

		int index = Collections.binarySearch(mTuples, newTuple);
		if(index > -1) // tuple already exists
			mTuples.get(index).addColors(newTuple.getColors());
		else { // tuple does not exist
			int insertionPoint = Math.abs(index + 1); // binarySearch returns (-(insertion point) - 1) if item is not found
			mTuples.add(insertionPoint, newTuple);
		}
	}

    private void insert(String newTupleString) throws CapacityExceededException {
        Tuple newTuple = new Tuple(newTupleString);
        insert(newTuple);
    }

	/* Checks to see if we can find the query within any part of any sequences in UC */
	private boolean containsPartial(String query) {
		for (int i = 0; i < mTuples.size(); i++) {
			if(mTuples.get(i).getSequence().startsWith(query))
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < mTuples.size(); i++)
			output.append(mTuples.get(i).toString() + "\n");

		return output.toString();
	}
}
