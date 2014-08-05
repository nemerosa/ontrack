angular.module('ot.extension.git.issue', [
    'ui.router',
    'ot.service.core'
])
    .config(function ($stateProvider) {
        $stateProvider.state('git-issue', {
            url: '/extension/git/issue/{branch}/{issue}',
            templateUrl: 'app/extension/git/git.issue.tpl.html',
            controller: 'GitIssueCtrl'
        });
    })
    .controller('GitIssueCtrl', function ($stateParams, $scope, $http, $interpolate, ot) {

        var configuration = $stateParams.configuration;
        var issue = $stateParams.issue;

        var view = ot.view();

//        ot.call(
//            $http.get(
//                $interpolate('extension/svn/configuration/{{configuration}}/issue/{{issue}}')($stateParams)
//            )).then(function (ontrackSVNIssueInfo) {
//                $scope.ontrackSVNIssueInfo = ontrackSVNIssueInfo;
//            });
    })
;