angular.module('ontrack.extension.svn', [
    'ui.router',
    'ontrack.extension.scm',
    'ot.service.core',
    'ot.service.structure',
    'ot.service.configuration',
    'ot.service.form'
])
    .config(function ($stateProvider) {
        // SVN configurations
        $stateProvider.state('svn-configurations', {
            url: '/extension/svn/configurations',
            templateUrl: 'extension/svn/svn.configurations.tpl.html',
            controller: 'SVNConfigurationsCtrl'
        });

    })
    .controller('SVNConfigurationsCtrl', function ($scope, $http, $modal, ot, otFormService, otAlertService, otConfigurationService) {
        var view = ot.view();
        view.title = 'SVN configurations';
        view.description = 'Management of the SVN configurations.';

        $scope.configurationFilter = "";

        // Loading the SVN configurations
        function loadSVNConfigurations() {
            ot.call($http.get('extension/svn/configurations')).then(function (configurations) {
                $scope.configurations = configurations;
                view.commands = [
                    {
                        id: 'svn-configuration-create',
                        name: "Create a configuration",
                        cls: 'ot-command-new',
                        action: $scope.createConfiguration
                    },
                    ot.viewApiCommand(configurations._self),
                    ot.viewCloseCommand('/home')
                ];
            });
        }

        loadSVNConfigurations();

        // Creating a configuration
        $scope.createConfiguration = function () {
            otFormService.display({
                uri: $scope.configurations._create,
                title: "SVN configuration",
                buttons: [otConfigurationService.testButton($scope.configurations._test)],
                submit: function (data) {
                    return ot.call($http.post($scope.configurations._create, data));
                }
            }).then(loadSVNConfigurations);
        };

        // Deleting a configuration
        $scope.deleteConfiguration = function (configuration) {
            otAlertService.confirm({
                title: 'Deleting configuration',
                message: "Do you really want to delete this SVN configuration? Some projects may still refer to it."
            }).then(
                function success() {
                    ot.call($http.delete(configuration._delete)).then(loadSVNConfigurations);
                }
            );
        };

        // Updating a configuration
        $scope.updateConfiguration = function (configuration) {
            otFormService.display({
                uri: configuration._update,
                title: "SVN configuration",
                buttons: [otConfigurationService.testButton($scope.configurations._test)],
                submit: function (data) {
                    return ot.call($http.put(configuration._update, data));
                }
            }).then(loadSVNConfigurations);
        };

        // Configuration indexation
        $scope.indexation = function (configuration) {
            $modal.open({
                templateUrl: 'extension/svn/svn.dialog.indexation.tpl.html',
                controller: 'svnDialogIndexation',
                resolve: {
                    config: function () {
                        return {
                            configuration: configuration
                        };
                    }
                }
            });
        };

    })
    .directive('otExtensionSvnRevisionSummary', function () {
        return {
            restrict: 'E',
            templateUrl: 'extension/svn/directive.revision.summary.tpl.html',
            transclude: true,
            scope: {
                revisionInfo: '=',
                title: '@'
            }
        };
    })
    .directive('otExtensionSvnRevisionBuilds', function () {
        return {
            restrict: 'E',
            templateUrl: 'extension/svn/directive.revision.builds.tpl.html',
            scope: {
                ontrackSvnRevisionInfo: '=',
                mergedRevisionInfos: '='
            }
        };
    })
    .directive('otExtensionSvnRevisionPromotions', function () {
        return {
            restrict: 'E',
            templateUrl: 'extension/svn/directive.revision.promotions.tpl.html',
            scope: {
                ontrackSvnRevisionInfo: '=',
                mergedRevisionInfos: '='
            }
        };
    })

