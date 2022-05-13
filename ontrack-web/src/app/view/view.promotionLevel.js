angular.module('ot.view.promotionLevel', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure',
    'ot.service.graphql',
    'ot.service.chart'
])
    .config(function ($stateProvider) {
        $stateProvider.state('promotionLevel', {
            url: '/promotionLevel/{promotionLevelId}',
            templateUrl: 'app/view/view.promotionLevel.tpl.html',
            controller: 'PromotionLevelCtrl'
        });
    })
    .controller('PromotionLevelCtrl', function ($state, $scope, $stateParams, $http, ot, otChartService, otStructureService, otAlertService, otGraphqlService) {
        const view = ot.view();
        // PromotionLevel's id
        const promotionLevelId = $stateParams.promotionLevelId;
        // GraphQL query
        const query = `query PromotionLevel($id: Int!, $offset: Int!, $size: Int!, $name: String, $version: String, $afterDate: LocalDateTime, $beforeDate: LocalDateTime) {
            promotionLevel(id: $id) {
                id
                name
                description
                annotatedDescription
                image
                _image
                promotionRuns: promotionRunsPaginated(offset: $offset, size: $size, name: $name, version: $version, afterDate: $afterDate, beforeDate: $beforeDate) {
                    pageInfo {
                        totalSize
                        currentOffset
                        currentSize
                        previousPage {
                            offset
                            size
                        }
                        nextPage {
                            offset
                            size
                        }
                        pageIndex
                        pageTotal
                    }
                    pageItems {
                        description
                        annotatedDescription
                        build {
                            name
                            links {
                                _page
                            }
                            decorations {
                              decorationType
                              error
                              data
                              feature {
                                id
                              }
                            }
                        }
                        creation {
                            user
                            time
                        }
                    }
                }
                decorations {
                    decorationType
                    data
                    error
                    feature {
                      id
                    }
                }
                branch {
                    id
                    name
                    project {
                        id
                        name
                    }
                }
                links {
                    _self
                    _update
                    _delete
                    _bulkUpdate
                    _runs
                    _properties
                    _events
                    _actions
                }
            }
        }`;

        // Query variables
        const pageSize = 20;
        const queryVariables = {
            id: promotionLevelId,
            offset: 0,
            size: pageSize,
            name: null,
            version: null,
            afterDate: null,
            beforeDate: null
        };

        // Filter
        $scope.filter = {
            name: '',
            version: '',
            afterDate: null,
            beforeDate: null
        };

        $scope.loadingPromotionLevel = true;
        let viewInitialized = false;

        // Loading the promotion level
        function loadPromotionLevel() {
            $scope.loadingPromotionLevel = true;

            if ($scope.filter.name) {
                queryVariables.name = $scope.filter.name;
            } else {
                queryVariables.name = null;
            }

            if ($scope.filter.version) {
                queryVariables.version = $scope.filter.version;
            } else {
                queryVariables.version = null;
            }

            queryVariables.afterDate = $scope.filter.afterDate;
            queryVariables.beforeDate = $scope.filter.beforeDate;

            otGraphqlService.pageGraphQLCall(query, queryVariables).then((data) => {
                let promotionLevel = data.promotionLevel;
                $scope.promotionLevel = promotionLevel;
                if (!viewInitialized) {
                    // View breadcrumbs
                    view.breadcrumbs = ot.branchBreadcrumbs(promotionLevel.branch);
                    // Commands
                    view.commands = [
                        {
                            condition: function () {
                                return promotionLevel.links._update;
                            },
                            id: 'updatePromotionLevelImage',
                            name: "Change image",
                            cls: 'ot-command-promotion-level-image',
                            action: changeImage
                        },
                        {
                            condition: function () {
                                return promotionLevel.links._update;
                            },
                            id: 'updatePromotionLevel',
                            name: "Update promotion level",
                            cls: 'ot-command-promotion-level-update',
                            action: function () {
                                otStructureService.update(
                                    promotionLevel.links._update,
                                    "Update promotion level"
                                ).then(loadPromotionLevel);
                            }
                        },
                        {
                            condition: function () {
                                return promotionLevel.links._delete;
                            },
                            id: 'deletePromotionLevel',
                            name: "Delete promotion level",
                            cls: 'ot-command-promotion-level-delete',
                            action: function () {
                                otAlertService.confirm({
                                    title: "Deleting a promotion level",
                                    message: "Do you really want to delete the promotion level " + promotionLevel.name +
                                        " and all its associated data?"
                                }).then(function () {
                                    return ot.call($http.delete(promotionLevel.links._delete));
                                }).then(function () {
                                    $state.go('branch', {branchId: promotionLevel.branch.id});
                                });
                            }
                        },
                        {
                            condition: function () {
                                return promotionLevel.links._bulkUpdate;
                            },
                            id: 'bulkUpdatePromotionLevel',
                            name: "Bulk update",
                            cls: 'ot-command-update',
                            action: function () {
                                otAlertService.confirm({
                                    title: "Promotion levels bulk update",
                                    message: "Updates all other promotion levels with the same name?"
                                }).then(function () {
                                    return ot.call($http.put(promotionLevel.links._bulkUpdate, {}));
                                }).then(loadPromotionLevel);
                            }
                        },
                        ot.viewActionsCommand($scope.promotionLevel.links._actions),
                        ot.viewCloseCommand('/branch/' + $scope.promotionLevel.branch.id)
                    ];
                    viewInitialized = true;
                }
            }).finally(() => {
                $scope.loadingPromotionLevel = false;
            });
        }

        // Initialisation
        loadPromotionLevel();

        // Clears the filter
        $scope.onClearFilter = () => {
            $scope.filter.name = '';
            $scope.filter.version = '';
            $scope.filter.afterDate = null;
            $scope.filter.beforeDate = null;
            queryVariables.offset = 0;
            queryVariables.size = pageSize;
            loadPromotionLevel();
        };

        // Applies the filter
        $scope.onApplyFilter = () => {
            queryVariables.offset = 0;
            queryVariables.size = pageSize;
            loadPromotionLevel();
        };

        // Changing the image
        function changeImage() {
            otStructureService.changePromotionLevelImage($scope.promotionLevel).then(loadPromotionLevel);
        }

        // Switching the page
        $scope.switchPage = (pageRequest) => {
            queryVariables.offset = pageRequest.offset;
            queryVariables.size = pageSize;
            loadPromotionLevel();
        };

        // Shared options
        $scope.chartOptions = otChartService.loadChartOptions("promotion-level-charts", {
            interval: "1y",
            period: "1w"
        });

        // Lead time chart
        $scope.leadTimeChart = otChartService.createDurationChart({
            title: "Lead time to promotion",
            chartOptionsKey: "promotion-level-charts",
            chartOptions: $scope.chartOptions,
            query: (chartOptions) => {
                return `query PromotionLevelLeadTimeChart {
                        getChart(input: {
                            name: "promotion-level-lead-time",
                            options: {
                                interval: "${chartOptions.interval}",
                                period: "${chartOptions.period}"
                            },
                            parameters: {
                                id: ${promotionLevelId},
                            }
                        })
                    }`;
            }
        });

    })
;