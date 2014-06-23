angular.module('ot.extension.svn.changelog', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure'
])
    .config(function ($stateProvider) {
        // SVN configurations
        $stateProvider.state('svn-changelog', {
            url: '/extension/svn/changelog?branch&from&to',
            templateUrl: 'app/extension/svn/svn.changelog.tpl.html',
            controller: 'SVNChangeLogCtrl'
        });
    })
    .controller('SVNChangeLogCtrl', function ($q, $log, $interpolate, $location, $stateParams, $scope, $http, ot, otStructureService) {

        // The build request
        $scope.buildDiffRequest = {
            branch: $stateParams.branch,
            from: $stateParams.from,
            to: $stateParams.to
        };

        // The view
        var view = ot.view();
        view.title = "Subversion change log";
        view.description = "Loading change log...";

        // Loading the branch
        otStructureService.getBranch($stateParams.branch).then(function (branch) {
            view.breadcrumbs = ot.branchBreadcrumbs(branch);
        });

        // Loading the builds
        $q.all([
            otStructureService.getBuild($stateParams.from),
            otStructureService.getBuild($stateParams.to)
        ]).then(function (result) {
            $scope.buildFrom = result[0];
            $scope.buildTo = result[1];
            // Adjusting the view description
            view.description = $interpolate("From build {{buildFrom.name}} to {{buildTo.name}}")({
                buildFrom: $scope.buildFrom,
                buildTo: $scope.buildTo
            });
        });
        // otStructureService.getBuild($stateParams.from).then(function (build1))

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
        });

    })
;