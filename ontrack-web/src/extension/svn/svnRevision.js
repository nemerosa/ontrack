angular.module('ot.extension.svn.revision', [
    'ui.router',
    'ot.service.core'
])
    .config(function ($stateProvider) {
        $stateProvider.state('svn-revision', {
            url: '/extension/svn/revision/{configuration}/{revision}',
            templateUrl: 'app/extension/svn/svn.revision.tpl.html',
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
;