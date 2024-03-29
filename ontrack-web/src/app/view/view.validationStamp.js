angular.module('ot.view.validationStamp', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure',
    'ot.service.graphql',
    'ot.service.chart'
])
    .config(function ($stateProvider) {
        $stateProvider.state('validationStamp', {
            url: '/validationStamp/{validationStampId}',
            templateUrl: 'app/view/view.validationStamp.tpl.html',
            controller: 'ValidationStampCtrl'
        });
    })
    .controller('ValidationStampCtrl', function ($q, $state, $scope, $stateParams, $http, ot, otChartService, otStructureService, otAlertService, otGraphqlService) {
        const view = ot.view();
        let viewInitialised = false;
        // ValidationStamp's id
        const validationStampId = $stateParams.validationStampId;

        // Initial query parameters
        const pageSize = 20;
        const queryVariables = {
            validationStampId: validationStampId,
            offset: 0,
            size: pageSize
        };

        // Range of validation runs
        $scope.selectedValidationRuns = {};

        // Query for the validation stamp
        const query = `
            query PaginatedValidationRuns($validationStampId: Int!, $offset: Int = 0, $size: Int = 20) {
                validationStamp(id: $validationStampId) {
                    id
                    name
                    description
                    annotatedDescription
                    image
                    _image
                    decorations {
                      decorationType
                      data
                      error
                      feature {
                        id
                      }
                    }
                    dataType {
                      descriptor {
                        id
                        displayName
                        feature {
                          id
                        }
                      }
                      config
                    }
                    charts {
                        id
                        title
                        type
                        config
                        parameters
                    }
                    branch {
                      id
                      name
                      project {
                        id
                        name
                      }
                      buildDiffActions {
                        id
                        name
                        type
                        uri
                      }
                    }
                    links {
                      _self
                      _bulkUpdate
                      _update
                      _delete
                      _properties
                      _events
                      _actions
                    }
                    validationRunsPaginated(offset: $offset, size: $size) {
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
                        id
                        runOrder
                        build {
                          id
                          name
                        }
                        creation {
                          user
                          time
                        }
                        runInfo {
                          sourceType
                          sourceUri
                          triggerType
                          triggerData
                          runTime
                        }
                        data {
                          descriptor {
                            id
                            feature {
                              id
                            }
                          }
                          data
                        }
                        validationRunStatuses {
                          creation {
                            user
                            time
                          }
                          statusID {
                            id
                            name
                          }
                          description
                          annotatedDescription
                        }
                      }
                    }
                }
            }`;

        // Loading the validation stamp
        function loadValidationStamp() {
            otGraphqlService.pageGraphQLCall(query, queryVariables).then(function (data) {
                const validationStamp = data.validationStamp;
                $scope.validationStamp = validationStamp;
                $scope.charts = [];
                // View setup
                if (!viewInitialised) {
                    // View title
                    view.breadcrumbs = ot.branchBreadcrumbs(validationStamp.branch);
                    // Commands
                    view.commands = [
                        {
                            condition: function () {
                                return validationStamp.links._update;
                            },
                            id: 'updateValidationStampImage',
                            name: "Change image",
                            cls: 'ot-command-validation-stamp-image',
                            action: changeImage
                        },
                        {
                            condition: function () {
                                return validationStamp.links._update;
                            },
                            id: 'updateValidationStamp',
                            name: "Update validation stamp",
                            cls: 'ot-command-validation-stamp-update',
                            action: function () {
                                otStructureService.update(
                                    validationStamp.links._update,
                                    "Update validation stamp"
                                ).then(loadValidationStamp);
                            }
                        },
                        {
                            condition: function () {
                                return validationStamp.links._delete;
                            },
                            id: 'deleteValidationStamp',
                            name: "Delete validation stamp",
                            cls: 'ot-command-validation-stamp-delete',
                            action: function () {
                                otAlertService.confirm({
                                    title: "Deleting a validation stamp",
                                    message: "Do you really want to delete the validation stamp " + validationStamp.name +
                                    " and all its associated data?"
                                }).then(function () {
                                    return ot.call($http.delete(validationStamp.links._delete));
                                }).then(function () {
                                    $state.go('branch', {branchId: validationStamp.branch.id});
                                });
                            }
                        },
                        {
                            condition: function () {
                                return validationStamp.links._bulkUpdate;
                            },
                            id: 'bulkUpdateValidationStamp',
                            name: "Bulk update",
                            cls: 'ot-command-update',
                            action: function () {
                                otAlertService.confirm({
                                    title: "Validation stamps bulk update",
                                    message: "Updates all other validation stamps with the same name?"
                                }).then(function () {
                                    return ot.call($http.put(validationStamp.links._bulkUpdate, {}));
                                }).then(loadValidationStamp);
                            }
                        },
                        ot.viewActionsCommand($scope.validationStamp.links._actions),
                        ot.viewCloseCommand('/branch/' + $scope.validationStamp.branch.id)
                    ];

                    // General chart options
                    $scope.chartOptions = otChartService.loadChartOptions("validation-stamp-charts", {
                        interval: '1y',
                        period: '1w'
                    });

                    // Initialization of the charts
                    validationStamp.charts.forEach(chart => {
                        $scope.charts.push({
                            id: chart.id,
                            title: chart.title,
                            chart: otChartService.createGenericChart({
                                chartType: chart.type,
                                chartConfig: chart.config,
                                chartOptionsKey: "validation-stamp-charts",
                                chartOptions: $scope.chartOptions,
                                query: (chartOptions) => {
                                    return `
                                        query Chart(
                                            $parameters: JSON!,
                                        ) {
                                            getChart(input: {
                                                name: "${chart.id}",
                                                options: {
                                                    interval: "${chartOptions.interval}",
                                                    period: "${chartOptions.period}"
                                                },
                                                parameters: $parameters
                                            })
                                        }
                                    `;
                                },
                                queryVariables: (chartOptions) => {
                                    return {
                                        parameters: chart.parameters
                                    };
                                }
                            })
                        });
                    });

                    // View OK now
                    viewInitialised = true;
                }
            });
        }

        // Initialisation
        loadValidationStamp();

        // Changing the image
        function changeImage() {
            otStructureService.changeValidationStampImage($scope.validationStamp).then(loadValidationStamp);
        }

        // Switching the page
        $scope.switchPage = function (pageRequest) {
            $scope.selectedValidationRuns.first = undefined;
            $scope.selectedValidationRuns.second = undefined;
            queryVariables.offset = pageRequest.offset;
            queryVariables.size = pageSize;
            loadValidationStamp();
        };

        // Diff between two validation runs
        $scope.validationRunDiff = function (action) {
            if ($scope.selectedValidationRuns.first && $scope.selectedValidationRuns.second) {
                $state.go(action.id, {
                    branch: $scope.validationStamp.branch.id,
                    from: $scope.selectedValidationRuns.first.build.id,
                    to: $scope.selectedValidationRuns.second.build.id
                });
            }
        };

        $scope.chartColSize = () => {
            const chartCount = $scope.validationStamp.charts.length;
            if (chartCount <= 3) {
                return 4;
            } else {
                return 6;
            }
        };

    })
;