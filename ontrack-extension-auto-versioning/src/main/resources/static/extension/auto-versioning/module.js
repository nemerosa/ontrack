angular.module('ontrack.extension.auto-versioning', [
    'ot.service.core',
    'ot.service.graphql'
])
    .config(function ($stateProvider) {
        $stateProvider.state('auto-versioning-audit-global', {
            url: '/extension/auto-versioning/audit/global',
            templateUrl: 'extension/auto-versioning/audit-global.tpl.html',
            controller: 'AutoVersioningAuditGlobalCtrl'
        });
    })

    .controller('AutoVersioningAuditGlobalCtrl', function ($scope, ot) {
        const view = ot.view();
        view.title = `Auto versioning audit`;
    })

    .config(function ($stateProvider) {
        $stateProvider.state('auto-versioning-audit-project', {
            url: '/extension/auto-versioning/audit/project/{project}',
            templateUrl: 'extension/auto-versioning/audit-project.tpl.html',
            controller: 'AutoVersioningAuditProjectCtrl'
        });
    })

    .controller('AutoVersioningAuditProjectCtrl', function ($stateParams, $scope, ot) {
        $scope.projectName = $stateParams.project;

        const view = ot.view();
        view.title = `Auto versioning audit for ${$scope.projectName}`;
    })

    .config(function ($stateProvider) {
        $stateProvider.state('auto-versioning-audit-source', {
            url: '/extension/auto-versioning/audit/source/{project}',
            templateUrl: 'extension/auto-versioning/audit-source.tpl.html',
            controller: 'AutoVersioningAuditSourceCtrl'
        });
    })

    .controller('AutoVersioningAuditSourceCtrl', function ($stateParams, $scope, ot) {
        $scope.sourceName = $stateParams.project;

        const view = ot.view();
        view.title = `Auto versioning audit from ${$scope.sourceName}`;
    })

    .config(function ($stateProvider) {
        $stateProvider.state('auto-versioning-audit-branch', {
            url: '/extension/auto-versioning/audit/branch/{project}/{branch}',
            templateUrl: 'extension/auto-versioning/audit-branch.tpl.html',
            controller: 'AutoVersioningAuditBranchCtrl'
        });
    })

    .controller('AutoVersioningAuditBranchCtrl', function ($stateParams, $scope, ot) {
        $scope.projectName = $stateParams.project;
        $scope.branchName = $stateParams.branch;

        const view = ot.view();
        view.title = `Auto versioning audit for ${$scope.projectName} / ${$scope.branchName}`;
    })

    .directive('otExtensionAutoVersioningAudit', function () {

        const parametersQuery = ` { autoVersioningAuditStates } `;

        const auditQuery = `
            query AutoVersioningAuditEntries(
                $offset: Int!,
                $size: Int!,
                $filter: AutoVersioningAuditQueryFilter!,
                $projectVisible: Boolean!,
                $projectName: String!,
            ) {
                projects(name: $projectName) @include(if: $projectVisible) {
                    gitRepositoryHtmlURL
                }
                autoVersioningAuditEntries(offset: $offset, size: $size, filter: $filter) {
                    pageInfo {
                        nextPage {
                            offset
                            size
                        }
                    }
                    pageItems {
                        mostRecentState {
                            creation {
                                time
                            }
                            state
                            data
                        }
                        duration
                        running
                        audit {
                            creation {
                                time
                            }
                            state
                            data
                        }
                        order {
                            uuid
                            sourceProject
                            branch {
                                name
                                links {
                                    _page
                                }
                                project {
                                    name
                                    links {
                                        _page
                                    }
                                }
                            }
                            repositoryHtmlURL
                            targetPaths
                            targetRegex
                            targetProperty
                            targetPropertyRegex
                            targetPropertyType
                            targetVersion
                            autoApproval
                            autoApprovalMode
                            upgradeBranchPattern
                            postProcessing
                            postProcessingConfig
                            validationStamp
                        }
                    }
                }
            }
        `;

        return {
            restrict: 'E',
            templateUrl: 'extension/auto-versioning/directive.audit.tpl.html',
            scope: {
                project: '=',
                branch: '=',
                source: '='
            },
            controller: function ($scope, otGraphqlService) {
                $scope.filter = {
                    project: "",
                    branch: "",
                    state: "",
                    running: "",
                    source: "",
                    version: ""
                };

                otGraphqlService.pageGraphQLCall(parametersQuery).then(data => {
                    $scope.autoVersioningAuditStates = data.autoVersioningAuditStates;
                });

                let offset = 0;
                let size = 20;
                $scope.runningQuery = false;
                $scope.items = [];

                const onSearch = (reset) => {
                    let project;
                    let projectVisible;
                    let projectName;
                    if ($scope.project) {
                        project = $scope.project;
                        projectVisible = true;
                        projectName = $scope.project;
                    } else if ($scope.filter.project) {
                        project = $scope.filter.project;
                        projectVisible = false;
                        projectName = '';
                    } else {
                        project = null;
                        projectVisible = false;
                        projectName = '';
                    }

                    let branch;
                    if ($scope.branch) {
                        branch = $scope.branch;
                    } else if ($scope.filter.branch) {
                        branch = $scope.filter.branch;
                    } else {
                        branch = null;
                    }

                    let state;
                    if ($scope.filter.state) {
                        state = $scope.filter.state;
                    } else {
                        state = null;
                    }

                    let running;
                    if ($scope.filter.running) {
                        running = $scope.filter.running === "yes";
                    } else {
                        running = null;
                    }

                    let source;
                    if ($scope.source) {
                        source = $scope.source;
                    } else if ($scope.filter.source) {
                        source = $scope.filter.source;
                    } else {
                        source = null;
                    }

                    let version;
                    if ($scope.filter.version) {
                        version = $scope.filter.version;
                    } else {
                        version = null;
                    }

                    if (reset) {
                        offset = 0;
                        size = 20;
                    }

                    const queryVariables = {
                        offset: offset,
                        size: size,
                        filter: {
                            project: project,
                            branch: branch,
                            state: state,
                            running: running,
                            source: source,
                            version: version
                        },
                        projectName: projectName,
                        projectVisible: projectVisible
                    };

                    $scope.runningQuery = true;
                    otGraphqlService.pageGraphQLCall(auditQuery, queryVariables).then(data => {
                        if (data.projects && data.projects[0].gitRepositoryHtmlURL) {
                            $scope.gitRepositoryHtmlURL = data.projects[0].gitRepositoryHtmlURL;
                        } else {
                            $scope.gitRepositoryHtmlURL = undefined;
                        }
                        $scope.pageInfo = data.autoVersioningAuditEntries.pageInfo;
                        if (reset) {
                            $scope.items = data.autoVersioningAuditEntries.pageItems;
                        } else {
                            $scope.items = $scope.items.concat(data.autoVersioningAuditEntries.pageItems);
                        }
                    }).finally(() => {
                        $scope.runningQuery = false;
                    });
                };

                $scope.onSearch = () => onSearch(true);

                $scope.onClear = () => {
                    $scope.filter.project = "";
                    $scope.filter.branch = "";
                    $scope.filter.state = "";
                    $scope.filter.running = "";
                    $scope.filter.source = "";
                    $scope.filter.version = "";
                    onSearch(true);
                };

                onSearch(true);

                $scope.getItemDuration = (item) => {
                    return moment.duration(item.duration, 'ms').humanize();
                };

                $scope.loadNextPage = () => {
                    if ($scope.pageInfo.nextPage) {
                        offset = $scope.pageInfo.nextPage.offset;
                        size = $scope.pageInfo.nextPage.size;
                        onSearch(false);
                    }
                };

                // Details management

                $scope.details = {};

                $scope.showItemDetails = (item) => {
                    $scope.details[item.order.uuid] = true;
                };

                $scope.hideItemDetails = (item) => {
                    $scope.details[item.order.uuid] = false;
                };

                $scope.hideAllDetails = () => {
                    $scope.details = {};
                };
            }
        };
    })
;