#!/bin/bash
# Fills a repository with commits

# Working copy
WC=./wc
echo Creating working copy at $WC
rm -rf $WC
svn checkout svn://localhost $WC --username admin --password test

# Variables
TRUNK=$WC/project/trunk

# Folders
svn mkdir $WC/project/trunk --parents
svn mkdir $WC/project/tags --parents
svn mkdir $WC/project/branches --parents
svn commit $WC -m "Project structure" --username admin --password test

# Initial commits by the user
echo apply plugin: 'java' >> $TRUNK/build.gradle
echo group = 'net.ontrack.test.svn' >> $TRUNK/build.gradle
svn mkdir $TRUNK/src/main/resources --parents
echo log4j.rootLogger=warn, Console >> $TRUNK/src/main/resources/log4j.properties
svn commit $WC -m "PRJ-1 Project creation" --username user --password test

# Other commits
svn mkdir $TRUNK/doc
echo First line of documentation >> $TRUNK/doc/README.md
svn commit $WC -m "PRJ-1 Project documentation" --username user --password test

