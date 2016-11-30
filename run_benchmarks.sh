#!/usr/bin/env bash

java -javaagent:jamm-0.3.1.jar -cp ./target/BloomFilterTrie-1.0-SNAPSHOT-jar-with-dependencies.jar edu.uchicago.mpcs56420.BenchmarkDriver ./db/test-med ./query/test-1