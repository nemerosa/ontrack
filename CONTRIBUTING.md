Contribution Guidelines
=======================

# Before you start

Before starting to work on a feature or a bug fix, please open an issue to discuss the use case or bug with us, or post
a comment in the relevant issue.

A new issue does not imply any commitment by the developers of Ontrack. But at least, the discussion can start.

## Security vulnerabilities

Do not report security vulnerabilities publicly. Please contact `ontrack@nemerosa.com`.

## Follow the Code of Conduct

Contributors must follow the [Code of Conduct](CODE_OF_CONDUCT.md).

## Slack

There a Community Slack for Ontrack. Please contact `ontrack@nemerosa.com` to get access to it.

# Developing Ontrack

See the [development guide](DEVELOPMENT.md) to get started with your development environment for Ontrack.

# Making a change

## Code change guidelines

All code contributions should contain the following:

* unit tests
* integration tests
* documentation (under [ontrack-docs](ontrack-docs)) when applicable

## Contributing to documentation

This repository includes the Ontrack documentation under [ontrack-docs](ontrack-docs) as Asciidoc documents.

You can generate the documentation by running

```bash
./gradlew -Pdocumentation :ontrack-docs:build
```

## Commit messages

The commit messages that accompany your code changes are an important piece of documentation. Please follow these
guidelines when creating commits:

* [write good commit messages](https://cbea.ms/git-commit/#seven-rules)
* [sign off](https://git-scm.com/docs/git-commit#Documentation/git-commit.txt---signoff) your commits to indicate that
  you agree to the terms of Developer Certificate of Origin. We can only accept PRs that have all commits signed off.

The Ontrack developers reserve their right to accept or deny commits based on their quality.

## Contributor License Agreement

Before we can merge your contributions, you must sign our Contributor License Agreement (CLA).

1. open a Pull Request
2. once you open the PR, our CLA bot will check if you have signed the CLA
3. if you haven't signed yet, it will post a comment with a link to sign the agreement
4. click the link, read the terms, and sign electronically (via GitHub login)
5. once signed, the bot will confirm, and we can merge your PR.

You can read the full text of the CLA [here](CLA.md).

### Why do we need this?

This helps protect you and us. The CLA ensures that the community is free to use your contributions under the terms of
the project’s license.
