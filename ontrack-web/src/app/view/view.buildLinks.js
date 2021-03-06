angular.module('ot.view.buildLinks', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure',
    'ot.service.graphql'
])
    .config(function ($stateProvider) {
        $stateProvider.state('buildLinks', {
            url: '/build/{buildId}/links',
            templateUrl: 'app/view/view.buildLinks.tpl.html',
            controller: 'BuildLinksCtrl'
        });
    })
    .controller('BuildLinksCtrl', function ($location, $scope, $stateParams, $window, ot, otGraphqlService) {
            const buildId = $stateParams.buildId;
            const view = ot.view();

            const buildFragment = `
                fragment buildInfo on Build {
                  id
                  name
                  links {
                    _page
                  }
                  promotionRuns(lastPerLevel: true) {
                    promotionLevel {
                      name
                      links {
                        _image
                      }
                    }
                  }
                  branch {
                    id
                    name
                    links {
                      _page
                    }
                    project {
                      id
                      name
                      links {
                        _page
                      }
                    }
                  }
                  releaseProperty {
                    value
                  }
                }
            `;

            const usingQuery = `
                query Downstream($buildId: Int!) {
                  builds(id: $buildId) {
                    ...buildInfo
                    using {
                      pageItems {
                        ...buildInfo
                        using {
                          pageItems {
                            ...buildInfo
                            using {
                              pageItems {
                                ...buildInfo
                                using {
                                  pageItems {
                                    ...buildInfo
                                    using {
                                      pageItems {
                                        ...buildInfo
                                        using {
                                          pageItems {
                                            ...buildInfo
                                            using {
                                              pageItems {
                                                ...buildInfo
                                                using {
                                                  pageItems {
                                                    ...buildInfo
                                                    using {
                                                      pageItems {
                                                        ...buildInfo
                                                      }
                                                    }
                                                  }
                                                }
                                              }
                                            }
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
                
                ${buildFragment}
            `;

            const usedByQuery = `
                query Upstream($buildId: Int!, $size: Int!) {
                  builds(id: $buildId) {
                    ...buildInfo
                    usedBy(size: $size) {
                      pageItems {
                        ...buildInfo
                        usedBy(size: $size) {
                          pageItems {
                            ...buildInfo
                            usedBy(size: $size) {
                              pageItems {
                                ...buildInfo
                                usedBy(size: $size) {
                                  pageItems {
                                    ...buildInfo
                                    usedBy(size: $size) {
                                      pageItems {
                                        ...buildInfo
                                        usedBy(size: $size) {
                                          pageItems {
                                            ...buildInfo
                                            usedBy(size: $size) {
                                              pageItems {
                                                ...buildInfo
                                                usedBy(size: $size) {
                                                  pageItems {
                                                    ...buildInfo
                                                    usedBy(size: $size) {
                                                      pageItems {
                                                        ...buildInfo
                                                      }
                                                    }
                                                  }
                                                }
                                              }
                                            }
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
                
                ${buildFragment}
            `;

            const parameter = $location.hash();
            let initialDirection = 'using';
            if (parameter === 'usedBy') {
                initialDirection = 'usedBy';
            }

            const context = {
                // using or usedBy, depending on the direction we want to follow
                direction: initialDirection,
                // the chart, not initialized at first
                chart: undefined
            };
            $scope.context = context;

            // Loading indicator
            $scope.loadingData = false;

            // Changing the direction of the dependencies
            $scope.changeDirection = () => {
                if (context.direction === 'using') {
                    context.direction = 'usedBy';
                } else {
                    context.direction = 'using';
                }
                $location.hash(context.direction);
                refreshGraph();
            };

            // Refreshes the chart
            const refreshGraph = () => {
                if (context.chart) {
                    context.chart.showLoading();
                }
                $scope.loadingData = true;
                loadData().then(raw => {
                    try {
                        // View setup
                        if (!$scope.build) {
                            const build = raw.builds[0];
                            view.title = '';
                            view.breadcrumbs = ot.buildBreadcrumbs(build);
                            view.commands = [
                                ot.viewCloseCommand('/build/' + build.id)
                            ];
                            $scope.build = build;
                        }
                        // Graph data preparation
                        const data = transformData(raw, context.direction);
                        // Graph setup
                        const chart = getOrCreateChart();
                        const options = createOptionWithData(data, context.direction);
                        chart.clear();
                        chart.setOption(options, true);
                    } finally {
                        $scope.loadingData = false;
                        if (context.chart) {
                            context.chart.hideLoading();
                        }
                    }
                });
            };

            // Creates the chart option with some data
            const createOptionWithData = (data, direction) => {
                return {
                    series: [
                        {
                            type: 'tree',
                            data: [data],
                            top: '1%',
                            left: '20%',
                            bottom: '1%',
                            right: '20%',
                            symbolSize: 25,
                            label: {
                                position: 'top',
                                verticalAlign: 'middle',
                                align: 'center',
                                fontSize: 12,
                                rich: {
                                    buildName: {
                                        padding: [0, 0, 0, 8]
                                    }
                                }
                            },
                            leaves: {
                                label: {
                                    position: 'top',
                                    verticalAlign: 'middle',
                                    align: 'center'
                                }
                            },
                            emphasis: {
                                focus: 'ancestor'
                            },
                            expandAndCollapse: (direction === 'usedBy'),
                            animationDuration: 550,
                            animationDurationUpdate: 750
                        }
                    ]
                };
            };

            // Gets the chart or creates it
            const getOrCreateChart = () => {
                if (context.chart) {
                    return context.chart;
                }
                context.chart = createChart();
                return context.chart;
            };

            // Creates & inits the chart
            const createChart = () => {
                const graph = document.getElementById('graph');
                const chart = echarts.init(graph);
                // Event handling
                initChartEventHandling(chart);
                // OK
                return chart;
            };

            // Event handling on the chart
            const initChartEventHandling = (chart) => {
                chart.on('dblclick', (params) => {
                    if (params.value && params.value.links && params.value.links._page) {
                        $window.open(params.value.links._page, "_blank");
                    }
                });
            };

            // Transforming the raw data into a graph
            const transformData = (raw, direction) => {
                const build = raw.builds[0];
                return transformBuildIntoNode(build, direction, 0);
            };

            // Transforming a `Build` into a node
            const transformBuildIntoNode = (build, direction, depth) => {
                // Display name
                let displayName = build.name;
                if (build.releaseProperty.value && build.releaseProperty.value.name) {
                    displayName = build.releaseProperty.value.name;
                }
                // Initial node
                const node = {
                    name: build.name,
                    value: build
                };
                // Label
                node.label = {
                    rich: {}
                };
                // Promotions
                let promotionsFormat = '';
                if (build.promotionRuns && build.promotionRuns.length > 0) {
                    const run = build.promotionRuns[build.promotionRuns.length - 1];
                    const promotion = run.promotionLevel.name;
                    const image = run.promotionLevel.links._image;
                    if (image) {
                        node.label.rich[promotion] = {
                            backgroundColor: {
                                image: image
                            },
                            height: 16,
                            weight: 16
                        };
                        promotionsFormat += `{${promotion}|}`;
                    }
                }
                // Label formatter
                node.label.formatter = [
                    build.branch.project.name,
                    `${promotionsFormat}{buildName|${displayName}}`
                ].join('\n');
                // For direction = usedBy, collapse nodes when depth > 0
                if (direction === 'usedBy' && depth > 0) {
                    node.collapsed = true;
                }
                // Children
                const children = build[direction];
                if (children && children.pageItems) {
                    node.children = children.pageItems.map((child) => transformBuildIntoNode(child, direction, depth + 1));
                }
                // OK
                return node;
            };

            // Loading the raw data
            const loadData = () => {
                if (context.direction === 'using') {
                    return loadUsingData();
                } else {
                    return loadUsedByData();
                }
            };

            // Loading the raw data in the `using` direction
            const loadUsingData = () => {
                return otGraphqlService.pageGraphQLCall(usingQuery, {buildId: buildId});
            };

            // Loading the raw data in the `usedBy` direction
            const loadUsedByData = () => {
                return otGraphqlService.pageGraphQLCall(usedByQuery, {
                    buildId: buildId,
                    size: 5 // Limit the history of "used by" links
                });
            };

            // Loading the data on load
            refreshGraph();
        }
    )
;