package edu.uchicago.mpcs53112.BloomFilterTrie;

/**
 * Created by Anuved on 11/25/2016.
 */
public abstract class Container {

	// all possible letters in input in order
	private static String ALPHABET = " abcdefghijklmnopqrstuvwxyz";

	// default container parameters
//	private static int CAPACITY = 248;
	private static int CAPACITY = 248;
	private static int PREFIX_LENGTH = 5;
//	private static int PRFX_PREFIX_LENGTH = 4;
	private static int PRFX_PREFIX_LENGTH = 2;
	private static int PRFX_SUFFIX_LENGTH = 1;

	/* Getters */
	public static int getCapacity() { return CAPACITY; }

	public static String getAlphabet() { return ALPHABET; }

	public static int getAlphabetSize() {
		return ALPHABET.length();
	}

	public static int getPrefixLength() { return PREFIX_LENGTH; }

	public static int getPrfxPrefixLength() {
		return PRFX_PREFIX_LENGTH;
	}

	public static int getPrfxSuffixLength() {
		return PRFX_SUFFIX_LENGTH;
	}

	/* Setters */
	public static void setAlphabet(String alphabet) { ALPHABET = alphabet; }

	public static void setCapacity(int capacity) { CAPACITY = capacity; }

	public static void setPrefixLength(int prefixLength) {
		PREFIX_LENGTH = prefixLength;
		PRFX_PREFIX_LENGTH = PREFIX_LENGTH / 2;
		PRFX_SUFFIX_LENGTH = PREFIX_LENGTH - PRFX_PREFIX_LENGTH;
	}


	/* Returns number of bits needed to represent Alphabet */
	public static int numBitsNeededForAlphabet() {
		int numBitsInInt = 32;
		return numBitsInInt - Integer.numberOfLeadingZeros(getAlphabetSize());
	}

	// let child classes implement this depending on if they're uncompressed or compressed
	public abstract void insert(Tuple newTuple) throws CapacityExceededException;

}
