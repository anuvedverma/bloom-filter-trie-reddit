# Table of Contents

1. [Overview](README.md#overview)
2. [Instructions](README.md#instructions)
3. [Input](README.md#input)

## Overview
The goal of this project was to implement and adapt a novel data structure, the Bloom Filter Trie, to compress and query Reddit comment data. The Bloom Filter Trie was originally designed for applications in Bioinformatics. This project aimed to extend it's applications to work with regular English text data.

As sequencing technology improves, genomic datasets are now larger than ever before. To extract valuable information from this data, it is crucial to be able to store and query it efficiently. This is especially true of pan-genomes, which are a redundant collection of similar sequencing reads, often extracted from members of the same species. Since most of the base pairs in these sequences are identical, pan-genomes are a prime candidate for compression. One method, proposed by Holley et al [1] uses a data structure called the Bloom Filter Trie (BFT) to compress, store, and query pan-genomes. The BFT essentially represents and compresses a Colored De Bruijn Graph (C-DBG), using Bloom Filters and Burst Tries to store k-mers from pan-genomic sequences.

This repository is my implementation of the Bloom Filter Trie, as described by Holley et al. (2016), adapted to store, compress, and query Reddit comment data.


## Instructions

* This project uses Maven to build and run
* To install the project, run `./install.sh`
* To run a basic benchmarking of BFT against HashMap and ArrayList, run `./run_benchmarks.sh`
* To run the BFT algorithm on all the queries stored in the `query` directory, run `./run_bft.sh`
* Datasets of varying sizes are stored in the `db` directory, and sample queries are stored in the `query` directory
    * To run the programs on a different dataset, update the BASH script by changing `./db/test-xsmall` and `./query/test-1` to your liking


### Input

The data provided in the `db` directory are Reddit comment data taken from the following link: https://www.reddit.com/r/datasets/comments/3bxlg7/i_have_every_publicly_available_reddit_comment/
* `db/test-med` is a 2.5MB dataset of Reddit comment data from January 1st, 2015
* `db/test-small` is a 170kB dataset of Reddit comment data from January 1st, 2015 (~300 comments)
* `db/test-xsmall` is a 6kB dataset of Reddit comment data from January 1st, 2015 (10 comments)
