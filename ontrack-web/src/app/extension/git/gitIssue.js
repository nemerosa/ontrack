angular.module('ot.extension.git.issue', [
    'ui.router',
    'ot.service.core'
])
    .config(function ($stateProvider) {
        $stateProvider.state('git-issue', {
            url: '/extension/git/{branch}/issue/{issue}',
            templateUrl: 'app/extension/git/git.issue.tpl.html',
            controller: 'GitIssueCtrl'
        });
    })
    .controller('GitIssueCtrl', function ($stateParams, $scope, $http, $interpolate, ot) {

        var view = ot.view();

        ot.call(
            $http.get(
                $interpolate('extension/git/{{branch}}/issue/{{issue}}')($stateParams)
            )).then(function (ontrackGitIssueInfo) {
                $scope.ontrackGitIssueInfo = ontrackGitIssueInfo;
            });
    })
;