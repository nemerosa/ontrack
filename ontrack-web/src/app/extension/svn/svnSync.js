angular.module('ot.extension.svn.sync', [
    'ui.router',
    'ot.service.core'
])
    .config(function ($stateProvider) {
        $stateProvider.state('svn-sync', {
            url: '/extension/svn/sync/{branch}',
            templateUrl: 'app/extension/svn/svn.sync.tpl.html',
            controller: 'SVNSyncCtrl'
        });
    })
    .controller('SVNSyncCtrl', function ($stateParams, $scope, $http, $interpolate, ot) {

        var branch = $stateParams.branch;
        var view = ot.view();

    })
;