#!/usr/bin/python

import argparse
import os

import github
import ontrack
import utils


def prepare_environment(options):
    """Preparing the working environment"""
    print "[publish] Preparing environment"
    # Prepare local working directory
    options.dir = os.path.join(os.getcwd(), 'release')
    print "[publish] Local environment at %s" % (os.path.abspath(options.dir))
    if os.path.exists(options.dir):
        os.rmdir(options.dir)
    os.mkdir(options.dir)
    # Checks out the code
    os.chdir(options.dir)
    utils.run_command('git', ['clone', "git@github.com:%s.git" % options.github_repository, options.dir])


def merge_into_master(options):
    """Merging the branch into the master"""
    # Checking the master out
    print "[publish] Checks the master out"
    utils.run_command('git', ['checkout', 'master'])
    # Merging the release branch
    print "[publish] Merging branch %s" % options.branch
    utils.run_command('git', ['merge', '--no-ff', options.branch])


def build():
    """Building"""
    print "[publish] Building from tag"
    utils.run_command('gradlew', ['clean', 'release'])


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
                                "%s/ontrack-ui/build/libs/ontrack-ui-%s.jar" % (options.dir, options.release))
    # Gets the change log since last release
    changeLog = ontrack.getChangeLog(options.ontrack_url, 'master', 'RELEASE')
    # Attach change log to the release
    github.setReleaseDescription(options, releaseid, changeLog)


def get_release_name(branch):
    """Extracts the release name from the name of the branch"""
    # TODO get_release_name
    # TODO Checks this is actually a release branch
    return '2.0-rc'


# Publication main method
def publish(options):
    # Gets the release from the branch
    options.release = get_release_name(options.branch)
    print "[publish] Release %s" % options.release
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
    _options = parser.parse_args()
    # Calling the publication
    publish(_options)
