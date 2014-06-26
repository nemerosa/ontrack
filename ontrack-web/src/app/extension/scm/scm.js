angular.module('ontrack.extension.scm', [

])
    .directive('otScmChangelogBuild', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/extension/scm/directive.scmChangelogBuild.tpl.html',
            scope: {
                scmBuildView: '='
            },
            transclude: true
        };
    })
/**
 * Truncates the start of a path
 */
    .filter('otExtensionScmTruncatePath', function () {
        return function (text, length) {
            var prefix = '...';
            if (isNaN(length)) {
                length = 10;
            }
            if (text.length <= length || text.length - prefix.length <= length) {
                return text;
            }
            else {
                return prefix + String(text).substring(text.length - prefix.length - length, text.length);
            }
        };
    })
;