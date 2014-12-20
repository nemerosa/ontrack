#!/usr/bin/python

import argparse

import github
from utils import run_command


def maven_publish(options):
    staging_dir = "%s/staging/%s" % (options.repository, options.version_full)
    # Deploys each module separately
    modules = ["common", "json", "client", "dsl"]
    for module in modules:
        # Update of POM to take into account the version
        run_command("sed", [
            "-i",
            "s/%s/%s/g" % (options.version_full, options.version_release),
            "%s/ontrack-%s-%s.pom" % (options.repository, module, options.version_full)
        ])
        # Deployment repository
        run_command("mkdir", ["-p", staging_dir])
        # Staging publication
        run_command("mvn", [
            "gpg:sign-and-deploy-file",
            "-Durl=file://%s" % staging_dir,
            "-DpomFile=%s/ontrack-%s-%s.pom" % (options.repository, module, options.version_full),
            "-Dfile=%s/ontrack-%s-%s.jar" % (options.repository, module, options.version_full)
        ])
    # Publication to OSSRH
    run_command("mvn", [
        "org.sonatype.plugins:nexus-staging-maven-plugin:1.6.5:deploy-staged-repository",
        "-DrepositoryDirectory=%s" % staging_dir,
        "-DnexusUrl=https://oss.sonatype.org",
        "-DserverId=ossrh",
        "-DautoReleaseAfterClose=true",
        "-DstagingProfileId=%s" % options.ossrh_profile
    ])


def publish(options):
    # Creation of the release
    print "[publish] Creation of GitHub release %s" % options.version_release
    release_id = github.create_release(options, options.version_commit, options.version_release)
    print "[publish] Release ID is %d" % release_id
    # Attach artifacts to the release
    ui_jar = "%s/ontrack-ui-%s.jar" % (options.repository, options.version_full)
    print "[publish] Uploading ontrack-ui.jar at %s..." % ui_jar
    github.upload_github_artifact(options, release_id, 'ontrack-ui.jar', 'application/zip', ui_jar)
    # Publication in the Maven Central repository
    print "[publish] Publishing DSL libraries in OSSRH..."
    maven_publish(options)
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
    parser.add_argument('--version-commit', required=True, help='Commit to release')
    parser.add_argument('--version-full', required=True, help='Version to release')
    parser.add_argument('--version-release', required=True, help='Release to create')
    parser.add_argument('--repository', required=True, help='Directory that contains the artifacts')
    parser.add_argument('--ossrh-profile', required=True, help='ID of the staging profile in OSSRH')
    parser.add_argument('--ontrack-url', required=True, help='ontrack URL')
    parser.add_argument('--github-repository', required=False, help='GitHub repository', default='nemerosa/ontrack')
    parser.add_argument('--github-user', required=True, help='GitHub user used to publish the release')
    parser.add_argument('--github-token', required=True,
                        help='GitHub password or API token used to publish the release')
    # Parsing of arguments
    _options = parser.parse_args()
    # Calling the publication
    publish(_options)
