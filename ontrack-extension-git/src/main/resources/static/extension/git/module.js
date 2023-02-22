const gitCommitInfoFragments = `
    fragment gitCommitInfoFields on OntrackGitCommitInfo {
      uiCommit {
        link
        commit {
          id
          author {
            name
          }
          commitTime
        }
        fullAnnotatedMessage
      }
      branchInfosList {
        type
        branchInfoList {
          branch {
            id
            name
            disabled
            links {
              _page
            }
          }
          firstBuild {
            ...buildFields
          }
          promotions {
            creation {
              time
            }
            promotionLevel {
              id
              name
              description
              image
              _image
              links {
                _page
              }
            }
            build {
              ...buildFields
            }
          }
        }
      }
    }
    
    fragment buildFields on Build {
      id
      name
      creation {
        time
      }
      links {
        _page
      }
      decorations {
        decorationType
        error
        data
        feature {
          id
        }
      }
    }
`;

angular.module('ontrack.extension.git', [
    'ot.service.core',
    'ot.service.configuration',
    'ot.service.form',
    'ot.service.structure',
    'ot.service.plot',
    'ot.service.graphql'
])
    .directive('otExtensionGitCommitSummary', function () {
        return {
            restrict: 'E',
            transclude: true,
            templateUrl: 'extension/git/directive.commit.summary.tpl.html',
            scope: {
                uiCommit: '=',
                title: '@'
            }
        };
    })
    .directive('otExtensionGitCommitInfo', () => ({
        restrict: 'E',
        templateUrl: 'extension/git/directive.commit.info.tpl.html',
        scope: {
            gitCommitInfo: '='
        }
    }))

    // Sync

    .config(function ($stateProvider) {
        $stateProvider.state('git-sync', {
            url: '/extension/git/sync/{branch}',
            templateUrl: 'extension/git/git.sync.tpl.html',
            controller: 'GitSyncCtrl'
        });
    })
    .controller('GitSyncCtrl', function ($stateParams, $state, $scope, $http, $interpolate, ot, otStructureService, otNotificationService) {

        var branchId = $stateParams.branch;
        var view = ot.view();
        view.commands = [
            ot.viewCloseCommand('/branch/' + branchId)
        ];

        // Loading of the sync information
        function load() {
            otStructureService.getBranch(branchId).then(function (branch) {
                $scope.branch = branch;
                view.title = $interpolate("Build synchronisation for branch {{project.name}}/{{name}}")(branch);
                view.breadcrumbs = ot.branchBreadcrumbs(branch);
            });
        }

        // Initialisation
        load();

        // Launching the sync
        $scope.launchSync = function () {
            ot.pageCall($http.post('extension/git/sync/' + branchId, {})).then(function () {
                // Message
                otNotificationService.info("The build synchronisation has been launched in the background.");
                // Goes back to the branch
                $state.go('branch', {branchId: branchId});
            });
        };

    })

    // Issues

    .config(function ($stateProvider) {
        $stateProvider.state('git-issue', {
            url: '/extension/git/{project}/issue/{issue}',
            templateUrl: 'extension/git/git.issue.tpl.html',
            controller: 'GitIssueCtrl'
        });
    })
    .controller('GitIssueCtrl', function ($stateParams, $scope, $http, $interpolate, ot, otGraphqlService) {
        const view = ot.view();
        view.title = "";

        const query = `
            query IssueInfo($project: Int!, $issue: String!) {
              projects(id: $project) {
                id
                name
                gitIssueInfo(token: $issue) {
                  issueServiceConfigurationRepresentation {
                    id
                    name
                    serviceId
                  }
                  issue
                  commitInfo {
                    ...gitCommitInfoFields
                  }
                }
              }
            }
             ${gitCommitInfoFragments}
        `;

        const queryVariables = {
            project: $stateParams.project,
            issue: $stateParams.issue
        };

        let viewInitialised = false;
        otGraphqlService.pageGraphQLCall(query, queryVariables).then(data => {
            $scope.project = data.projects[0];
            $scope.gitIssueInfo = data.projects[0].gitIssueInfo;
            if (!viewInitialised) {
                // View configuration
                view.breadcrumbs = ot.projectBreadcrumbs($scope.project);
                // Commands
                view.commands = [
                    ot.viewCloseCommand('/project/' + $scope.project.id)
                ];
                // OK
                viewInitialised = true;
            }
        });
    })

    // Configurations

    .config(function ($stateProvider) {
        // Artifactory configurations
        $stateProvider.state('git-configurations', {
            url: '/extension/git/configurations',
            templateUrl: 'extension/git/git.configurations.tpl.html',
            controller: 'GitConfigurationsCtrl'
        });
    })
    .controller('GitConfigurationsCtrl', function ($scope, $http, ot, otFormService, otAlertService, otConfigurationService) {
        var view = ot.view();
        view.title = 'Git configurations';
        view.description = 'Management of the Git configurations.';

        // Loading the Artifactory configurations
        function load() {
            ot.call($http.get('extension/git/configurations')).then(function (configurations) {
                $scope.configurations = configurations;
                view.commands = [
                    {
                        id: 'git-configuration-create',
                        name: "Create a configuration",
                        cls: 'ot-command-new',
                        action: $scope.createConfiguration
                    },
                    ot.viewCloseCommand('/home')
                ];
            });
        }

        load();

        // Creating a configuration
        $scope.createConfiguration = function () {
            otFormService.display({
                uri: $scope.configurations._create,
                title: "Git configuration",
                buttons: [otConfigurationService.testButton($scope.configurations._test)],
                submit: function (data) {
                    return ot.call($http.post($scope.configurations._create, data));
                }
            }).then(load);
        };

        // Deleting a configuration
        $scope.deleteConfiguration = function (configuration) {
            otAlertService.confirm({
                title: 'Git configuration',
                message: "Do you really want to delete this Git configuration? Some projects may still refer to it."
            }).then(
                function success() {
                    ot.call($http.delete(configuration._delete)).then(load);
                }
            );
        };

        // Updating a configuration
        $scope.updateConfiguration = function (configuration) {
            otFormService.display({
                uri: configuration._update,
                title: "Git configuration",
                buttons: [otConfigurationService.testButton($scope.configurations._test)],
                submit: function (data) {
                    return ot.call($http.put(configuration._update, data));
                }
            }).then(load);
        };
    })

    // Commits

    .config(function ($stateProvider) {
        $stateProvider.state('git-commit', {
            url: '/extension/git/{project}/commit/{commit}',
            templateUrl: 'extension/git/git.commit.tpl.html',
            controller: 'GitCommitCtrl'
        });
    })
    .controller('GitCommitCtrl', function ($stateParams, $scope, $http, $interpolate, ot, otGraphqlService) {
        const view = ot.view();
        view.title = "";

        const query = `
            query CommitInfo($project: Int!, $commit: String!) {
              projects(id: $project) {
                id
                name
                gitCommitInfo(commit: $commit) {
                  ...gitCommitInfoFields
                }
              }
            }
            ${gitCommitInfoFragments}
        `;

        const queryVariables = {
            project: $stateParams.project,
            commit: $stateParams.commit
        };


        let viewInitialised = false;
        otGraphqlService.pageGraphQLCall(query, queryVariables).then(data => {
            $scope.project = data.projects[0];
            $scope.gitCommitInfo = data.projects[0].gitCommitInfo;
            if (!viewInitialised) {
                // View configuration
                view.breadcrumbs = ot.projectBreadcrumbs($scope.project);
                // Commands
                view.commands = [
                    ot.viewCloseCommand('/project/' + $scope.project.id)
                ];
                // OK
                viewInitialised = true;
            }
        });
    })

    // Change log

    .config(function ($stateProvider) {
        $stateProvider.state('git-changelog', {
            url: '/extension/git/changelog?from&to',
            templateUrl: 'extension/git/git.changelog.tpl.html',
            controller: 'GitChangeLogCtrl'
        });
    })
    .directive('otExtensionGitChangelogFileFilters', function (otGraphqlService, otScmChangelogFilechangefilterService) {
        return {
            restrict: 'E',
            templateUrl: 'extension/git/directive.changelogFileFilters.tpl.html',
            transclude: true,
            scope: {
                projectId: '=',
                context: '='
            },
            controller: function ($scope) {
                $scope.quickPattern = '';
                $scope.selectedFilter = null;

                $scope.context.getSelectedPatterns = () => {
                    if ($scope.quickPattern) {
                        return [$scope.quickPattern];
                    } else if ($scope.selectedFilter) {
                        return $scope.selectedFilter.patterns;
                    } else {
                        return [];
                    }
                };

                $scope.$watch('projectId', (value) => {
                    if (value) {
                        otScmChangelogFilechangefilterService.loadFiltersWithGraphQL($scope.projectId).then(({canManage, filters}) => {
                            $scope.filters = filters;
                            $scope.canManage = canManage;
                        });
                    }
                });

                $scope.filterDisplayName = filter => {
                    if (filter.shared) {
                        return `${filter.name} (*)`;
                    } else {
                        return filter.name;
                    }
                };

                $scope.unselectPattern = () => {
                    $scope.quickPattern = '';
                    $scope.selectedFilter = undefined;
                    $scope.submitQuickPattern();
                };

                $scope.submitQuickPattern = () => {
                    const pattern = $scope.quickPattern;
                    if (pattern) {
                        $scope.submitPattern([pattern]);
                    } else {
                        $scope.submitPattern(null);
                    }
                };

                $scope.saveQuickFilter = () => {
                    const pattern = $scope.quickPattern;
                    if (pattern) {
                        otScmChangelogFilechangefilterService.addFilterByProjectId($scope.projectId, [pattern])
                            .then(filter => {
                                // Adds the filter into the list and selects it
                                $scope.filters.push(filter);
                                $scope.selectedFilter = filter;
                            });
                    }
                };

                $scope.$watch('selectedFilter', () => {
                    if ($scope.selectedFilter) {
                        $scope.submitPattern($scope.selectedFilter.patterns);
                    } else {
                        $scope.submitPattern(undefined);
                    }
                });

                $scope.editFileFilter = () => {
                    if ($scope.selectedFilter) {
                        otScmChangelogFilechangefilterService.editFilterByProjectId($scope.projectId, $scope.selectedFilter)
                            .then(filter => {
                                $scope.selectedFilter.patterns = filter.patterns;
                                $scope.submitPattern(filter.patterns);
                            });
                    }
                };

                $scope.deleteFileFilter = () => {
                    if ($scope.selectedFilter) {
                        otScmChangelogFilechangefilterService.deleteFilterByProjectId($scope.projectId, $scope.selectedFilter);
                        $scope.filters.splice($scope.filters.indexOf($scope.selectedFilter), 1);
                        $scope.selectedFilter = undefined;
                    }
                };

                $scope.filterCanShare = (filter) => {
                    return filter && !filter.shared && $scope.canManage;
                };

                $scope.shareFileFilter = (filter) => {
                    otScmChangelogFilechangefilterService.shareFileFilterByProjectId($scope.projectId, filter);
                };

                $scope.submitPattern = (patterns) => {
                    $scope.context.filterFunction = otScmChangelogFilechangefilterService.itemFilterFunction(patterns, (item) => item.path);
                };
            }
        };
    })
    .controller('GitChangeLogCtrl', function ($q, $log, $modal, $interpolate, $anchorScroll, $location, $stateParams, $scope, $http,
                                              ot, otGraphqlService, otScmChangeLogService) {

        // The view
        const view = ot.view();
        view.title = "Git change log";

        // Boundaries
        const from = $stateParams.from;
        const to = $stateParams.to;

        // Initial button names
        $scope.commitsCommand = "Commits";
        $scope.issuesCommand = "Issues";
        $scope.filesCommand = "File changes";

        // Loading the change log skeleton
        const loadChangeLogFrame = () => {
            const query = `
                query GitChangeLog( $from: Int!, $to: Int!, ) {
                    gitChangeLog(from: $from, to: $to) {
                        uuid
                        project {
                            id
                        }
                        buildFrom {
                            ...BuildInfo
                        }
                        buildTo {
                            ...BuildInfo
                        }
                        hasIssues
                    }
                }

                fragment BuildInfo on Build {
                  id
                  name
                  creation {
                    time
                    user
                  }
                  links {
                    _page
                  }
                  branch {
                    id
                    name
                    links {
                        _page
                    }
                    project {
                      id
                      name
                    }
                  }
                  decorations {
                    decorationType
                    feature {
                      id
                    }
                    data
                  }
                  promotionRuns {
                    creation {
                      time
                    }
                    build {
                      id
                      name
                    }
                    promotionLevel {
                      id
                      name
                      image
                      _image
                    }
                  }
                  validations {
                    validationStamp {
                      id
                      name
                      image
                      _image
                      links {
                        _page
                      }
                    }
                    validationRuns(count: 1) {
                      validationRunStatuses(lastOnly: true) {
                        statusID {
                          id
                        }
                        description
                        creation {
                            time
                            user
                        }
                      }
                      links {
                        _page
                      }
                    }
                  }
                }
            `;
            otGraphqlService.pageGraphQLCall(query, {
                from: from,
                to: to
            }).then(data => {
                $scope.changeLog = data.gitChangeLog;
                view.breadcrumbs = ot.projectBreadcrumbs($scope.changeLog.buildFrom.branch.project);
                view.commands = [
                    ot.viewCloseCommand('/project/' + $scope.changeLog.buildFrom.branch.project.id)
                ];
            });
        };

        // Loading the change load at startup
        loadChangeLogFrame();

        // Loading the commits
        $scope.changeLogCommits = () => {
            if (!$scope.commits) {
                $scope.commitsLoading = true;
                $scope.commitsCommand = "Loading the commits...";
                const query = `
                    query GetChangeLogCommits($uuid: String!) {
                      gitChangeLogByUUID(uuid: $uuid) {
                        commitsPlot
                        commits {
                          id
                          shortId
                          annotatedMessage
                          link
                          author
                          timestamp
                          build {
                            id
                            name
                            links {
                              _page
                            }
                            promotionRuns(lastPerLevel: true) {
                              promotionLevel {
                                id
                                name
                                image
                                _image
                              }
                            }
                            using {
                              pageItems {
                                id
                                branch {
                                  project {
                                    name
                                  }
                                }
                                name
                              }
                            }
                          }
                        }
                      }
                    }
                `;
                otGraphqlService.pageGraphQLCall(query, {uuid: $scope.changeLog.uuid}).then(data => {
                    $scope.commits = data.gitChangeLogByUUID.commits;
                    $scope.commitsPlot = data.gitChangeLogByUUID.commitsPlot;
                }).finally(() => {
                    $scope.commitsLoading = false;
                    $scope.commitsCommand = "Commits";
                    $location.hash('commits');
                    $anchorScroll();
                });
            } else {
                $location.hash('commits');
                $anchorScroll();
            }
        };

        // Loading the issues
        $scope.changeLogIssues = () => {
            if (!$scope.issues) {
                $scope.issuesLoading = true;
                $scope.issuesCommand = "Loading the issues...";
                const query = `
                    query GetChangeLogIssues($uuid: String!) {
                      gitChangeLogByUUID(uuid: $uuid) {
                        issues {
                          issueServiceConfiguration {
                            serviceId
                          }
                          list {
                            issue: issueObject
                          }
                        }
                      }
                    }
                `;
                otGraphqlService.pageGraphQLCall(query, {uuid: $scope.changeLog.uuid}).then(data => {
                    $scope.issues = data.gitChangeLogByUUID.issues;
                    $location.hash('issues');
                    $anchorScroll();
                }).finally(() => {
                    $scope.issuesLoading = false;
                    $scope.issuesCommand = "Issues";
                });
            } else {
                $location.hash('issues');
                $anchorScroll();
            }
        };

        // Configuring the change log export
        $scope.changeLogExport = () => {
            otScmChangeLogService.displayChangeLogExportGraphQL({
                projectId: $scope.changeLog.project.id,
                buildFromId: $scope.changeLog.buildFrom.id,
                buildToId: $scope.changeLog.buildTo.id,
            });
        };

        // Loading the file changes if needed
        $scope.changeLogFiles = () => {
            if (!$scope.files) {
                $scope.filesLoading = true;
                $scope.filesCommand = "Loading the file changes...";
                const query = `
                    query GetChangeLogFiles($uuid: String!) {
                      gitChangeLogByUUID(uuid: $uuid) {
                        files {
                          list {
                            changeType
                            oldPath
                            newPath
                            path
                            url
                          }
                        }
                      }
                    }
                `;
                otGraphqlService.pageGraphQLCall(query, {uuid: $scope.changeLog.uuid}).then(data => {
                    $scope.files = data.gitChangeLogByUUID.files;
                    $location.hash('files');
                    $anchorScroll();
                }).finally(() => {
                    $scope.filesLoading = false;
                    $scope.filesCommand = "File changes";
                });
            } else {
                $location.hash('files');
                $anchorScroll();
            }
        };

        $scope.fileChangeContext = {};

        $scope.diffFileFilter = (patterns) => {
            $scope.diffComputing = true;
            const selectedPatterns = patterns ? patterns : $scope.fileChangeContext.getSelectedPatterns();
            const params = {
                from: $scope.changeLog.buildFrom.id,
                to: $scope.changeLog.buildTo.id,
                patterns: selectedPatterns.join(',')
            };
            return ot.pageCall($http.get("/extension/git/changelog/diff", {params})).then(diff => {
                let link = "/extension/git/changelog/diff";
                link += $interpolate('?from={{from}}&to={{to}}&patterns={{patterns}}')(params);
                $modal.open({
                    templateUrl: 'extension/scm/dialog.scmDiff.tpl.html',
                    controller: 'otExtensionScmDialogDiff',
                    resolve: {
                        config: function () {
                            return {
                                diff: diff,
                                link: link
                            };
                        }
                    }
                });
            }).finally(() => {
                $scope.diffComputing = false;
            });
        };

        // Shows the diff for a file
        $scope.showFileDiff = changeLogFile => {
            if (!$scope.diffComputing) {
                changeLogFile.diffComputing = true;
                $scope.diffFileFilter([changeLogFile.path])
                    .finally(() => {
                        changeLogFile.diffComputing = false;
                    });
            }
        };

    })
    .directive('gitPlot', function (otPlot) {
        return {
            restrict: 'A',
            scope: {
                gitPlot: '='
            },
            link: function (scope, element) {
                scope.$watch('gitPlot',
                    function () {
                        if (scope.gitPlot) {
                            otPlot.draw(element[0], scope.gitPlot);
                        }
                    }
                );
            }
        };
    })

    // Git project sync

    .config(function ($stateProvider) {
        // Git project sync
        $stateProvider.state('git-project-sync', {
            url: '/extension/git/project-sync/{projectId}',
            templateUrl: 'extension/git/git.project-sync.tpl.html',
            controller: 'GitProjectSyncCtrl'
        });
    })
    .controller('GitProjectSyncCtrl', function ($http, $scope, $stateParams, ot, otStructureService, otNotificationService) {
        // Gets the project ID from the parameters
        var projectId = $stateParams.projectId;
        // View definition
        var view = ot.view();
        view.title = 'Git project synchronisation';
        // Loading the project
        otStructureService.getProject(projectId).then(function (project) {
            $scope.project = project;
            // Sub page of the project
            view.breadcrumbs = ot.projectBreadcrumbs(project);
            // Commands
            view.commands = [
                ot.viewCloseCommand('/project/' + project.id)
            ];
            // Loads the Git synchronisation information
            return ot.pageCall($http.get(project._gitSync));
        }).then(function (gitSyncInfo) {
            $scope.gitSyncInfo = gitSyncInfo;
        });
        // Project synchronisation
        $scope.projectSync = function (reset) {
            $scope.synchronising = true;
            ot.pageCall($http.post($scope.project._gitSync, {reset: reset})).then(function (ack) {
                if (!ack.success) {
                    otNotificationService.error("The Git synchronisation could be launched.");
                } else {
                    otNotificationService.success("The Git synchronisation has been launched in the background.");
                }
                // Loads the Git synchronisation information
                return ot.pageCall($http.get($scope.project._gitSync));
            }).then(function (gitSyncInfo) {
                $scope.gitSyncInfo = gitSyncInfo;
            }).finally(function () {
                $scope.synchronising = false;
            });
        };
    })
;