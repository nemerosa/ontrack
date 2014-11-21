#!/usr/bin/python

import argparse
import re

import github
import ontrack
import utils


def get_release_name(branch):
    """Extracts the release name from the name of the branch"""
    matcher = re.match('release/(.*)', branch)
    if matcher is not None:
        return matcher.group(1)
    else:
        raise Exception('Can only release... releases.')


def prepare_environment(options):
    """Preparing the working environment"""
    print "[publish] Preparing environment"
    # We must assume being on the release branch already, in order to get access to this file
    # Gets the current branch
    options.branch = utils.run_command('git', ['rev-parse', '--abbrev-ref', 'HEAD']).strip()
    print "[publish] Current branch is %s" % options.branch
    # Checks the current branch
    options.release = get_release_name(options.branch)
    print "[publish] Release %s" % options.release


def merge_into_master(options):
    """Merging the branch into the master"""
    # Removes any extra change
    utils.run_command('git', ['checkout', '--', '.'])
    # Cleans the workspace
    utils.run_command('git', ['clean', '-df'])
    # Checking the master out
    print "[publish] Checks the master out"
    utils.run_command('git', ['checkout', 'master'])
    # Merging the release branch
    print "[publish] Merging branch %s" % options.branch
    utils.run_command('git',
                      ['merge', '--no-ff', "origin/%s" % options.branch, '--message', "Release %s" % options.release])


def build():
    """Building"""
    print "[publish] Building from tag"
    utils.run_command('./gradlew', ['clean', 'release'])


# Tagging and building
def tag_and_build(options):
    print "[publish] Preparing release %s" % options.release
    # Tagging
    print "[publish] Tagging into %s" % options.release
    utils.run_command('git', ['tag', options.release])
    # Building
    build()


# Deploying in acceptance
def acceptance_deploy(options):
    # TODO Deploying in acceptance
    print "[publish] Deploying %s in acceptance" % options.release


# Publication in GitHub
def github_publish(options):
    # Pushes the master and pushes the tag
    utils.run_command('git', ['push', 'origin', 'master'])
    utils.run_command('git', ['push', 'origin', options.release])
    # Creation of the release
    print "[publish] Creation of GitHub release %s" % options.release
    releaseid = github.createRelease(options, options.release)
    print "[publish] Release ID is %d" % releaseid
    # Attach artifacts to the release
    github.uploadGithubArtifact(options, releaseid, 'ontrack-ui.jar', 'application/zip',
                                "ontrack-ui/build/libs/ontrack-ui-%s.jar" % options.release)
    # Gets the change log since last release
    changeLog = ontrack.getChangeLog(options.ontrack_url, 'master', 'RELEASE')
    # Attach change log to the release
    github.setReleaseDescription(options, releaseid, changeLog)


def acceptance_local(options):
    print "[publish] Local acceptance tests..."
    utils.run_command('local-docker-acceptance.sh', [])

def publish(options):
    # Preparing the environment
    prepare_environment(options)
    # Merging into the master
    merge_into_master(options)
    # Tagging and building
    tag_and_build(options)
    # Runs acceptance locally and builds the Docker image
    acceptance_local(options)
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
    parser.add_argument('--github-repository', required=False, help='GitHub repository', default='nemerosa/ontrack')
    parser.add_argument('--ontrack-url', required=True, help='ontrack URL')
    parser.add_argument('--github-user', required=True, help='GitHub user used to publish the release')
    parser.add_argument('--github-token', required=True,
                        help='GitHub password or API token used to publish the release')
    # Parsing of arguments
    _options = parser.parse_args()
    # Calling the publication
    publish(_options)
