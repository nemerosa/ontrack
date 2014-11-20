#!/usr/bin/python

import argparse

import github
import ontrack


# Preparing the working environment
def prepare_environment(options):
    print "[publish] Preparing environment"
    # TODO Prepare local working directory
    # TODO Checks out the code
    # TODO Returns the prepared directory
    # options.dir = ...


# Merging the branch into the master
def merge_into_master(options):
    # TODO Checking the master out
    print "[publish] Checks the master out"
    # TODO Merging the release branch
    print "[publish] Merging branch %s" % (options.branch)


# Building
def build(options):
    # TODO Building
    print "[publish] Building from tag"


# Tagging and building
def tag_and_build(options):
    # TODO Gets the release from the branch
    options.release = '2.0-rc'
    # TODO Tagging
    print "[publish] Tagging into %s" % (options.release)
    # Building
    build(options)


# Deploying in acceptance
def acceptance_deploy(options):
    # TODO Deploying in acceptance
    print "[publish] Deploying %s in acceptance" % (options.release)


# Publication in GitHub
def github_publish(options):
    # TODO Pushes the tag
    # Creation of the release
    print "[publish] Creation of GitHub release %s" % (options.release)
    releaseid = github.createRelease(options, options.release)
    print "[publish] Release ID is %d" % releaseid
    # Attach artifacts to the release
    github.uploadGithubArtifact(options, releaseid, 'ontrack-ui.jar', 'application/zip',
                                "%s/ontrack-ui/build/libs/ontrack-ui-%s.jar" % (options.dir, options.release))
    # Gets the change log since last release
    changeLog = ontrack.getChangeLog(options.ontrack_url, 'master', 'RELEASE')
    # Attach change log to the release
    github.setReleaseDescription(options, releaseid, changeLog)


# Publication main method
def publish(options):
    # Preparing the environment
    prepare_environment(options)
    # Merging into the master
    merge_into_master(options)
    # Tagging and building
    tag_and_build(options)
    # Deploys in acceptance and run acceptance tests
    acceptance_deploy(options)
    # TODO acceptanceTest(options)
    # Publication in GitHub
    github_publish(options)
    # Deploys in production and run smoke tests
    # TODO productionDeploy(options)
    # TODO productionTest(options)
    # OK
    print "[publish] End."

# Entry point
if __name__ == '__main__':
    # Argument definitions
    parser = argparse.ArgumentParser(description='Ontrack publication')
    parser.add_argument('--branch', required=True, help='Release branch to release')
    parser.add_argument('--github-repository', required=False, help='GitHub repository', default='nemerosa/ontrack')
    parser.add_argument('--ontrack-url', required=True, help='ontrack URL')
    parser.add_argument('--github-user', required=True, help='GitHub user used to publish the release')
    parser.add_argument('--github-token', required=True,
                        help='GitHub password or API token used to publish the release')
    # Parsing of arguments
    options = parser.parse_args()
    # Calling the publication
    publish(options)
