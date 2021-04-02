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
                    project {
                      id
                      name
                    }
                  }
                  releaseProperty {
                    value
                  }
                }
            `;

            const context = {
                // using or usedBy, depending on the direction we want to follow
                direction: 'using',
                // the chart, not initialized at first
                chart: undefined
            };
            $scope.context = context;

            // Changing the direction of the dependencies
            $scope.changeDirection = (direction) => {
                if (direction !== context.direction) {
                    context.direction = direction;
                    refreshGraph();
                }
            };

            // Refreshes the chart
            const refreshGraph = () => {
                loadData().then(raw => {
                    // View setup
                    const build = raw.builds[0];
                    view.title = `Build links for ${build.name}`;
                    view.breadcrumbs = ot.buildBreadcrumbs(build);
                    view.commands = [
                        ot.viewCloseCommand('/build/' + build.id)
                    ];
                    // Graph preparation
                    const data = transformData(raw, context.direction);
                    setGraphData(data);
                });
            };

            // Sets the data into the graph
            const setGraphData = (data) => {
                const chart = getOrCreateChart();
                chart.showLoading();
                const options = createOptionWithData(data);
                chart.setOption(options);
                chart.hideLoading();
            };

            // Creates the chart option with some data
            const createOptionWithData = (data) => {
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
                            expandAndCollapse: false,
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
                chart.on('click', (params) => {
                    if (params.value && params.value.links && params.value.links._page) {
                        $window.open(params.value.links._page, "_blank");
                    }
                });
            };

            // Transforming the raw data into a graph
            const transformData = (raw, direction) => {
                const build = raw.builds[0];
                return transformBuildIntoNode(build, direction);
            };

            // Transforming a `Build` into a node
            const transformBuildIntoNode = (build, direction) => {
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
                if (build.promotionRuns) {
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
                // Children
                const children = build[direction];
                if (children && children.pageItems) {
                    node.children = children.pageItems.map((child) => transformBuildIntoNode(child, direction));
                }
                // OK
                return node;
            };

            // Loading the raw data
            const loadData = () => {
                if (context.direction === 'using') {
                    return loadUsingData();
                } else {
                    throw 'Used by direction not supported yet';
                }
            };

            // Loading the raw data in the `using` direction
            const loadUsingData = () => {
                return otGraphqlService.pageGraphQLCall(usingQuery, {buildId: buildId});
            };

            // Loading the data on load
            refreshGraph();
        }
    )
;