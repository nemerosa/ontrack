#!/bin/bash

# Stopping the repository
PID=`cat svn.pid`
kill -TERM $PID
echo Stopping SVN server with PID $PID

