angular.module('ot.extension.git.changelog', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure'
])
    .config(function ($stateProvider) {
        $stateProvider.state('git-changelog', {
            url: '/extension/git/changelog?branch&from&to',
            templateUrl: 'app/extension/git/git.changelog.tpl.html',
            controller: 'GitChangeLogCtrl'
        });
    })
    .controller('GitChangeLogCtrl', function ($q, $log, $interpolate, $anchorScroll, $location, $stateParams, $scope, $http, ot, otStructureService) {

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
        });

    })
;