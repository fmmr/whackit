#!/usr/bin/env bash
mvn clean install
echo "upload to pi"
scp target/whackit-0.0.1-SNAPSHOT.jar pi@whackit:~/whackit/whackit.jar
echo "kill existing"
ssh pi@whackit 'sudo killall java'
echo "exec whackit.jar"
ssh pi@whackit 'java -jar whackit/whackit.jar >> whackit/whackit.log 2>&1 &'
open http://whackit:8080
echo "tail whackit.log"
ssh pi@whackit 'tail -F whackit/whackit.log'