/**
 * Change log
 */

    .config(function ($stateProvider) {
        // SVN configurations
        $stateProvider.state('svn-changelog', {
            url: '/extension/svn/changelog?from&to',
            templateUrl: 'extension/svn/svn.changelog.tpl.html',
            controller: 'SVNChangeLogCtrl'
        });
    })
    .controller('SVNChangeLogCtrl', function ($q, $log, $interpolate, $anchorScroll, $location, $stateParams, $scope, $http,
                                              ot, otStructureService, otScmChangeLogService, otScmChangelogFilechangefilterService) {

        // The build request
        $scope.buildDiffRequest = {
            from: $stateParams.from,
            to: $stateParams.to
        };

        // The view
        var view = ot.view();
        view.title = "Subversion change log";

        /**
         * The REST end point to contact is contained by the current path, with the leading
         * slash being removed.
         */
        var path = $location.path().substring(1);

        /**
         * Loads the change log
         */

        ot.pageCall($http.get(path, {params: $scope.buildDiffRequest})).then(function (changeLog) {
            $scope.changeLog = changeLog;

            view.breadcrumbs = ot.projectBreadcrumbs(changeLog.project);

            $scope.revisionsCommand = "Revisions";
            $scope.issuesCommand = "Issues";
            $scope.filesCommand = "File changes";

            // Loading the revisions if needed
            $scope.changeLogRevisions = function () {
                if (!$scope.revisions) {
                    $scope.revisionsLoading = true;
                    $scope.revisionsCommand = "Loading the revisions...";
                    ot.pageCall($http.get($scope.changeLog._revisions)).then(function (revisions) {
                        $scope.revisions = revisions;
                        $scope.revisionsLoading = false;
                        $scope.revisionsCommand = "Revisions";
                        $location.hash('revisions');
                        $anchorScroll();
                    });
                } else {
                    $location.hash('revisions');
                    $anchorScroll();
                }
            };

            // Loading the issues if needed
            $scope.changeLogIssues = function () {
                if (!$scope.issues) {
                    $scope.issuesLoading = true;
                    $scope.issuesCommand = "Loading the issues...";
                    ot.pageCall($http.get($scope.changeLog._issues)).then(function (issues) {
                        $scope.issues = issues;
                        $scope.issuesLoading = false;
                        $scope.issuesCommand = "Issues";
                        $location.hash('issues');
                        $anchorScroll();
                    });
                } else {
                    $location.hash('issues');
                    $anchorScroll();
                }
            };

            // Loading the file changes if needed
            $scope.changeLogFiles = function () {
                if (!$scope.files) {
                    $scope.filesLoading = true;
                    $scope.filesCommand = "Loading the file changes...";
                    ot.pageCall($http.get($scope.changeLog._files)).then(function (files) {
                        $scope.files = files;
                        $scope.filesLoading = false;
                        $scope.filesCommand = "File changes";
                        $location.hash('files');
                        $anchorScroll();
                    });
                } else {
                    $location.hash('files');
                    $anchorScroll();
                }
            };

            // File filter configuration
            $scope.changeLogFileFilterConfig = otScmChangelogFilechangefilterService.initFilterConfig();

            // Configuring the change log export
            $scope.changeLogExport = function () {
                otScmChangeLogService.displayChangeLogExport({
                    changeLog: $scope.changeLog,
                    exportFormatsLink: changeLog._exportFormats,
                    exportIssuesLink: changeLog._exportIssues
                });
            };

            // Shows a diff for a file
            $scope.showFileDiff = function (changeLog, svnChangeLogFile) {
                if (!$scope.diffLoading) {
                    $scope.diffLoading = true;
                    svnChangeLogFile.diffLoading = true;
                    otScmChangelogFilechangefilterService
                        .diffFileFilter(changeLog, svnChangeLogFile.path)
                        .finally(function () {
                            svnChangeLogFile.diffLoading = false;
                            $scope.diffLoading = false;
                        });
                }
            };
        });

    })

