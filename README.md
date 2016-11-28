TODO: 
- BFTVertex: write "insert" algorithm [DONE]
- BFTVertex: write "bursting" algorithm for uncompressed container [DONE]
- Container: Update signature for parent Container methods to allow "containsSuffix" to accept String as param [DONE]
- UncompressedContainer: fix to make sure inserts with same suffix (but different colors) do not throw Exception [DONE]
- CompressedContainer: refactor to put mSuf, mClust, and mChildren into a single class [DONE]
- CompressedContainer: expand functions to work on Tuples (aka finish implementing parent Container methods) [DONE]
- BFTVertex: update "insert" to allow color insertion [DONE]
- TEST [DONE]
- COMMIT [DONE]

- Test BFTVertex's "containsSuffix" [DONE]
- CompressedContainer: setup Bloom Filter [DONE]
- Test BFTVertex's "insert" with "mayContains" (3) (aka test Bloom Filter) [DONE]
- BFTVertex: create another "contains" function that checks if exact tuple (including color) already exists [DONE]
- 
- TEST [DONE]
- COMMIT [DONE]

- BloomFilterTrie: create this class []
- Write better tests w/ text input files []
- TEST []
- COMMIT []

- Clean & comment code
- Fix README