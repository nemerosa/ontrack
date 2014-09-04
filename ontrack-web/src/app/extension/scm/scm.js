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
    .directive('otScmChangelogIssueValidations', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/extension/scm/directive.scmChangelogIssueValidations.tpl.html',
            scope: {
                changeLogIssue: '='
            }
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
    .service('otScmChangeLogService', function ($http, $modal, ot) {
        var self = {};

        self.displayChangeLogExport = function (config) {
            $modal.open({
                templateUrl: 'app/extension/scm/scmChangeLogExport.tpl.html',
                controller: function ($scope, $modalInstance) {
                    $scope.config = config;
                    // Export request
                    // TODO Loads it from the local storage, indexed by the branch ID
                    $scope.exportRequest = {};
                    // Loading the export formats
                    ot.call($http.get(config.exportFormatsLink)).then(function (exportFormatsResources) {
                        $scope.exportFormats = exportFormatsResources.resources;
                    });

                    // Closing the dialog
                    $scope.cancel = function () {
                        $modalInstance.dismiss('cancel');
                    };

                    // Export generation
                    $scope.doExport = function () {
                        // Request
                        var request = {
                            branch: config.changeLog.branch.id,
                            from: config.changeLog.scmBuildFrom.buildView.build.id,
                            to: config.changeLog.scmBuildTo.buildView.build.id
                        };
                        // Format
                        if ($scope.exportRequest.format) {
                            request.format = $scope.exportRequest.format;
                        }
                        // Call
                        $scope.exportCalling = true;
                        ot.call($http.get(config.exportIssuesLink, {
                            params: request
                        })).then(function success(exportedIssues) {
                            $scope.exportCalling = false;
                            $scope.exportContent = exportedIssues;
                        });
                    };

                }
            });
        };

        return self;
    })
;