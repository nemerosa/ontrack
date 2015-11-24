angular.module('ot.extension.svn.issue', [
    'ui.router',
    'ot.service.core'
])
    .config(function ($stateProvider) {
        $stateProvider.state('svn-issue', {
            url: '/extension/svn/issue/{configuration}/{issue}',
            templateUrl: 'app/extension/svn/svn.issue.tpl.html',
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
;