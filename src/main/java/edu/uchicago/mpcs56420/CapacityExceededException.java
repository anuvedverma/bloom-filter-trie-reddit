package edu.uchicago.mpcs56420;

/**
 * Created by Anuved on 11/25/2016.
 */
public class CapacityExceededException extends Exception {

	private Tuple mLastTuple;

	public CapacityExceededException() {}

	public CapacityExceededException(Tuple lastTuple) {
		mLastTuple = lastTuple;
	}

	public Tuple getLastTuple() {
		return mLastTuple;
	}
}