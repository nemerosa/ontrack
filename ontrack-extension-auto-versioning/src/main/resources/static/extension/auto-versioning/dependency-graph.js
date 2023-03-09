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
                        links {
                            _image
                        }
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
                    lastBuild: builds(count: 1) {
                        ...BuildMinInfo
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
                  autoVersioning(buildId: $buildId) {
                    lastEligibleBuild {
                      ...BuildInfo
                    }
                    status {
                      order {
                        targetVersion
                      }
                      running
                      mostRecentState {
                        state
                        running
                        processing
                        creation {
                          time
                        }
                      }
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
                query RootNode($buildId: Int!) {
                    build(id: $buildId) {
                        ...BuildInfo
                        ...BuildDependencies
                    }
                }
                ${gqlBuildDependencies}
            `, {buildId: $scope.rootBuildId}
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

        // Loading a build node dependencies

        const loadBuildDependencies = (buildId) => {
            return otGraphqlService.pageGraphQLCall(`
                query BuildDependencies($buildId: Int!) {
                    build(id: $buildId) {
                        ...BuildDependencies
                    }
                }
                ${gqlBuildDependencies}
            `, {buildId}).then(data => {
                return data.build.using.pageItems;
            });
        };

        // Given a build, creates a node & its descendants, for use inside the graph

        const createDependencyNodes = (node, build) => {
            if (build.using && build.using.pageItems) {
                node.children = build.using.pageItems.map(childBuild => transformData(childBuild));
            }
        };

        const buildLine = (node, build, config) => {
            // Display name is the build name unless there is a release property attached to the build
            let displayName = build.name;
            if (build.releaseProperty && build.releaseProperty.value && build.releaseProperty.value.name) {
                displayName = build.releaseProperty.value.name;
            }
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
                        height: 12,
                        weight: 12
                    };
                    promotionsFormat += `{${promotion}|} `;
                }
            }
            // OK
            let line = '';
            if (config && config.prefix) {
                line += config.prefix;
            }
            line += promotionsFormat;
            if (config && config.main) {
                line += `{mainBuildName|${displayName}}`;
            } else {
                line += displayName;
            }
            return line;
        };

        const getAVStateImageUrl = (state) => {
            let icon;
            if (state.running) {
                if (state.processing) {
                    icon = 'processing';
                } else {
                    icon = 'running';
                }
            } else {
                icon = 'stopped';
            }
            return `/extension/auto-versioning/state/${icon}.png`;
        };

        const avStatus = (node, status) => {
            const className = status.mostRecentState.state;
            if (!node.label.rich[className]) {
                const imageUrl = getAVStateImageUrl(status.mostRecentState);
                node.label.rich[className] = {
                    backgroundColor: {
                        image: imageUrl
                    },
                    height: 12,
                    weight: 12
                };
            }
            return `AV Status: {${className}|} ${status.mostRecentState.state} - ${status.order.targetVersion}`;
        };

        const transformData = (build) => {

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
                    },
                    mainBuildName: {
                        fontWeight: 'bold'
                    }
                }
            };

            // Format lines
            const formatterLines = [];

            // Project & branch name as a line
            formatterLines.push(`{projectName|${build.branch.project.name}}`);
            formatterLines.push(build.branch.name);
            // Build line
            formatterLines.push(buildLine(node, build, {main: true}));

            // Last eligible build
            if (build.autoVersioning && build.autoVersioning.lastEligibleBuild) {
                formatterLines.push(buildLine(node, build.autoVersioning.lastEligibleBuild, {prefix: "Last eligible build: "}));
            }

            // Last build
            const lastBuilds = build.branch.lastBuild;
            if (lastBuilds && lastBuilds.length > 0) {
                const lastBuild = lastBuilds[0];
                if (lastBuild.id > build.id) {
                    formatterLines.push(buildLine(node, lastBuild, {prefix: "Last build: "}));
                }
            }

            // Last AV status
            if (build.autoVersioning && build.autoVersioning.status) {
                formatterLines.push(avStatus(node, build.autoVersioning.status));
            }

            // Label formatter
            node.label.formatter = formatterLines.join('\n');

            // Fill the dependencies
            createDependencyNodes(node, build);

            // OK
            return node;
        };

        // Looks for the node having buildId as a value

        const lookForNode = (node, buildId) => {
            if (node.value === buildId) {
                return node;
            } else if (node.children && node.children.length > 0) {
                let result = null;
                node.children.forEach(child => {
                    const childResult = lookForNode(child, buildId);
                    if (childResult) {
                        result = childResult;
                    }
                });
                return result;
            } else {
                return null;
            }
        };

        // Loading the children for a node

        const loadNodeDependencies = (buildId) => {
            // Looks for the node having the buildId as a value
            const node = lookForNode(options.series[0].data[0], buildId);
            if (node) {
                // Check if the children have been already loaded or not
                if (!node.childrenLoaded) {
                    // Loading the dependencies
                    loadBuildDependencies(buildId).then(builds => {
                        if (builds) {
                            node.children = builds.map(child => transformData(child));
                            // Refreshes the chart
                            getOrCreateChart().setOption(options);
                        }
                        node.childrenLoaded = true;
                    });
                }
            }
        };

        // Initializes the chart location

        const initChartEventHandling = (chart) => {
            chart.on('click', (params) => {
                const buildId = params.value;
                if (buildId) {
                    loadNodeDependencies(buildId);
                }
            });
        };

        const createChart = () => {
            const graph = document.getElementById('graph');
            const chart = echarts.init(graph);
            // Event handling
            initChartEventHandling(chart);
            // OK
            return chart;
        };

        const getOrCreateChart = () => {
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
                    labelLayout: {
                        align: 'left'
                    },
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
        };

        // Loading the graph
        const loadGraph = (clear) => {
            if (context.chart && clear) {
                context.chart.showLoading();
            }
            $scope.loadingData = true;
            loadRootNode().then(rootBuild => {
                const rootNode = transformData(rootBuild);
                // Graph setup
                const chart = getOrCreateChart();
                createOptionWithData(rootNode);
                if (clear) {
                    chart.clear();
                }
                chart.setOption(options);
            }).finally(() => {
                $scope.loadingData = false;
                if (context.chart && clear) {
                    context.chart.hideLoading();
                }
            });
        };

        // Calling the loading of the graph on page startup
        loadGraph();
    })
;