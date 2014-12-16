Git
===

## Git configurations

From an administrative point of view, one can declare:

* `BasicGitConfiguration` - name, remote, user/password, links to the commits/files/etc, link to the issue management, indexation interval
* `GitHubConfiguration` - GitHub configuration: name, repository name, user/password/token, indexation interval

Both are `GitConfiguration`s.

From a structure point of view, one can associate a project:

* to a basic Git configuration
* to a GitHub configuration

Then, a branch can be associated to additional information:

* the linked Git branch
* the nature of the link between the builds and the commits
* the synchronisation between the builds and the commits, when applicable

## Git access

From a Git access point of view, when it's time to access a repository, we need the following information:

* remote
* user/password

This `GitRepository` can be computed from any `GitConfiguration`.

Indexation of repositories is based on the list of all the `GitRepository`, by collecting all projects and their
associated `GitConfiguration`.

Grouping the indexations per remote only is not enough because the way to access this repository might be different,
or the same remote and user are used for some basic and GitHub configurations.

Therefore, to differentiate two such repositories, we need additionally two other attributes:

* the `source`
* the `name` in this source

Together, the `source` and the `name` are enough to identify a `GitRepository`.

To validate is given indexed repository is still valid for its definition (remote + credentials), we need to compare
all the keys (source, name, remote, credentials).
