#!/bin/bash

port=5051
jar=$(realpath $(find . -name '*jar-with-dependencies*'))

echo $jar
cd target/test-classes
$JAVA_HOME/bin/java -javaagent:$jar=org.viapivov.exposer.server.ResponsiveClass@$port -Dexposer.parallelism=4 -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8751 org.viapivov.exposer.server.TestServer  &
PID=$!
sleep 3

id=1
expected_response="{\"jsonrpc\":\"2.0\",\"result\":\"SUCCESS-$id\",\"id\":$id}"
response=$(echo "{\"jsonrpc\":\"2.0\", \"method\":\"test$id\", \"id\":$id, \"params\":{}}" | nc -q 2 localhost $port)
sleep 1
if [ $response != $expected_response ]; then
    echo "Expected $expected_response but got $response"
    exit $id
fi

id=2
expected_response="{\"jsonrpc\":\"2.0\",\"result\":\"SUCCESS-$id\",\"id\":$id}"
response=$(echo "{\"jsonrpc\":\"2.0\", \"method\":\"test$id\", \"id\":$id, \"params\":\"test string\"}" | nc -q 2 localhost $port)
sleep 1
if [ $response != $expected_response ]; then
    echo "Expected $expected_response but got $response"
    exit $id
fi

id=3
expected_response="{\"jsonrpc\":\"2.0\",\"result\":\"SUCCESS-$id\",\"id\":$id}"
response=$(echo "{\"jsonrpc\":\"2.0\", \"method\":\"test$id\", \"id\":$id, \"params\":{\"wrapee\":\"test string\"}}" | nc -q 2 localhost $port)
sleep 1
if [ $response != $expected_response ]; then
    echo "Expected $expected_response but got $response"
    exit $id
fi

id=4
expected_response="{\"jsonrpc\":\"2.0\",\"result\":\"SUCCESS-$id\",\"id\":$id}"
response=$(echo "{\"jsonrpc\":\"2.0\", \"method\":\"test$id\", \"id\":$id, \"params\":[{\"wrapee\":\"test string\"}, \"string test\"]}" | nc -q 2 localhost $port)
sleep 1
if [ $response != $expected_response ]; then
    echo "Expected $expected_response but got $response"
    exit $id
fi

id=5
expected_response="{\"jsonrpc\":\"2.0\",\"result\":{\"wrapee\":\"SUCCESS-$id\"},\"id\":$id}"
response=$(echo "{\"jsonrpc\":\"2.0\", \"method\":\"test$id\", \"id\":$id, \"params\":{}}" | nc -q 2 localhost $port)
sleep 1
if [ $response != $expected_response ]; then
    echo "Expected $expected_response but got $response"
    exit $id
fi

id=6
expected_response="{\"jsonrpc\":\"2.0\",\"result\":\"SUCCESS-$id\",\"id\":$id}"
response=$(echo "{\"jsonrpc\":\"2.0\", \"method\":\"test$id\", \"id\":$id, \"params\":42}" | nc -q 2 localhost $port)
sleep 1
if [ $response != $expected_response ]; then
    echo "Expected $expected_response but got $response"
    exit $id
fi