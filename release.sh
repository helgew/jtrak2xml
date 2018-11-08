#!/bin/sh
mvn clean release:clean release:prepare
git commit -m'release README.md' README.md
git push origin master
mvn release:clean
