#!/usr/bin/env bash

java -javaagent:jamm-0.3.1.jar -cp ./target/BloomFilterTrie-1.0-SNAPSHOT-jar-with-dependencies.jar edu.uchicago.mpcs53112.BenchmarkDriver ./db/test-xsmall/ ./query/test-1