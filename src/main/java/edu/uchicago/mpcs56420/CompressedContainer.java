package edu.uchicago.mpcs56420;

/**
 * Created by Anuved on 11/25/2016.
 */
public class CompressedContainer extends Container {



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
}
