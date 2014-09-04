angular.module('ot.extension.git.changelog', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure',
    'ot.service.plot',
    'ontrack.extension.scm'
])
    .config(function ($stateProvider) {
        $stateProvider.state('git-changelog', {
            url: '/extension/git/changelog?branch&from&to',
            templateUrl: 'app/extension/git/git.changelog.tpl.html',
            controller: 'GitChangeLogCtrl'
        });
    })
    .controller('GitChangeLogCtrl', function ($q, $log, $interpolate, $anchorScroll, $location, $stateParams, $scope, $http, ot, otStructureService, otScmChangeLogService) {

        // The build request
        $scope.buildDiffRequest = {
            branch: $stateParams.branch,
            from: $stateParams.from,
            to: $stateParams.to
        };

        // The view
        var view = ot.view();
        view.title = "Git change log";

        // Loading the branch
        otStructureService.getBranch($stateParams.branch).then(function (branch) {
            view.breadcrumbs = ot.branchBreadcrumbs(branch);
        });

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

            $scope.commitsCommand = "Commits";
            $scope.issuesCommand = "Issues";
            $scope.filesCommand = "File changes";

            // Loading the commits if needed
            $scope.changeLogCommits = function () {
                if (!$scope.commits) {
                    $scope.commitsLoading = true;
                    $scope.commitsCommand = "Loading the commits...";
                    ot.pageCall($http.get($scope.changeLog._commits)).then(function (commits) {
                        $scope.commits = commits;
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

            // Configuring the change log export
            $scope.changeLogExport = function () {
                otScmChangeLogService.displayChangeLogExport({
                    changeLog: $scope.changeLog,
                    exportFormatsLink: changeLog._exportFormats
                });
            };

        });

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
;