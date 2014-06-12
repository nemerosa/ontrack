#!/bin/sh
# Script to execute AFTER the release

# Gets the release
RELEASE=`cat .release`
echo Tagging for release ${RELEASE}

# Makes sure to get rid of all local changes
git reset --hard HEAD

# Tagging
git tag --force --message "v$RELEASE" "$RELEASE"

# Pushing
git push
git push --tags
