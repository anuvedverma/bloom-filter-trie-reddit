package edu.uchicago.mpcs56420;

/**
 * Created by Anuved on 11/25/2016.
 */
public abstract class Container {

    private static final int CAPACITY = 5;

    public static int getCapacity() {
        return CAPACITY;
    }

    public abstract boolean contains(Tuple tuple);

    public abstract boolean containsSuffix(Tuple tuple);

    public abstract void insert(Tuple newTuple) throws CapacityExceededException;

}
