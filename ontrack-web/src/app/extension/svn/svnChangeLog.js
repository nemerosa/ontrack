angular.module('ot.extension.svn.changelog', [
    'ui.router',
    'ot.service.core'
])
    .config(function ($stateProvider) {
        // SVN configurations
        $stateProvider.state('svn-changelog', {
            url: '/extension/svn/changelog?branch&from&to',
            templateUrl: 'app/extension/svn/svn.changelog.tpl.html',
            controller: 'SVNChangeLogCtrl'
        });
    })
    .controller('SVNChangeLogCtrl', function ($log, $location, $stateParams, $scope, $http, ot) {

        // The build request
        $scope.buildDiffRequest = {
            branch: $stateParams.branch,
            from: $stateParams.from,
            to: $stateParams.to
        };

        /**
         * The REST end point to contact is contained by the current path, with the leading
         * slash being removed.
         */
        var path = $location.path().substring(1);

        /**
         * Loads the change log
         */

        ot.pageCall($http.get(path, {params: $scope.buildDiffRequest})).then(function (changeLog) {
            $log.debug('changeLog=', changeLog);
        });

    })
;