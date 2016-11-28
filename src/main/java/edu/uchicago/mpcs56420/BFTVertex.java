package edu.uchicago.mpcs56420;

import java.util.ArrayList;
import java.util.BitSet;

/**
 * Created by Anuved on 11/26/2016.
 */
public class BFTVertex {

	private ArrayList<CompressedContainer> mCompressedContainers;
	private UncompressedContainer mUncompressedContainer;
    private BitSet mTerminalColors;

	public BFTVertex() {
		mCompressedContainers = new ArrayList<>();
		mUncompressedContainer = new UncompressedContainer();
        mTerminalColors = new BitSet();
	}

	/* Getters */
	public ArrayList<Container> getContainers() {
		ArrayList<Container> containers = new ArrayList<>();
		containers.addAll(mCompressedContainers);
		containers.add(mUncompressedContainer);
		return containers;
	}

    public BitSet getTerminalColors() { return mTerminalColors; }

    public int numTerminalColors() { return mTerminalColors.cardinality(); }

    public UncompressedContainer getUncompressedContainer() { return mUncompressedContainer; }

	public ArrayList<CompressedContainer> getCompressedContainers() { return mCompressedContainers; }


    /* Public interface for inserting a tuple into the vertex */
    public void insert(Tuple newTuple) {
        Tuple tuple = new Tuple(newTuple);
        insert(this, tuple);
    }

    /* Public interface for checking if the vertex contains a tuple (including color) */
    public boolean contains(Tuple checkTuple) {
        Tuple tuple = new Tuple(checkTuple);
        return contains(this, tuple);
    }

    /* Public interface for checking if Vertex contains a suffix */
    public boolean containsSuffix(Tuple checkTuple) {
        Tuple tuple = new Tuple(checkTuple);
        return containsSuffix(this, tuple);
    }


    /* Algorithm to insert a tuple into a BFTVertex */
    private void insert(BFTVertex vertex, Tuple newTuple) {

        if(newTuple.getSequence().isEmpty()) {
            mTerminalColors.or(newTuple.getColors());
            return;
        }
        // get tuple prefix
        int sfpxLength = Container.getSfpxLength();
        String sfpx = newTuple.getPrefix(sfpxLength);

        // get vertex's containers
        UncompressedContainer uncompressedContainer = vertex.getUncompressedContainer();
        ArrayList<CompressedContainer> compressedContainers = vertex.getCompressedContainers();

        // if vertex contains tuple in uncompressed container: update color
        if(uncompressedContainer.containsSequence(newTuple)) {
            try {
                uncompressedContainer.insert(newTuple); // will simply update color
                return;
            } catch (CapacityExceededException e) {
                // WE SHOULD NEVER REACH THIS PART OF CODE:
                // inserting into UncompressedContainer with existing suffix should not increase Container size
                System.out.println("ERROR INSERTING TUPLE INTO UNCOMPRESSED CONTAINER: " + e.getLastTuple());
                e.printStackTrace();
            }
        }

        // if vertex already contains prefix in compressed container: insert truncated tuple suffix into child
        for (CompressedContainer cont : compressedContainers) {
            if(cont.mayContain(sfpx) && cont.containsPrefix(sfpx)) {
                sfpx = newTuple.emitPrefix(sfpxLength);
                BFTVertex childVertex = cont.getChildOf(sfpx);
                insert(childVertex, newTuple);
                return;
            }
        }

        // 3) if vertex mayContain(prefix) in compressed container (aka. if prefix is a false positive, because of previous step):
        //      - if CC at capacity, repeat (3) (ie. keep iterating through CCs)
        //      - else add prefix to CC; insert truncated tuple into child
        for (CompressedContainer cont : compressedContainers) {
            if(cont.mayContain(sfpx)) {
                sfpx = newTuple.emitPrefix(sfpxLength);
                try {
                    cont.insert(new Tuple(sfpx));
                    BFTVertex childVertex = cont.getChildOf(sfpx);
                    insert(childVertex, newTuple);
                    return;
                } catch (CapacityExceededException e) { continue; }
            }
        }

        // 4) if none of the above: add to uncompressed container (burst if necessary)
        try { uncompressedContainer.insert(newTuple); }
        catch (CapacityExceededException e) { burstUncompressedContainer(newTuple); }

    }


