angular.module('ot.view.branch', [
    'ui.router',
    'ot.service.core',
    'ot.service.task',
    'ot.service.form',
    'ot.service.structure',
    'ot.service.buildfilter',
    'ot.service.copy',
    'ot.dialog.validationStampRunView',
    'ot.dialog.validationStampRunGroup',
    'ot.dialog.promotionRuns',
    'ot.service.graphql',
    'ot.service.user'
])
    .config(function ($stateProvider) {
        $stateProvider.state('branch', {
            url: '/branch/{branchId}',
            templateUrl: 'app/view/view.branch.tpl.html',
            controller: 'BranchCtrl'
        });
    })
    .controller('BranchCtrl', function ($state, $scope, $stateParams, $http, $modal, $location, $rootScope,
                                        ot, otFormService, otStructureService, otAlertService, otTaskService, otNotificationService, otCopyService,
                                        otBuildFilterService, otGraphqlService, otUserService) {
        const view = ot.view();
        let viewInitialized = false;
        // Branch's id
        const branchId = $stateParams.branchId;

        // Loading indicators
        $scope.loadingBranch = true;
        $scope.loadingBuilds = false;

        // Query: loading the branch
        const gqlBranch = `
            query LoadBranch(
                $branchId: Int!,
            ) {
                branches(id: $branchId) {
                    id
                    name
                    description
                    annotatedDescription
                    project {
                        id
                        name
                    }
                    decorations {
                      ...decorationContent
                    }
                    links {
                        _actions
                        _update
                    }
                }
            }
            
            fragment decorationContent on Decoration {
                decorationType
                error
                data
                feature {
                    id
                }
            }
        `;

        // Query: loading the builds
        const gqlBuilds = `
            query LoadBuilds(
                $branchId: Int!,
                $offset: Int!,
                $size: Int!,
            ) {
                branches(id: $branchId) {
                    buildsPaginated(
                        offset: $offset,
                        size: $size,
                    ) {
                        pageInfo {
                            totalSize
                            nextPage {
                                offset
                                size
                            }
                        }
                        pageItems {
                            id
                            name
                            creation {
                              time
                            }
                            decorations {
                              ...decorationContent
                            }
                            promotionRuns(lastPerLevel: true) {
                              creation {
                                time
                              }
                              promotionLevel {
                                id
                                name
                                image
                                _image
                              }
                            }
                        }
                    }
                }
            }
            
            fragment decorationContent on Decoration {
                decorationType
                error
                data
                feature {
                    id
                }
            }
        `;

        // Loading the branch
        const loadBranch = () => {
            $scope.loadingBranch = true;
            otGraphqlService.pageGraphQLCall(gqlBranch, {branchId})
                .then(data => {
                    $scope.branch = data.branches[0];
                    if (!viewInitialized) {
                        view.breadcrumbs = ot.projectBreadcrumbs($scope.branch.project);
                        view.commands = [
                            {
                                condition: function () {
                                    return $scope.branch.links._update;
                                },
                                id: 'updateBranch',
                                name: "Update branch",
                                cls: 'ot-command-branch-update',
                                action: function () {
                                    otStructureService.update(
                                        $scope.branch.links._update,
                                        "Update branch"
                                    ).then(loadBranch);
                                }
                            },
                            ot.viewActionsCommand($scope.branch.links._actions, []),
                            ot.viewCloseCommand('/project/' + $scope.branch.project.id),
                        ];
                        viewInitialized = true;
                    }
                })
                .finally(() => {
                    $scope.loadingBranch = false;
                });
        };

        // Pagination status
        const pagination = {
            offset: 0,
            size: 10,
        };

        /**
         * Loading the list of builds
         * @param reset True if the list of builds must be reset
         */
        const loadBuilds = (reset) => {
            $scope.loadingBuilds = true;
            const gqlVariables = {
                branchId,
                offset: pagination.offset,
                size: pagination.size,
            };
            otGraphqlService.pageGraphQLCall(gqlBuilds, gqlVariables)
                .then(data => {
                    const dataBranch = data.branches[0];
                    const dataBuilds = dataBranch.buildsPaginated;
                    $scope.buildsPageInfo = dataBuilds.pageInfo;
                    if (reset) {
                        $scope.builds = dataBuilds.pageItems;
                    } else {
                        $scope.builds.push(...dataBuilds.pageItems);
                    }
                })
                .finally(() => {
                    $scope.loadingBuilds = false;
                });
        };

        // Starts by loading the branch
        loadBranch();

        // Loading the builds AFTER the branch is loaded
        $scope.$watch('branch', (value) => {
            if (value) {
                loadBuilds(true);
            }
        });

        // Pagination: loading more builds
        $scope.loadMoreBuilds = () => {
            if ($scope.buildsPageInfo.nextPage) {
                pagination.offset = $scope.buildsPageInfo.nextPage.offset;
                pagination.size = $scope.buildsPageInfo.nextPage.size;
                loadBuilds(false);
            }
        };

        // Auto refresh management
        $scope.autoRefresh = localStorage.getItem('autoRefresh') === 'true';
        const refreshTaskName = 'Branch builds reloading';
        const refreshBuildView = () => {
            // Resetting the pagination
            pagination.offset = 0;
            pagination.size = 10;
            // Reloading
            loadBuilds(true);
        };
        $scope.$watch('autoRefresh', () => {
            if ($scope.autoRefresh) {
                // 1 minute interval
                otTaskService.register(refreshTaskName, refreshBuildView, 60 * 1000);
            } else {
                otTaskService.stop(refreshTaskName);
            }
        });
        $scope.toggleAutoRefresh = () => {
            $scope.autoRefresh = !$scope.autoRefresh;
            localStorage.setItem('autoRefresh', $scope.autoRefresh);
        };
    })
;