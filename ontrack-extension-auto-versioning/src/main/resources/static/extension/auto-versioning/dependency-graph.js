angular.module('ontrack.extension.auto-versioning.dependency-graph', [
    'ot.service.core',
    'ot.service.graphql'
])
    .config(function ($stateProvider) {
        $stateProvider.state('auto-versioning-dependency-graph', {
            url: '/extension/auto-versioning/dependency-graph/build/{buildId}',
            templateUrl: 'extension/auto-versioning/dependency-graph.tpl.html',
            controller: 'AutoVersioningDependencyGraphCtrl'
        });
    })

    .controller('AutoVersioningDependencyGraphCtrl', function ($stateParams, $scope,
                                                               ot, otGraphqlService) {
        $scope.rootBuildId = $stateParams.buildId;

        const view = ot.view();

        // GraphQL fragments
        const gqlBuildMinInfo = `
            fragment BuildMinInfo on Build {
                id
                name
                releaseProperty {
                    value
                }
                links {
                    _page
                }
                promotionRuns(lastPerLevel: true) {
                    promotionLevel {
                        name
                        image
                        _image
                    }
                }
            }
        `;

        const gqlBuildInfo = `
            fragment BuildInfo on Build {
                branch {
                    id
                    name
                    project {
                        id
                        name
                        links {
                            _page
                        }
                    }
                    links {
                        _page
                    }
                }
                ...BuildMinInfo
            }
            
            ${gqlBuildMinInfo}
        `;

        const gqlBuildDependencies = `
            fragment BuildDependencies on Build {
              using {
                pageItems {
                  ...BuildInfo
                  lastBuild: branch {
                    builds(count: 1) {
                      ...BuildMinInfo
                    }
                  }
                }
              }
            }
            ${gqlBuildInfo}
        `;

        // Loading the first node & initializing the view

        let viewInitialized = false;

        const loadRootNode = () => {
            return otGraphqlService.pageGraphQLCall(`
                query RootNode($rootBuildId: Int!) {
                    build(id: $rootBuildId) {
                        ...BuildInfo
                        ...BuildDependencies
                    }
                }
                ${gqlBuildDependencies}
            `, {rootBuildId: $scope.rootBuildId}
            ).then(data => {
                $scope.rootBuild = data.build;
                if (!viewInitialized) {
                    view.breadcrumbs = ot.buildBreadcrumbs($scope.rootBuild);
                    view.commands = [
                        ot.viewCloseCommand(`/build/${$scope.rootBuild.id}`)
                    ];
                    viewInitialized = true;
                }
                return data.build;
            });
        };

        // Given a build, creates a node & its descendants, for use inside the graph

        const transformData = async (build) => {
            // Initial node
            const node = {
                name: build.name,
                value: build.id
            };
            // Styling
            node.label = {
                rich: {
                    projectName: {
                        fontWeight: 'bold'
                    }
                }
            };
            // Format lines
            const formatterLines = [];

            // Project & branch name as a line
            formatterLines.push(`{projectName|${build.branch.project.name}}`);
            formatterLines.push(build.branch.name);
            // Display name is the build name unless there is a release property attached to the build
            let displayName = build.name;
            if (build.releaseProperty?.value?.name) {
                displayName = build.releaseProperty.value.name;
            }
            // TODO Promotions
            // Build line
            // TODO formatterLines.push(`${promotionsFormat}{decorationText|${displayName}}`);
            formatterLines.push(displayName);

            // Label formatter
            node.label.formatter = formatterLines.join('\n');
            // OK
            return node;
        };

        // Initializes the chart location

        const initChartEventHandling = (chart) => {
            // chart.on('dblclick', (params) => {
            // });
        };

        const createChart = () => {
            const graph = document.getElementById('graph');
            const chart = echarts.init(graph);
            // Event handling
            initChartEventHandling(chart);
            // OK
            return chart;
        };

        const getOrCreateChart = async () => {
            if (context.chart) {
                return context.chart;
            }
            context.chart = createChart();
            return context.chart;
        };

        // Chart options
        const options = {
            tooltip: {
                trigger: 'item',
                triggerOn: 'click',
                enterable: true,
                alwaysShowContent: true,
                show: false // Managed at data node level
            },
            series: [
                {
                    type: 'tree',
                    data: [],
                    top: '1%',
                    left: '20%',
                    bottom: '1%',
                    right: '20%',
                    symbol: 'none',
                    symbolSize: 1,
                    label: {
                        backgroundColor: 'white',
                        borderColor: '#CCCCCC',
                        borderWidth: 1,
                        padding: 5,
                        borderRadius: 5,
                        align: 'left',
                        position: 'inside',
                        verticalAlign: 'middle',
                        fontSize: 12,
                        rich: {
                            decorationText: {
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

        // Creates the chart option with some data
        const createOptionWithData = (data) => {
            options.series[0].data = [data];
        };

        // Overall context
        const context = {
            // Chart will be
            chart: undefined,
        }

        // Loading the graph
        const loadGraph = async (clear) => {
            if (context.chart && clear) {
                context.chart.showLoading();
            }
            $scope.loadingData = true;
            try {
                const rootBuild = await loadRootNode();
                const rootNode = await transformData(rootBuild);
                // Graph setup
                const chart = await getOrCreateChart();
                await createOptionWithData(rootNode);
                if (clear) {
                    chart.clear();
                }
                chart.setOption(options);
            } finally {
                $scope.loadingData = false;
                if (context.chart && clear) {
                    context.chart.hideLoading();
                }
            }
        };

        // Calling the loading of the graph on page startup
        loadGraph();
    })
;