#!/usr/bin/python

import argparse

import github
import ontrack


def publish(options):
    # Creation of the release
    print "[publish] Creation of GitHub release %s" % options.release
    release_id = github.createRelease(options, options.release)
    print "[publish] Release ID is %d" % release_id
    # Attach artifacts to the release
    print "[publish] Uploading ontrack-ui.jar..."
    github.uploadGithubArtifact(options, release_id, 'ontrack-ui.jar', 'application/zip',
                                "%s/ontrack-ui-%s.jar" % (options.repository, options.release))
    # TODO #172 Gets the change log since last release
    # print "[publish] Getting the change log from Ontrack..."
    # change_log = ontrack.getChangeLog(options.ontrack_url, 'master', 'RELEASE')
    # TODO #172 Attach change log to the release
    # print "[publish] Setting the change log as description in the release..."
    # github.setReleaseDescription(options, release_id, change_log)
    # OK
    print "[publish] End."

# Entry point
if __name__ == '__main__':
    # Argument definitions
    parser = argparse.ArgumentParser(description='Ontrack publication')
    parser.add_argument('--release', required=True, help='Release to create')
    parser.add_argument('--repository', required=True, help='Directory that contains the artifacts')
    parser.add_argument('--ontrack-url', required=True, help='ontrack URL')
    parser.add_argument('--github-repository', required=False, help='GitHub repository', default='nemerosa/ontrack')
    parser.add_argument('--github-user', required=True, help='GitHub user used to publish the release')
    parser.add_argument('--github-token', required=True,
                        help='GitHub password or API token used to publish the release')
    # Parsing of arguments
    _options = parser.parse_args()
    # Calling the publication
    publish(_options)
