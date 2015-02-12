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
    .directive('otExtensionScmIssueCommitBranchInfos', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/extension/scm/directive.scmIssueCommitBranchInfos.tpl.html',
            scope: {
                infos: '='
            }
        };
    })
    .directive('otExtensionScmChangelogFilechangefilter', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/extension/scm/directive.scmChangelogFilechangefilter.tpl.html',
            scope: {
                project: '='
            },
            controller: function ($scope) {
                $scope.submitQuickPattern = function () {
                    var pattern = $scope.quickPattern;
                    if (pattern) {
                        alert(pattern);
                    }
                };
            }
        };
    })
    .service('otScmChangeLogService', function ($http, $modal, $interpolate, ot) {
        var self = {};

        function storeExportRequest(projectId, exportRequest) {
            localStorage.setItem(
                    'issueExportConfig_' + projectId,
                JSON.stringify(exportRequest)
            );
        }

        function loadExportRequest(projectId) {
            var json = localStorage.getItem('issueExportConfig_' + projectId);
            if (json) {
                return JSON.parse(json);
            } else {
                return {
                    grouping: []
                };
            }
        }

        self.displayChangeLogExport = function (config) {

            var projectId = config.changeLog.project.id;

            $modal.open({
                templateUrl: 'app/extension/scm/scmChangeLogExport.tpl.html',
                controller: function ($scope, $modalInstance) {
                    $scope.config = config;
                    // Export request
                    $scope.exportRequest = loadExportRequest(projectId);
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
                    // Removing a group
                    $scope.removeGroup = function (groups, group) {
                        var idx = groups.indexOf(group);
                        if (idx >= 0) {
                            groups.splice(idx, 1);
                        }
                    };

                    // Export generation
                    $scope.doExport = function () {

                        // Request
                        var request = {
                            from: config.changeLog.scmBuildFrom.buildView.build.id,
                            to: config.changeLog.scmBuildTo.buildView.build.id
                        };

                        // Permalink
                        var url = config.exportIssuesLink;
                        url += $interpolate('?from={{from}}&to={{to}}')(request);

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
                            storeExportRequest(projectId, $scope.exportRequest);
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