    /* Algorithm to determine presence of suffix in a vertex and its children */
    private boolean contains(BFTVertex vertex, Tuple checkTuple) {

        // get vertex's containers
        UncompressedContainer uncompressedContainer = vertex.getUncompressedContainer();
        ArrayList<CompressedContainer> compressedContainers = vertex.getCompressedContainers();

        // if exists in uncompressed container, we're done
        if(uncompressedContainer.contains(checkTuple))
            return true;

        // get tuple prefix
        int sfpxLength = Container.getSfpxLength();
        String sfpx = checkTuple.getPrefix(sfpxLength);

        // recursively search through vertex's (and its children's) compressed containers
        for (CompressedContainer cont : compressedContainers) {
            if(cont.mayContain(sfpx) && cont.containsPrefix(sfpx)) {
                sfpx = checkTuple.emitPrefix(sfpxLength);
                BFTVertex childVertex = cont.getChildOf(sfpx);
                return contains(childVertex, checkTuple);
            } else return false;
        }

        return false;
    }

    /* Algorithm to determine presence of suffix in a vertex and its children */
	private boolean containsSuffix(BFTVertex vertex, Tuple checkTuple) {

        // get vertex's containers
		UncompressedContainer uncompressedContainer = vertex.getUncompressedContainer();
		ArrayList<CompressedContainer> compressedContainers = vertex.getCompressedContainers();

		// if exists in uncompressed container, we're done
		if(uncompressedContainer.containsSequence(checkTuple))
			return true;


        // get tuple prefix
        int sfpxLength = Container.getSfpxLength();
        String sfpx = checkTuple.getPrefix(sfpxLength);

        // recursively search through vertex's (and its children's) compressed containers
		for (CompressedContainer cont : compressedContainers) {
			if(cont.mayContain(sfpx) && cont.containsPrefix(sfpx)) {
                sfpx = checkTuple.emitPrefix(sfpxLength);
                BFTVertex childVertex = cont.getChildOf(sfpx);
                return containsSuffix(childVertex, checkTuple);
			} else return false;
		}

		return false;
	}


    /* Algorithm for bursting an UncompressedContainer upon addition of newTuple */
    private void burstUncompressedContainer(Tuple newTuple) {

        ArrayList<Tuple> tuples = mUncompressedContainer.getTuples();
        tuples.add(newTuple);

        UncompressedContainer newUncompressedContainer = new UncompressedContainer();
        CompressedContainer newCompressedContainer = new CompressedContainer();
        for (int i = 0; i < tuples.size(); i++) {
            Tuple uncompressedTuple = tuples.get(i);
            try {
                String sfpx = uncompressedTuple.emitPrefix(Container.getSfpxLength());
                newCompressedContainer.insert(new Tuple(sfpx));
                newCompressedContainer.getChildOf(sfpx).insert(uncompressedTuple);
            } catch (CapacityExceededException e) {
                try { newUncompressedContainer.insert(uncompressedTuple); }
                catch (CapacityExceededException e1) {
                    // WE SHOULD NEVER REACH THIS PART OF CODE:
                    // there shouldn't be enough tuples leftover after inserting into CompressedContainer to overflow the UncompressedContainer
                    System.out.println("ERROR INSERTING TUPLE INTO UNCOMPRESSED CONTAINER: " + e1.getLastTuple());
                    e1.printStackTrace();
                }
            }
        }

        mUncompressedContainer = newUncompressedContainer;
        mCompressedContainers.add(newCompressedContainer);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        output.append("BFTVertex: " + super.toString() + "\n");
        output.append("Terminal Colors: " + mTerminalColors + "\n");
        output.append("Num. Compressed Containers: " + mCompressedContainers.size() + "\n");
        output.append("Uncompressed Container Size: " + mUncompressedContainer.size() + "\n");

        return output.toString();
    }
}
