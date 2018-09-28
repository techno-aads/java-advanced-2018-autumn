#!/usr/bin/env bash
#java -jar lib/checkstyle-8.12-all.jar -c conf/checkstyle.xml src/ru/ifmo/task1 &&
mkdir -p build &&
javac -cp src src/ru/ifmo/task1/Walker.java -d build &&
java -cp build ru.ifmo.task1.Walker $@

