#!/bin/bash

# Repository root
ROOT=`pwd`/repos
rm -rf $ROOT
echo Creating SVN repository at $ROOT
svnadmin create $ROOT

# Copying the configuration
cp -rf conf $ROOT

# Starting the repository
svnserve --daemon --root $ROOT --pid-file `pwd`/svn.pid
PID=`cat svn.pid`
echo SVN server started with PID $PID

