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
    .service('otScmChangeLogService', function ($http, $modal, $interpolate, ot) {
        var self = {};

        function storeExportRequest(branchId, exportRequest) {
            localStorage.setItem(
                    'issueExportRequest_' + branchId,
                JSON.stringify(exportRequest)
            );
        }

        function loadExportRequest(branchId) {
            var json = localStorage.getItem('issueExportRequest_' + branchId);
            if (json) {
                return JSON.parse(json);
            } else {
                return {
                    grouping: []
                };
            }
        }

        self.displayChangeLogExport = function (config) {

            var branchId = config.changeLog.branch.id;

            $modal.open({
                templateUrl: 'app/extension/scm/scmChangeLogExport.tpl.html',
                controller: function ($scope, $modalInstance) {
                    $scope.config = config;
                    // Export request
                    // TODO Loads it from the local storage, indexed by the branch ID
                    $scope.exportRequest = loadExportRequest(branchId);
                    // Loading the export formats
                    ot.call($http.get(config.exportFormatsLink)).then(function (exportFormatsResources) {
                        $scope.exportFormats = exportFormatsResources.resources;
                    });

                    // Closing the dialog
                    $scope.cancel = function () {
                        $modalInstance.dismiss('cancel');
                    };

                    // Group management
                    $scope.addGroup = function () {
                        // Adds an empty group
                        $scope.exportRequest.grouping.push({
                            name: '',
                            types: ''
                        });
                    };

                    // Export generation
                    $scope.doExport = function () {

                        // Request
                        var request = {
                            branch: branchId,
                            from: config.changeLog.scmBuildFrom.buildView.build.id,
                            to: config.changeLog.scmBuildTo.buildView.build.id
                        };

                        // Permalink
                        var url = config.exportIssuesLink;
                        url += $interpolate('?branch={{branch}}&from={{from}}&to={{to}}')(request);

                        // Format
                        if ($scope.exportRequest.format) {
                            request.format = $scope.exportRequest.format;
                            url += $interpolate('&format={{format}}')(request);
                        }

                        // Grouping
                        if ($scope.exportRequest.grouping.length > 0) {
                            var grouping = '';
                            for (var i = 0; i < $scope.exportRequest.grouping.length; i++) {
                                if (i > 0) {
                                    grouping += '|';
                                }
                                var groupSpec = $scope.exportRequest.grouping[i];
                                grouping += groupSpec.name + '=' + groupSpec.types;
                            }
                            grouping = encodeURIComponent(grouping);
                            request.grouping = grouping;
                            url += $interpolate('&grouping={{grouping}}')(request);
                        }

                        // Exclude
                        if ($scope.exportRequest.exclude) {
                            request.exclude = $scope.exportRequest.exclude;
                            url += $interpolate('&exclude={{exclude}}')(request);
                        }

                        // Call
                        $scope.exportCalling = true;
                        ot.call($http.get(url)).then(function success(exportedIssues) {
                            $scope.exportCalling = false;
                            $scope.exportError = '';
                            $scope.exportContent = exportedIssues;
                            $scope.exportPermaLink = url;
                            storeExportRequest(branchId, $scope.exportRequest);
                        }, function error(message) {
                            $scope.exportCalling = false;
                            $scope.exportError = message.content;
                            $scope.exportContent = '';
                            $scope.exportPermaLink = '';
                        });
                    };

                }
            });
        };

        return self;
    })
;