angular.module('ontrack.extension.git', [
    'ot.extension.git.configuration',
    'ot.extension.git.sync',
    'ot.extension.git.changelog',
    'ot.extension.git.issue',
    'ot.extension.git.commit'
])
    .directive('otExtensionGitCommitSummary', function () {
        return {
            restrict: 'E',
            transclude: true,
            templateUrl: 'app/extension/git/directive.commit.summary.tpl.html',
            scope: {
                uiCommit: '=',
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
    .directive('otExtensionGitCommitPromotions', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/extension/git/directive.commit.promotions.tpl.html',
            scope: {
                commitInfo: '='
            }
        };
    })
;