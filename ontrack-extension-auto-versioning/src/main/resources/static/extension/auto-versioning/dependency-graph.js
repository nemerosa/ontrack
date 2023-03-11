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

    .config(function ($stateProvider) {
        $stateProvider.state('auto-versioning-dependency-graph-branch', {
            url: '/extension/auto-versioning/dependency-graph/branch/{branchId}',
            templateUrl: 'extension/auto-versioning/dependency-graph-branch.tpl.html',
            controller: 'AutoVersioningDependencyGraphBranchCtrl'
        });
    })

    .service('otExtensionAutoVersioningDependencyGraph', function (otGraphqlService) {

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
                    buildDiffActions {
                      id
                      name
                      type
                      uri
                    }
                    links {
                        _page
                    }
                }
                ...BuildMinInfo
            }
            
            ${gqlBuildMinInfo}
        `;

        const gqlBuildNodeInfo = (autoVersioningArguments) => `
            fragment BuildNodeInfo on Build {
              ...BuildInfo
              lastBuildInfo: branch {
                lastBuild: builds(count: 1) {
                  ...BuildInfo
                }
              }
              autoVersioning(${autoVersioningArguments}) {
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
            ${gqlBuildInfo}
        `;

        const gqlBuildDependencies = (autoVersioningArguments) => `
            fragment BuildDependencies on Build {
              using {
                pageItems {
                  ...BuildNodeInfo
                }
              }
            }
            ${gqlBuildNodeInfo(autoVersioningArguments)}
        `;

        const self = {};

        /**
         * URL to the AV state icon
         */
        self.getAVStateImageUrl = (state) => {
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

        /**
         * Initializes the graph & the initial data
         * @param config.rootQuery Function which takes a GraphQL fragment and returns a GraphQL path to put under the query
         * @param config.rootBuild Given the data returns by the GraphQL query, returns the root build
         * @param config.rootVariables Variables to pass to the root query
         * @param config.autoVersioningArguments Arguments to pass to Build.autoVersioning (for the root query only)
         * @param config.onBuildSelected Method to call whenever a build is selected
         */
        self.loadRootNode = (config) => {
            const query = `
                ${
                    config.rootQuery(`
                        ...BuildNodeInfo
                        ...BuildDependencies
                    `)
                }
                ${gqlBuildDependencies(config.autoVersioningArguments)}
            `;

            // Overall context
            const context = {
                // Chart will be
                chart: undefined,
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
                            borderColor: '#777',
                            borderWidth: 1,
                            padding: 5,
                            borderRadius: 5,
                            align: 'left',
                            position: 'inside',
                            verticalAlign: 'middle',
                            fontSize: 12,
                            lineHeight: 12,
                            rich: {
                                hr : {
                                    borderColor: '#777',
                                    width: '100%',
                                    borderWidth: 0.5,
                                    height: 0
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

            // Loading a build node dependencies

            const loadBuildDependencies = (buildId) => {
                return otGraphqlService.pageGraphQLCall(`
                    query BuildDependencies($buildId: Int!) {
                        build(id: $buildId) {
                            ...BuildDependencies
                        }
                    }
                    ${gqlBuildDependencies('buildId: $buildId')}
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

            const avStatus = (node, status) => {
                const className = status.mostRecentState.state;
                if (!node.label.rich[className]) {
                    const imageUrl = self.getAVStateImageUrl(status.mostRecentState);
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
                    build: build,
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

                // Function to include a separator
                const separator = () => {
                    formatterLines.push('{hr|}');
                };

                // Project & branch name as a line
                formatterLines.push(`{projectName|${build.branch.project.name}}`);
                formatterLines.push(build.branch.name);
                // Build line
                formatterLines.push(buildLine(node, build, {main: true}));

                // Last eligible build
                if (build.autoVersioning && build.autoVersioning.lastEligibleBuild) {
                    separator();
                    formatterLines.push(buildLine(node, build.autoVersioning.lastEligibleBuild, {prefix: "Last eligible build: "}));
                }

                // Last build
                const lastBuilds = build.lastBuildInfo.lastBuild;
                if (lastBuilds && lastBuilds.length > 0) {
                    const lastBuild = lastBuilds[0];
                    if (lastBuild.id > build.id) {
                        separator();
                        formatterLines.push(buildLine(node, lastBuild, {prefix: "Last build: "}));
                    }
                }

                // Last AV status
                if (build.autoVersioning && build.autoVersioning.status) {
                    separator();
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
                    // Returning the build attached to the node
                    return node.build;
                } else {
                    return null;
                }
            };

            // Initializes the chart location

            const initChartEventHandling = (chart) => {
                chart.on('click', (params) => {
                    const buildId = params.value;
                    if (buildId) {
                        const build = loadNodeDependencies(buildId);
                        if (build && config.onBuildSelected) {
                            config.onBuildSelected(angular.copy(build));
                        }
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

            // Creates the chart option with some data
            const createOptionWithData = (data) => {
                options.series[0].data = [data];
            };

            return otGraphqlService.pageGraphQLCall(
                query,
                config.rootVariables
            ).then(data => {
                const rootBuild = config.rootBuild(data);
                const rootNode = transformData(rootBuild);
                // Graph setup
                const chart = getOrCreateChart();
                createOptionWithData(rootNode);
                chart.setOption(options);
                return rootBuild;
            });
        };

        return self;
    })

    .controller('AutoVersioningDependencyGraphBranchCtrl', function ($stateParams, $scope,
                                                               ot, otGraphqlService,
                                                               otExtensionAutoVersioningDependencyGraph) {
        $scope.branchId = $stateParams.branchId;

        const view = ot.view();

        // Loading the first node & initializing the view

        let viewInitialized = false;

        otExtensionAutoVersioningDependencyGraph.loadRootNode({
            rootQuery: (fragment) => {
                return `
                    query RootBuild($branchId: Int!) {
                        branches(id: $branchId) {
                            builds(count: 1) {
                                ${fragment}
                            }
                        }
                    }
                `;
            },
            rootVariables: {branchId: $scope.branchId},
            rootBuild: (data) => data.branches[0].builds[0],
            autoVersioningArguments: 'branchId: $branchId',
            onBuildSelected: (build) => {
                $scope.selectedBuild = build;
            }
        }).then(rootBuild => {
            $scope.rootBuild = rootBuild;
            if (!viewInitialized) {
                view.breadcrumbs = ot.branchBreadcrumbs($scope.rootBuild.branch);
                view.commands = [
                    ot.viewCloseCommand(`/branch/${$scope.rootBuild.branch.id}`)
                ];
                viewInitialized = true;
            }
        });

    })

    .directive('otAutoVersioningDependencyGraphSelectedBuild', function () {
        return {
            restrict: 'E',
            templateUrl: 'extension/auto-versioning/directive.dependency-graph-selected-build.tpl.html',
            scope: {
                selectedBuild: '='
            },
            controller: function ($scope, otExtensionAutoVersioningDependencyGraph) {
                $scope.avStatusIconUrl = (state) => otExtensionAutoVersioningDependencyGraph.getAVStateImageUrl(state);
            }
        };
    })

    .directive('otAutoVersioningDependencyGraphBuildLinks', function () {
        return {
            restrict: 'E',
            templateUrl: 'extension/auto-versioning/directive.dependency-graph-build-links.tpl.html',
            scope: {
                name: '@',
                refBuild: '=',
                build: '='
            },
            controller: function ($scope, $state) {
                $scope.buildDiff = action => {
                    if ($scope.refBuild) {
                        $state.go(action.id, {
                            branch: $scope.build.branch.id,
                            from: $scope.refBuild.id,
                            to: $scope.build.id
                        });
                    }
                };
            }
        };
    })

    .controller('AutoVersioningDependencyGraphCtrl', function ($stateParams, $scope,
                                                               ot, otGraphqlService,
                                                               otExtensionAutoVersioningDependencyGraph) {
        $scope.rootBuildId = $stateParams.buildId;

        const view = ot.view();

        // Build selection

        $scope.selectedBuild = undefined;

        // Loading the first node & initializing the view

        let viewInitialized = false;

        otExtensionAutoVersioningDependencyGraph.loadRootNode({
            rootQuery: (fragment) => {
                return `
                    query RootBuild($buildId: Int!) {
                        build(id: $buildId) {
                            ${fragment}
                        }
                    }
                `;
            },
            rootVariables: {buildId: $scope.rootBuildId},
            rootBuild: (data) => data.build,
            autoVersioningArguments: 'buildId: $buildId',
            onBuildSelected: (build) => {
                $scope.$apply(function () {
                    $scope.selectedBuild = build;
                });
            }
        }).then(rootBuild => {
            $scope.rootBuild = rootBuild;
            if (!viewInitialized) {
                view.breadcrumbs = ot.buildBreadcrumbs($scope.rootBuild);
                view.commands = [
                    ot.viewCloseCommand(`/build/${$scope.rootBuild.id}`)
                ];
                viewInitialized = true;
            }
        });
    })
;