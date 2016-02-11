angular.module('ot.extension.svn.changelog', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure',
    'ontrack.extension.scm'
])
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
;