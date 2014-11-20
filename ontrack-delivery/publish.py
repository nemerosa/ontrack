#!/usr/bin/python

import argparse
import json

import github
import ontrack

# Preparing the working environment

def prepareEnvironment(options):
    print "[publish] Preparing environment"
    # TODO Prepare local working directory
    # TODO Checks out the code
    # TODO Returns the prepared directory
    # options.dir = ...


# Merging the branch into the master

def mergeIntoMaster(options):
    # TODO Checking the master out
    print "[publish] Checks the master out"
    # TODO Merging the release branch
    print "[publish] Merging branch %s" % (options.branch)


# Building

def build(options):
    # TODO Building
    print "[publish] Building from tag"


# Tagging and building

def tagAndBuild(options):
    # TODO Gets the release from the branch
    options.release = '2.0-rc'
    # TODO Tagging
    print "[publish] Tagging into %s" % (options.release)
    # Building
    build(options)


# Deploying in acceptance

def acceptanceDeploy(options):
    # TODO Deploying in acceptance
    print "[publish] Deploying %s in acceptance" % (options.release)


# Publication in GitHub

def githubPublish(options):
    # TODO Pushes the tag
    # Creation of the release
    print "[publish] Creation of GitHub release %s" % (options.release)
    response = github.callGithub(
        options,
        "https://api.github.com/repos/%s/releases" % options.github_repository,
        {
            'tag_name': options.release,
            'name': options.release
        }
    )
    releaseId = json.load(response)['id']
    print "[publish] Release ID is %d" % releaseId
    # Attach artifacts to the release
    github.uploadGithubArtifact(options, releaseId, 'ontrack-ui.jar', 'application/zip',
                                "%s/ontrack-ui/build/libs/ontrack-ui-%s.jar" % (options.dir, options.release))
    # Gets the change log since last release
    changeLog = ontrack.getChangeLog(options.ontrack_url, 'master', 'RELEASE')
    # TODO Attach change log to the release


# Publication main method

def publish(options):
    # Preparing the environment
    prepareEnvironment(options)
    # Merging into the master
    mergeIntoMaster(options)
    # Tagging and building
    tagAndBuild(options)
    # Deploys in acceptance and run acceptance tests
    acceptanceDeploy(options)
    # TODO acceptanceTest(options)
    # Publication in GitHub
    githubPublish(options)
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
