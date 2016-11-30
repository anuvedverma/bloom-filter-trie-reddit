# Table of Contents

1. [Overview] (README.md#overview)
2. [Instructions] (README.md#instructions)
3. [Input] (README.md#input)

##Overview
As sequencing technology improves, our genomic datasets are now larger than ever before. To extract valuable information from this data, it is crucial to be able to store and query it efficiently. This is especially true of pan-genomes, which are a redundant collection of similar sequencing reads, often extracted from members of the same species. Since most of the base pairs in these sequences are identical, pan-genomes are a prime candidate for compression. One method, proposed by Holley et al [1] uses a data structure called the Bloom Filter Trie (BFT) to compress, store, and query pan-genomes. The BFT essentially represents and compresses a Colored De Bruijn Graph (C-DBG), using Bloom Filters and Burst Tries to store k-mers from pan-genomic sequences.

This repository is my implementation of the Bloom Filter Trie, as described by Holley et al 2016.


##Instructions

### UPDATE: Command-line Maven not working for some reason... If instructions fail, please open and run from an IDE (IntelliJ or Eclipse) as a Maven project! 

* This project uses Maven to build and run
* To install the project, run `./install.sh`
* To run a basic benchmarking of BFT, run `./run_benchmarks.sh`
* To run the BFT algorithm on all the queries stored in the `query` directory, run `./run_benchmarks.sh`
* Datasets of varying sizes are stored in the `db` directory, and sample queries are stored in the `query` directory
    * To run the programs on a different dataset, update the BASH script by changing `./db/test-mid` to your liking


###Input

The data provided in the `db` directory are sequences taken from various strains of _E. coli_.
* `db/test-xlarge` are the full genomes of 10 different _E. coli_ strains (warning: I have not completed a test on this yet)
* `db/test-large` are the first ~6-10kb base-pairs from the genomes of 10 different _E. coli_ strains
* `db/test-med` are the first ~300-500 base-pairs from the genomes of 10 different _E. coli_ strains
* `db/test-small` are the first ~100 base-pairs from the genomes of 10 different _E. coli_ strains
* `db/test-xsmall` are the 12 base-pair sequences shown in the figures from Holley et al. 2016