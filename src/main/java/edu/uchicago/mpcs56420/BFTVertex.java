package edu.uchicago.mpcs56420;

import java.util.ArrayList;

/**
 * Created by Anuved on 11/26/2016.
 */
public class BFTVertex {

    static int NUM_VERTICES = 0;

	private ArrayList<CompressedContainer> mCompressedContainers;
	private UncompressedContainer mUncompressedContainer;

	public BFTVertex() {
		mCompressedContainers = new ArrayList<>();
		mUncompressedContainer = new UncompressedContainer();
        NUM_VERTICES++;
	}

	/* Getters */
	public ArrayList<Container> getContainers() {
		ArrayList<Container> containers = new ArrayList<>();
		containers.addAll(mCompressedContainers);
		containers.add(mUncompressedContainer);
		return containers;
	}

	public UncompressedContainer getUncompressedContainer() { return mUncompressedContainer; }

	public ArrayList<CompressedContainer> getCompressedContainers() { return mCompressedContainers; }

	/* Algorithm to determine presence of suffix in a vertex and its children */
	public boolean containsSuffix(BFTVertex vertex, Tuple tuple) {

		// get tuple prefix
        int sfpxLength = Container.getSfpxLength();
        String sfpx = tuple.getSfxPrefix(sfpxLength);

        // get vertex's containers
		UncompressedContainer uncompressedContainer = vertex.getUncompressedContainer();
		ArrayList<CompressedContainer> compressedContainers = vertex.getCompressedContainers();

		// if exists in uncompressed container, we're done
		if(uncompressedContainer.containsSuffix(tuple))
			return true;

		// recursively search through vertex's (and its children's) compressed containers
		for (CompressedContainer cont : compressedContainers) {
			if(cont.mayContain(sfpx)) {
				if(cont.containsPrefix(sfpx)) {
                    sfpx = tuple.emitSfxPrefix(sfpxLength);
					BFTVertex childVertex = cont.getChildOf(sfpx);
					return containsSuffix(childVertex, tuple);
				}
			} else return false;
		}

		return false;
	}

    public void insert(Tuple newTuple) {
        insert(this, newTuple);
    }

	/* Algorithm to insert a Tuple into a BFTVertex:
	 * 1) if vertex containsPrefix tuple in UC: update color
     * 2) if vertex containsPrefix prefix in CC: insert truncated tuple into child
     *    ... (above happens recursively... at this point, no UC containsPrefix tuple AND no CC containsPrefix prefix)
	 * 3) if vertex mayContain(prefix) in CC:
     *        - if CC at capacity, repeat (3) ie. keep iterating through CCs
     *        - else add prefix to CC; insert truncated tuple into child
     *    ... (at this point, mayContain(prefix) is false for all CCs)
     * 4) add to UC
	 * */
	private void insert(BFTVertex vertex, Tuple newTuple) {

        // get tuple prefix
        int sfpxLength = Container.getSfpxLength();
        String sfpx = newTuple.getSfxPrefix(sfpxLength);

        // get vertex's containers
		UncompressedContainer uncompressedContainer = vertex.getUncompressedContainer();
		ArrayList<CompressedContainer> compressedContainers = vertex.getCompressedContainers();

        // 1) if vertex contains tuple in UC: update color
		if(uncompressedContainer.containsSuffix(newTuple))
			try {
				uncompressedContainer.insert(newTuple);
				return;
			} catch (CapacityExceededException e) {
                // WE SHOULD NEVER REACH THIS PART OF CODE:
                // inserting into UncompressedContainer with existing suffix should not increase Container size
                System.out.println("ERROR INSERTING TUPLE INTO UNCOMPRESSED CONTAINER: " + e.getLastTuple());
                e.printStackTrace();
			}

		// 2) if vertex containsPrefix prefix in CC: insert truncated tuple into child
		for (CompressedContainer cont : compressedContainers) {
			if(cont.containsPrefix(sfpx)) {
                sfpx = newTuple.emitSfxPrefix(sfpxLength);
                BFTVertex childVertex = cont.getChildOf(sfpx);
                insert(childVertex, newTuple);
                return;
			}
		}

        // 3) if vertex mayContain(prefix) in CC:
        //      - if CC at capacity, repeat (3) ie. keep iterating through CCs
        //      - else add prefix to CC; insert truncated tuple into child
        for (CompressedContainer cont : compressedContainers) {
            if(cont.mayContain(sfpx)) {
                sfpx = newTuple.emitSfxPrefix(sfpxLength);
                try {
                    cont.insert(sfpx);
                    BFTVertex childVertex = cont.getChildOf(sfpx);
                    insert(childVertex, newTuple);
                    return;
                } catch (CapacityExceededException e) { continue; }
            }
        }

        // 4) add to UC (with bursting if needed)
        try { uncompressedContainer.insert(newTuple); }
        catch (CapacityExceededException e) { burstUncompressedContainer(e.getLastTuple()); }

    }

    /* Algorithm for bursting an UncompressedContainer */
    public void burstUncompressedContainer(Tuple newTuple) {

        ArrayList<Tuple> tuples = mUncompressedContainer.getTuples();
        tuples.add(newTuple);

        UncompressedContainer newUncompressedContainer = new UncompressedContainer();
        CompressedContainer newCompressedContainer = new CompressedContainer();
        for (int i = 0; i < tuples.size(); i++) {
            try {
                Tuple suffixTuple = tuples.get(i);
                String sfpx = suffixTuple.emitSfxPrefix(Container.getSfpxLength());
                newCompressedContainer.insert(sfpx);
                newCompressedContainer.getChildOf(sfpx).insert(suffixTuple);
//                newCompressedContainer.insert(tuples.get(i));

                // TODO: set bloom filter here?
            } catch (CapacityExceededException e) {
                try { newUncompressedContainer.insert(e.getLastTuple()); }
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
        output.append("Num. Compressed Containers: " + mCompressedContainers.size() + "\n");
        output.append("Uncompressed Container Size: " + mUncompressedContainer.size() + "\n");

        return output.toString();
    }
}