/**
 * Indexation dialog
 */

    .controller('svnDialogIndexation', function ($scope, $modalInstance, $http, config, ot, otAlertService, otFormService) {
        // General configuration
        $scope.config = config;
        // Range form
        $scope.range = {
            from: 1,
            to: 1
        };

        // Getting the last revision info
        ot.call($http.get(config.configuration._indexation)).then(function (lastRevisionInfo) {
            $scope.lastRevisionInfo = lastRevisionInfo;
        });

        // Getting the range
        ot.call($http.get(config.configuration._indexationRange)).then(function (form) {
            $scope.range.from = otFormService.getFieldValue(form, 'from');
            $scope.range.to = otFormService.getFieldValue(form, 'to');
        });

        // Cancelling the dialog
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        // Result of the indexation call
        function onSuccess(ack) {
            if (ack.success) {
                // Closes the dialog
                $scope.cancel();
            } else {
                $scope.message = {
                    type: 'warning',
                    content: "Indexation is already running on this repository."
                };
            }
        }

        // Indexation from latest
        $scope.indexFromLatest = function () {
            ot.call($http.post($scope.config.configuration._indexationFromLatest)).then(
                onSuccess,
                function error(message) {
                    $scope.message = message;
                }
            );
        };

        // Range indexation
        $scope.indexRange = function () {
            var from = $scope.range.from;
            var to = $scope.range.to;
            ot.call($http.post($scope.config.configuration._indexationRange, {
                from: from,
                to: to
            })).then(
                onSuccess,
                function error(message) {
                    $scope.message = message;
                }
            );
        };

        // Full re-indexation
        $scope.fullReindexation = function () {
            otAlertService.confirm({
                title: "Full re-indexation",
                message: "Are you sure to fully re-index the repository? All associated cached data (revisions, issues...) will be lost."
            }).then(function () {
                // Launches full reindexation
                ot.call($http.post($scope.config.configuration._indexationFull)).then(
                    onSuccess,
                    function error(message) {
                        $scope.message = message;
                    }
                );
            });
        };
    })

/**
 * SVN issues
 */

    .config(function ($stateProvider) {
        $stateProvider.state('svn-issue', {
            url: '/extension/svn/issue/{configuration}/{issue}',
            templateUrl: 'extension/svn/svn.issue.tpl.html',
            controller: 'SVNIssueCtrl'
        });
    })
    .controller('SVNIssueCtrl', function ($stateParams, $scope, $http, $interpolate, ot) {

        var configuration = $stateParams.configuration;
        var issue = $stateParams.issue;

        var view = ot.view();
        view.title = $interpolate("Issue {{issue}} in {{configuration}} repository")($stateParams);

        ot.call(
            $http.get(
                $interpolate('extension/svn/configuration/{{configuration}}/issue/{{issue}}')($stateParams)
            )).then(function (ontrackSVNIssueInfo) {
                $scope.ontrackSVNIssueInfo = ontrackSVNIssueInfo;
            });
    })

/**
 * SVN revision
 */

    .config(function ($stateProvider) {
        $stateProvider.state('svn-revision', {
            url: '/extension/svn/revision/{configuration}/{revision}',
            templateUrl: 'extension/svn/svn.revision.tpl.html',
            controller: 'SVNRevisionCtrl'
        });
    })
    .controller('SVNRevisionCtrl', function ($stateParams, $scope, $http, $interpolate, ot) {

        var configuration = $stateParams.configuration;
        var revision = $stateParams.revision;

        var view = ot.view();
        view.title = $interpolate("Revision {{revision}} in {{configuration}} repository")($stateParams);

        ot.call(
            $http.get(
                $interpolate('extension/svn/configuration/{{configuration}}/revision/{{revision}}')($stateParams)
            )).then(function (ontrackSVNRevisionInfo) {
                $scope.ontrackSVNRevisionInfo = ontrackSVNRevisionInfo;
            });
    })

/**
 * Synchronisation
 */

    .config(function ($stateProvider) {
        $stateProvider.state('svn-sync', {
            url: '/extension/svn/sync/{branch}',
            templateUrl: 'extension/svn/svn.sync.tpl.html',
            controller: 'SVNSyncCtrl'
        });
    })
    .controller('SVNSyncCtrl', function ($stateParams, $state, $scope, $http, $interpolate, ot, otStructureService, otNotificationService) {

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
            ot.pageCall($http.post('extension/svn/sync/' + branchId, {})).then(function () {
                // Message
                otNotificationService.info("The build synchronisation has been launched in the background.");
                // Goes back to the branch
                $state.go('branch', {branchId: branchId});
            });
        };

    })

;