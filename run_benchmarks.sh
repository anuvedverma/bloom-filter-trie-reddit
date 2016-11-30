#!/usr/bin/env bash


#java -jar ./target/BloomFilterTrie-1.0-SNAPSHOT.jar edu.uchicago.mpcs56420.BenchmarkDriver ./db/test-med ./query/test-1
#java -jar ./target/BloomFilterTrie-1.0-SNAPSHOT.jar edu.uchicago.mpcs56420.BenchmarkDriver ./db/test-med ./query/test-1

java -javaagent:jamm-0.3.1.jar -cp ./target/BloomFilterTrie-1.0-SNAPSHOT.jar edu.uchicago.mpcs56420.BenchmarkDriver ./db/test-med ./query/test-1

## Save current directory
#DIR=$(pwd)
#
## UNRESOLVED SUN JAVAC BUG: ENUM CLASSES MUST BE COMPILED FIRST, OTHERWISE JAVAC THROWS INCORRECT ERRORS
#javac -classpath ./src/*.java
#
## Unable to find main class and pass in arguments with current directory structure
#cd ./src
#java PaymoDriver $DIR/paymo_input/batch_payment.txt $DIR/paymo_input/stream_payment.txt $DIR/paymo_output/output1.txt $DIR/paymo_output/output2.txt $DIR/paymo_output/output3.txt $DIR/paymo_output/output4.txt