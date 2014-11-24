#!/bin/bash

# The Release phase is applicable only for the release/* branches
# It prepares the master to be ready for actual release and publication

# Help function
function show_help {
	echo "Ontrack release preparation."
	echo ""
	echo "Available options are:"
	echo "    -h, --help                    Displays this help"
	echo "    -r, --repo=<url>              Repository to clone (default: 'git@github.com:nemerosa/ontrack.git')"
	echo "    -d, --dir=<dir>               Working directory (default: 'build')"
	echo "    -b, --branch=<branch>         Branch to merge (required)"
	echo "    -x, --accept                  If set, other branches than release/* are accepted"
	echo "    -np, --no-push                If set, no push is actually performed"
}

# Check function

function check {
	if [ "$1" == "" ]
	then
		echo $2
		exit 1
	fi
}

# Defaults

GIT_REPO="git@github.com:nemerosa/ontrack.git"
DIR="build"
BRANCH=
ACCEPT=no
PUSH=yes

# Command central

for i in "$@"
do
	case $i in
		-h|--help)
			show_help
			exit 0
			;;
		-r=*|--repo=*)
			GIT_REPO=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-d=*|--dir=*)
			DIR=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-b=*|--branch=*)
			BRANCH=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-x|--accept)
			ACCEPT=yes
			;;
		-np|--no-push)
			PUSH=no
			;;
		*)
			echo "Unknown option: $i"
			show_help
			exit 1
		;;
	esac
done

# Checking

check "${BRANCH}" "The branch to release from is required (--branch=release/*)"

# Logging

echo "Git repo to clone:    ${GIT_REPO}"
echo "Working directory:    ${DIR}"
echo "Branch:               ${BRANCH}"
echo "Accepting no release: ${ACCEPT}"
echo "Pushing:              ${PUSH}"

# Preparing the local environment

rm -rf ${DIR}

# Getting the release info

RELEASE_TYPE=`echo ${BRANCH} | sed -E 's/(.*)\/(.*)/\1/'`
RELEASE_NAME=`echo ${BRANCH} | sed -E 's/(.*)\/(.*)/\2/'`
echo "Release type:         ${RELEASE_TYPE}"
echo "Release name:         ${RELEASE_NAME}"

# Checks the type

if [ "${RELEASE_TYPE}" != "release" ]
then
	if [ "${ACCEPT}" == "no" ]
	then
		echo "Not a release - cannot prepare master"
		exit 1
	else
		echo "Not a release - going on all the same"
	fi
fi

# Cloning the repository

git clone ${GIT_REPO} ${DIR}
if [ "$?" != "0" ]
then
	echo "Could not clone repository"
	exit 1
fi

# Merging the branch

cd ${DIR}
git checkout master
git merge --no-ff --message "Release ${BRANCH}" "origin/${BRANCH}"
if [ "$?" != "0" ]
then
	echo "Could not merge ${BRANCH}"
	exit 1
fi


# Tagging

TAG="${RELEASE_NAME}"
git tag --force "${TAG}"

echo "The master branch has been prepared."

# Pushing

if [ "${PUSH}" == "yes" ]
then
	git push origin master
	git push origin "${TAG}"
	echo "The master branch has been pushed."
else
	echo "The master branch has NOT been pushed."
fi

# End

