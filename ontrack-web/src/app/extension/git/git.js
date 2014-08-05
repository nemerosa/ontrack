angular.module('ontrack.extension.git', [
    'ot.extension.git.configuration',
    'ot.extension.git.sync',
    'ot.extension.git.changelog',
    'ot.extension.git.issue'
])
    .directive('otExtensionGitCommitSummary', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/extension/git/directive.commit.summary.tpl.html',
            scope: {
                commitInfo: '=',
                title: '@'
            }
        };
    })
    .directive('otExtensionGitCommitBuilds', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/extension/git/directive.commit.builds.tpl.html',
            scope: {
                commitInfo: '='
            }
        };
    })
;