angular.module('ontrack.extension.scm', [
    'ot.service.form'
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
    .directive('otExtensionScmChangelogFilechangefilter', function (otScmChangelogFilechangefilterService) {
        return {
            restrict: 'E',
            templateUrl: 'app/extension/scm/directive.scmChangelogFilechangefilter.tpl.html',
            scope: {
                project: '=',
                filterCallback: '='
            },
            controller: function ($scope) {
                // TODO Loads the list of filters (async)
                $scope.filters = [];
                // Submitting a pattern
                $scope.submitQuickPattern = function () {
                    var pattern = $scope.quickPattern;
                    if (pattern) {
                        $scope.submitPattern([pattern]);
                    }
                };
                $scope.addFileFilter = function () {
                    otScmChangelogFilechangefilterService.addFilter($scope.project).then(function (filter) {
                        // Adds the filter into the list and selects it
                        $scope.filters.push(filter);
                        $scope.selectedFilter = filter;
                    });
                };
                $scope.editFileFilter = function () {
                    if ($scope.selectedFilter) {
                        otScmChangelogFilechangefilterService.editFilter($scope.project, $scope.selectedFilter).then(function (filter) {
                            $scope.selectedFilter.patterns = filter.patterns;
                            $scope.submitPattern(filter.patterns);
                        });
                    }
                };
                $scope.deleteFileFilter = function () {
                    if ($scope.selectedFilter) {
                        otScmChangelogFilechangefilterService.deleteFilter($scope.project, $scope.selectedFilter);
                        $scope.filters.splice($scope.filters.indexOf($scope.selectedFilter), 1);
                        $scope.selectedFilter = undefined;
                    }
                };
                $scope.$watch('selectedFilter', function () {
                    if ($scope.selectedFilter) {
                        $scope.submitPattern($scope.selectedFilter.patterns);
                    } else {
                        $scope.submitPattern(undefined);
                    }
                });
                $scope.submitPattern = function (patterns) {
                    // Sets the function on the callback
                    if ($scope.filterCallback) {
                        console.log('Calling callback with ', patterns);
                        $scope.filterCallback(otScmChangelogFilechangefilterService.filterFunction(patterns));
                    }
                };
                $scope.unselectPattern = function () {
                    $scope.quickPattern = '';
                    $scope.selectedFilter = undefined;
                    $scope.submitPattern(undefined);
                };
            }
        };
    })
    .service('otScmChangelogFilechangefilterService', function ($q, otFormService) {
        var self = {};

        function loadStore(project) {
            var json = localStorage.getItem("fileChangeFilters_" + project.id);
            if (json) {
                return JSON.parse(json);
            } else {
                return {};
            }
        }

        function saveStore(project, store) {
            localStorage.setItem("fileChangeFilters_" + project.id, JSON.stringify(store));
        }

        function patternMatch(pattern, path) {
            var re = pattern
                .replace(/\*\*/g, '$MULTI$')
                .replace(/\*/g, '$SINGLE$')
                .replace(/\$SINGLE\$/g, '[^\/]+')
                .replace(/\$MULTI\$/g, '.*');
            re = '^' + re + '$';
            var rx = new RegExp(re);
            return rx.test(path);
        }

        self.filterFunction = function (patterns) {
            return function (path) {
                if (patterns) {
                    return patterns.some(function (pattern) {
                        return patternMatch(pattern, path);
                    });
                } else {
                    return true;
                }
            };
        };

        self.addFilter = function (project) {
            // Form configuration
            var form = {
                fields: [{
                    name: 'name',
                    type: 'text',
                    label: "Name",
                    help: "Name to use to save the filter.",
                    required: true,
                    regex: '.*'
                }, {
                    name: 'patterns',
                    type: 'memo',
                    label: "Filter(s)",
                    help: "List of ANT-like patterns (one per line).",
                    required: true
                }]
            };
            // Shows the dialog
            return otFormService.display({
                form: form,
                title: "Create file change filter",
                submit: function (data) {
                    // Loads the store
                    var store = loadStore(project);
                    // Controlling the name
                    if (store[data.name]) {
                        return "Filter with name " + data.name + " already exists.";
                    }
                    // Parsing the patterns
                    var patterns = data.patterns.split('\n').map(function (it) { return it.trim(); });
                    // Saves the filter
                    store[data.name] = patterns;
                    saveStore(project, store);
                    // Returns the filter
                    var d = $q.defer();
                    d.resolve({
                        name: data.name,
                        patterns: patterns
                    });
                    return d.promise;
                }
            });
        };

        self.editFilter = function (project, filter) {
            // Form configuration
            var form = {
                fields: [{
                    name: 'name',
                    type: 'text',
                    label: "Name",
                    help: "Name to use to save the filter.",
                    required: true,
                    readOnly: true,
                    regex: '.*',
                    value: filter.name
                }, {
                    name: 'patterns',
                    type: 'memo',
                    label: "Filter(s)",
                    help: "List of ANT-like patterns (one per line).",
                    required: true,
                    value: filter.patterns.join('\n')
                }]
            };
            // Shows the dialog
            return otFormService.display({
                form: form,
                title: "Edit file change filter",
                submit: function (data) {
                    // Parsing the patterns
                    var patterns = data.patterns.split('\n').map(function (it) { return it.trim(); });
                    // Saves the filter
                    var store = loadStore(project);
                    store[filter.name] = patterns;
                    saveStore(project, store);
                    // Returns the filter
                    var d = $q.defer();
                    d.resolve({
                        name: filter.name,
                        patterns: patterns
                    });
                    return d.promise;
                }
            });
        };

        self.deleteFilter = function (project, filter) {
            var store = loadStore(project);
            delete store[filter.name];
            saveStore(project, store);
        };

        return self;
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