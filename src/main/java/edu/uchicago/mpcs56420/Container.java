package edu.uchicago.mpcs56420;

/**
 * Created by Anuved on 11/25/2016.
 */
public abstract class Container {

	// container parameters
	private static final int CAPACITY = 5;
	private static final String ALPHABET = "acgt"; //{'a', 'c', 'g', 't'};
	private static final int ALPHABET_SIZE = 4;
	private static final int SFPX_SIZE = 4;
	private static final int SFPX_PREFIX_SIZE = 2;
	private static final int SFPX_SUFFIX_SIZE = 2;

	public static int getCapacity() {
		return CAPACITY;
	}

	public static String getAlphabet() {
		return ALPHABET;
	}

	public static int getAlphabetSize() {
		return ALPHABET_SIZE;
	}

	public static int getSfpxSize() {
		return SFPX_SIZE;
	}

	public static int getSfpxPrefixSize() {
		return SFPX_PREFIX_SIZE;
	}

	public static int getSfpxSuffixSize() {
		return SFPX_SUFFIX_SIZE;
	}

	/* Returns number of bits needed to represent Alphabet */
	public static int numBitsNeededForAlphabet() {
		int numBitsInInt = 32;
		return numBitsInInt - Integer.numberOfLeadingZeros(ALPHABET_SIZE);
	}

	public abstract boolean contains(Tuple tuple);

	public abstract boolean containsSuffix(Tuple tuple);

	public abstract void insert(Tuple newTuple) throws CapacityExceededException;

}
