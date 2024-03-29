angular.module('ontrack.extension.auto-versioning.dependency-graph', [
    'ot.service.core',
    'ot.service.graphql',
    'ot.service.task'
])
    .config(function ($stateProvider) {
        $stateProvider.state('auto-versioning-dependency-graph', {
            url: '/extension/auto-versioning/dependency-graph/build/{buildId}/downstream',
            templateUrl: 'extension/auto-versioning/dependency-graph.tpl.html',
            controller: 'AutoVersioningDependencyGraphCtrl'
        });
    })

    .config(function ($stateProvider) {
        $stateProvider.state('auto-versioning-dependency-graph-branch', {
            url: '/extension/auto-versioning/dependency-graph/branch/{branchId}/downstream',
            templateUrl: 'extension/auto-versioning/dependency-graph-branch.tpl.html',
            controller: 'AutoVersioningDependencyGraphBranchCtrl'
        });
    })

    .config(function ($stateProvider) {
        $stateProvider.state('auto-versioning-dependency-graph-upstream', {
            url: '/extension/auto-versioning/dependency-graph/build/{buildId}/upstream',
            templateUrl: 'extension/auto-versioning/dependency-graph-upstream.tpl.html',
            controller: 'AutoVersioningDependencyGraphUpstreamCtrl'
        });
    })

    .config(function ($stateProvider) {
        $stateProvider.state('auto-versioning-dependency-graph-branch-upstream', {
            url: '/extension/auto-versioning/dependency-graph/branch/{branchId}/upstream',
            templateUrl: 'extension/auto-versioning/dependency-graph-branch-upstream.tpl.html',
            controller: 'AutoVersioningDependencyGraphBranchUpstreamCtrl'
        });
    })

    .service('otExtensionAutoVersioningDependencyGraph', function ($q, otGraphqlService) {

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

        const gqlBuildNodeInfo = (autoVersioningArguments, direction) => `
            fragment BuildNodeInfo on Build {
              ...BuildInfo
              lastBuildInfo: branch {
                lastBuild: builds(count: 1) {
                  ...BuildInfo
                }
              }
              previousBuild {
                  ...BuildInfo
              }
              nextBuild {
                  ...BuildInfo
              }
              autoVersioning(${autoVersioningArguments}, direction: ${direction}) {
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

        const gqlBuildDependencies = (autoVersioningArguments, direction) => {
            let dependencyDirection = 'usingQualified';
            if (direction !== 'DOWN') {
                dependencyDirection = 'usedByQualified';
            }
            return `
                fragment BuildDependencies on Build {
                  ${dependencyDirection} {
                    pageItems {
                        qualifier
                        build {
                            ...BuildNodeInfo
                        }
                    }
                  }
                }
                ${gqlBuildNodeInfo(autoVersioningArguments, direction)}
            `;
        };

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
         * Given a build, returns its last build if available.
         */
        self.getLastBuild = (build) => {
            const lastBuilds = build.lastBuildInfo.lastBuild;
            if (lastBuilds && lastBuilds.length > 0) {
                return lastBuilds[0];
            } else {
                return null;
            }
        };

        /**
         * Initializes a graph
         * @param config.rootQuery Function which takes a GraphQL fragment and returns a GraphQL path to put under the query
         * @param config.rootBuild Given the data returns by the GraphQL query, returns the root build
         * @param config.rootVariables Variables to pass to the root query
         * @param config.autoVersioningArguments Arguments to pass to Build.autoVersioning (for the root query only)
         * @param config.onBuildSelected Method to call whenever a build is selected
         * @param config.direction DOWN or UP
         * @param config.layout.elements Layout options for the elements
         */
        self.createGraph = (config) => {

            const query = `
                ${
                    config.rootQuery(`
                        ...BuildNodeInfo
                        ...BuildDependencies
                    `)
                }
                ${gqlBuildDependencies(config.autoVersioningArguments, config.direction)}
            `;

            // Overall context
            const context = {
                // Chart will be
                chart: undefined,
                // Selected node
                selectedNode: undefined,
                // Links build ID --> parent node
                parents: {},
                // Index (build IDs --> Boolean) of nodes having been expanded
                expansionIndex: {}
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
                        left: '1%',
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
                        expandAndCollapse: false
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
                    ${gqlBuildDependencies('buildId: $buildId', config.direction)}
                `, {buildId}).then(data => {
                    if (config.direction === 'DOWN') {
                        return data.build.usingQualified.pageItems.map(link => {
                            const build = link.build;
                            build.qualifier = link.qualifier;
                            return build;
                        });
                    } else {
                        return data.build.usedByQualified.pageItems.map(link => {
                            const build = link.build;
                            build.qualifier = link.qualifier;
                            return build;
                        });
                    }
                });
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

            const transformData = (build, parentNode) => {

                // Initial node
                const node = {
                    build: build,
                    name: build.name,
                    value: build.id
                };

                // Links build ID --> parent node
                context.parents[build.id] = parentNode;

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

                // Project name as the first line
                let projectName = build.branch.project.name;
                // Qualifier if any is appended to the display name
                if (build.qualifier) {
                    projectName += ` [${build.qualifier}]`;
                }
                formatterLines.push(`{projectName|${projectName}}`);
                // Branch name as a line
                formatterLines.push(build.branch.name);
                // Build line
                formatterLines.push(buildLine(node, build, {main: true}));

                // Previous build
                if (config.layout.elements.previousBuild && build.previousBuild) {
                    separator();
                    formatterLines.push(buildLine(node, build.previousBuild, {prefix: "Previous build: "}));
                }

                // Next build
                if (config.layout.elements.nextBuild && build.nextBuild) {
                    separator();
                    formatterLines.push(buildLine(node, build.nextBuild, {prefix: "Next build: "}));
                }

                // Last eligible build
                if (config.layout.elements.lastEligibleBuild && build.autoVersioning && build.autoVersioning.lastEligibleBuild) {
                    separator();
                    formatterLines.push(buildLine(node, build.autoVersioning.lastEligibleBuild, {prefix: "Last eligible build: "}));
                }

                // Last build
                if (config.layout.elements.lastBuild) {
                    const lastBuild = self.getLastBuild(build);
                    if (lastBuild && lastBuild.id > build.id) {
                        separator();
                        formatterLines.push(buildLine(node, lastBuild, {prefix: "Last build: "}));
                    }
                }

                // Last AV status
                if (config.layout.elements.avStatus && build.autoVersioning && build.autoVersioning.status) {
                    separator();
                    formatterLines.push(avStatus(node, build.autoVersioning.status));
                }

                // Label formatter
                node.label.formatter = formatterLines.join('\n');

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

            const loadDependenciesForNode = (node, recursive) => {
                // Checking if expansion must be done for this done
                const expansionState = context.expansionIndex[node.value] === true;
                const mustExpand = recursive || expansionState;
                // Promise to return
                const d = $q.defer();
                // Check if the children have been already loaded or not
                if (!node.childrenLoaded) {
                    // Loading the dependencies
                    return loadBuildDependencies(node.value).then(builds => {
                        if (builds) {
                            node.children = builds.map(child => transformData(child, node));
                        }
                        node.childrenLoaded = true;
                        // Adding to the index for restoration on refresh
                        context.expansionIndex[node.value] = true;
                        // Recursive loading
                        if (mustExpand && node.children) {
                            $q.all(node.children.map(child => loadDependenciesForNode(child, recursive))).then(() => {
                                d.resolve(node);
                            });
                        }
                        // We're done here
                        else {
                            d.resolve(node);
                        }
                        // OK
                        return d.promise;
                    });
                } else {
                    if (mustExpand && node.children) {
                        $q.all(node.children.map(child => loadDependenciesForNode(child, recursive))).then(() => {
                            d.resolve(node);
                        });
                        return d.promise;
                    } else {
                        d.resolve(node);
                        return d.promise;
                    }
                }
            };

            const selectBuildId = (buildId) => {
                // Looks for the node having the buildId as a value
                const node = lookForNode(options.series[0].data[0], buildId);
                if (node) {
                    loadDependenciesForNode(node).then(() => {
                        // Selection
                        selectNode(node);
                        if (node.build && config.onBuildSelected) {
                            config.onBuildSelected(angular.copy(node.build));
                        }
                        // Refreshes the chart
                        getOrCreateChart().setOption(options);
                    });
                }
            };

            // Selection of a node

            const selectNode = (node) => {
                if (context.selectedNode) {
                    context.selectedNode.label.borderWidth = 1;
                }
                context.selectedNode = node;
                context.selectedNode.label.borderWidth = 4;
            };

            // Initializes the chart location

            const initChartEventHandling = (chart) => {
                chart.on('click', (params) => {
                    const buildId = params.value;
                    if (buildId) {
                        selectBuildId(buildId);
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


            const graph = {};

            graph.loadRootNode = () => {
                return otGraphqlService.pageGraphQLCall(
                    query,
                    config.rootVariables
                ).then(data => {
                    const rootBuild = config.rootBuild(data);
                    const rootNode = transformData(rootBuild, null);
                    // Loading of dependencies
                    return loadDependenciesForNode(rootNode, false);
                }).then((rootNode) => {
                    // Initial selection
                    selectNode(rootNode);
                    // Graph setup
                    const chart = getOrCreateChart();
                    createOptionWithData(rootNode);
                    chart.setOption(options);
                    return rootNode.build;
                });
            };

            graph.expandAllDependencies = () => {
                const root = options.series[0].data[0];
                loadDependenciesForNode(root, true).then(() => {
                    // Refreshes the chart
                    getOrCreateChart().setOption(options);
                });
            };

            // Resizing in height
            graph.resizeHeight = (height) => {
                if (context.chart) {
                    context.chart.resize({
                        width: null,
                        height: height,
                        silent: true,
                        animation: {
                            duration: 250
                        }
                    });
                }
            };

            // Selects the build on the right of the selected one (top most)

            graph.selectBuildRight = () => {
                if (context.selectedNode) {
                    // Making sure its children are loaded
                    loadDependenciesForNode(context.selectedNode).then(() => {
                        // Gets its children
                        const children = context.selectedNode.children;
                        if (children && children.length > 0) {
                            // Selects the first child
                            const child = children[0];
                            selectBuildId(child.value);
                        }
                    });
                }
            };

            // Selects the build on the left of the selected one (the parent)

            graph.selectBuildLeft = () => {
                if (context.selectedNode) {
                    const parentNode = context.parents[context.selectedNode.value];
                    if (parentNode) {
                        selectBuildId(parentNode.value);
                    }
                }
            };

            // Selects the build down of the selected one

            graph.selectBuildDown = () => {
                if (context.selectedNode) {
                    const parentNode = context.parents[context.selectedNode.value];
                    if (parentNode) {
                        const children = parentNode.children;
                        if (children && children.length > 0) {
                            const index = children.indexOf(context.selectedNode);
                            if (index >= 0 && (index + 1) < children.length) {
                                const nextNode = children[index + 1];
                                selectBuildId(nextNode.value);
                            }
                        }
                    }
                }
            };

            // Selects the build up of the selected one

            graph.selectBuildUp = () => {
                if (context.selectedNode) {
                    const parentNode = context.parents[context.selectedNode.value];
                    if (parentNode) {
                        const children = parentNode.children;
                        if (children && children.length > 0) {
                            const index = children.indexOf(context.selectedNode);
                            if (index > 0) {
                                const previousNode = children[index - 1];
                                selectBuildId(previousNode.value);
                            }
                        }
                    }
                }
            };

            return graph;
        };

        return self;
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

    .directive('otAutoVersioningDependencyGraph', function ($state, otExtensionAutoVersioningDependencyGraph, otTaskService) {
        return {
            restrict: 'E',
            templateUrl: 'extension/auto-versioning/directive.dependency-graph.tpl.html',
            scope: {
                rootBuildId: '=',
                rootBranchId: '=',
                rootBuildSetter: '=',
                direction: '@'
            },
            controller: function ($scope) {

                // Layout initial options
                const localStorageLayoutKey = 'dependencyGraphLayout';
                $scope.layout = {
                    elements: {
                        previousBuild: true,
                        nextBuild: true,
                        lastBuild: true,
                        lastEligibleBuild: true,
                        avStatus: true
                    },
                    height: 600
                };
                let storedLayout = localStorage.getItem(localStorageLayoutKey);
                if (storedLayout) {
                    const parsedStoredLayout = JSON.parse(storedLayout);
                    if (parsedStoredLayout) {
                        angular.copy(parsedStoredLayout, $scope.layout);
                    }
                }

                const config = {};
                config.direction = $scope.direction ? $scope.direction : 'DOWN';
                if ($scope.rootBuildId) {
                    config.rootQuery = (fragment) => {
                        return `
                            query RootBuild($buildId: Int!) {
                                build(id: $buildId) {
                                    ${fragment}
                                }
                            }
                        `;
                    };
                    config.rootVariables = {buildId: $scope.rootBuildId};
                    config.rootBuild = (data) => data.build;
                    config.autoVersioningArguments = 'buildId: $buildId';
                } else if ($scope.rootBranchId) {
                    config.rootQuery = (fragment) => {
                        return `
                            query RootBuild($branchId: Int!) {
                                branches(id: $branchId) {
                                    builds(count: 1) {
                                        ${fragment}
                                    }
                                }
                            }
                        `;
                    };
                    config.rootVariables = {branchId: $scope.rootBranchId};
                    config.rootBuild = (data) => data.branches[0].builds[0];
                    config.autoVersioningArguments = 'branchId: $branchId';
                } else {
                    throw new Error("Either root-build-id or root-branch-id must be set.");
                }
                config.onBuildSelected = (build) => {
                    $scope.selectedBuild = build;
                };
                config.layout = $scope.layout;

                const graph = otExtensionAutoVersioningDependencyGraph.createGraph(config);

                graph.loadRootNode().then(rootBuild => {
                    if ($scope.rootBuildSetter) {
                        $scope.rootBuildSetter(rootBuild);
                        $scope.selectedBuild = rootBuild;
                    }
                });

                // Refreshing the graph
                const refreshGraph = () => {
                    $scope.refreshing = true;
                    graph.loadRootNode().then(rootBuild => {
                        if ($scope.rootBuildSetter) {
                            $scope.rootBuildSetter(rootBuild);
                            $scope.selectedBuild = rootBuild;
                        }
                    }).finally(() => {
                        $scope.refreshing = false;
                    });
                };
                $scope.refreshGraph = refreshGraph;

                // Toggling the auto refresh
                $scope.toggleAutoRefresh = () => {
                    $scope.graphAutoRefresh = !$scope.graphAutoRefresh;
                };

                // Auto refresh watching
                $scope.$watch('graphAutoRefresh', () => {
                    const taskName = 'Graph auto refresh';
                    if ($scope.graphAutoRefresh) {
                        // 1 minute interval
                        otTaskService.register(taskName, refreshGraph, 60 * 1000);
                    } else {
                        otTaskService.stop(taskName);
                    }
                });

                // Expanding all dependencies
                $scope.expandAllDependencies = () => {
                    $scope.expanding = true;
                    try {
                        graph.expandAllDependencies();
                    } finally {
                        $scope.expanding = false;
                        $scope.expanded = true;
                    }
                };

                // Navigate to the downstream graph
                $scope.goToDownstreamGraph = () => {
                    if ($scope.rootBuildId) {
                        $state.go('auto-versioning-dependency-graph', {
                            buildId: $scope.rootBuildId
                        });
                    } else if ($scope.rootBranchId) {
                        $state.go('auto-versioning-dependency-graph-branch', {
                            branchId: $scope.rootBranchId
                        });
                    }
                };

                // Navigate to the upstream graph
                $scope.goToUpstreamGraph = () => {
                    if ($scope.rootBuildId) {
                        $state.go('auto-versioning-dependency-graph-upstream', {
                            buildId: $scope.rootBuildId
                        });
                    } else if ($scope.rootBranchId) {
                        $state.go('auto-versioning-dependency-graph-branch-upstream', {
                            branchId: $scope.rootBranchId
                        });
                    }
                };

                // Toggling an element
                $scope.toggleElement = () => {
                    // Value in the model has already been changed, just reacting to it now
                    // Saves the value
                    localStorage.setItem(localStorageLayoutKey, JSON.stringify($scope.layout));
                    // Refreshes the graph
                    refreshGraph();
                };

                // Changing the height of the chart
                $scope.applyHeight = () => {
                    let height = $scope.layout.height;
                    if (!height) {
                        height = 600;
                    }
                    graph.resizeHeight(height);
                };

                // Key shortcutd
                $scope.keyShortcutsVisible = false;
                $scope.toggleKeyShortcutsVisible = () => {
                    $scope.keyShortcutsVisible = !$scope.keyShortcutsVisible;
                };

                // Registering a key listener at dom level
                let keyListener = (event) => {
                    if (event.key === 'ArrowRight' && event.shiftKey) {
                        selectBuildRight();
                    } else if (event.key === 'ArrowLeft' && event.shiftKey) {
                        selectBuildLeft();
                    } else if (event.key === 'ArrowUp' && event.shiftKey) {
                        selectBuildUp();
                    } else if (event.key === 'ArrowDown' && event.shiftKey) {
                        selectBuildDown();
                    } else if (event.key === 'm') {
                        goToBranchDownstream();
                    } else if (event.key === 'M') {
                        goToBranchUpstream();
                    } else if (event.key === 'b') {
                        goToBuildDownstream();
                    } else if (event.key === 'B') {
                        goToBuildUpstream();
                    } else if (event.key === 'p') {
                        goToPreviousBuildDownstream();
                    } else if (event.key === 'P') {
                        goToPreviousBuildUpstream();
                    } else if (event.key === 'n') {
                        goToNextBuildDownstream();
                    } else if (event.key === 'N') {
                        goToNextBuildUpstream();
                    } else if (event.key === 'l') {
                        goToLastBuildDownstream();
                    } else if (event.key === 'L') {
                        goToLastBuildUpstream();
                    } else if (event.key === 'e') {
                        goToLastEligibleBuildDownstream();
                    } else if (event.key === 'E') {
                        goToLastEligibleBuildUpstream();
                    } else {
                        // console.log({event});
                    }
                };
                document.addEventListener('keyup', keyListener);
                // Unregistering the event when changing the page
                $scope.$on('$stateChangeStart', function () {
                    document.removeEventListener('keyup', keyListener);
                });

                const goToBuildGraph = (build, direction) => {
                    location.href = `#/extension/auto-versioning/dependency-graph/build/${build.id}/${direction}`;
                };

                const _goToBuildDownstream = (build) => {
                    goToBuildGraph(build, 'downstream');
                };

                const _goToBuildUpstream = (build) => {
                    goToBuildGraph(build, 'upstream');
                };

                const goToBranchDownstream = () => {
                    if ($scope.selectedBuild) {
                        location.href = `#/extension/auto-versioning/dependency-graph/branch/${$scope.selectedBuild.branch.id}/downstream`;
                    }
                };

                const goToBranchUpstream = () => {
                    if ($scope.selectedBuild) {
                        location.href = `#/extension/auto-versioning/dependency-graph/branch/${$scope.selectedBuild.branch.id}/upstream`;
                    }
                };

                const goToBuildDownstream = () => {
                    if ($scope.selectedBuild) {
                        _goToBuildDownstream($scope.selectedBuild);
                    }
                };

                const goToBuildUpstream = () => {
                    if ($scope.selectedBuild) {
                        _goToBuildUpstream($scope.selectedBuild);
                    }
                };

                const goToPreviousBuildDownstream = () => {
                    if ($scope.selectedBuild && $scope.selectedBuild.previousBuild) {
                        _goToBuildDownstream($scope.selectedBuild.previousBuild);
                    }
                };

                const goToPreviousBuildUpstream = () => {
                    if ($scope.selectedBuild && $scope.selectedBuild.previousBuild) {
                        _goToBuildUpstream($scope.selectedBuild.previousBuild);
                    }
                };

                const goToNextBuildDownstream = () => {
                    if ($scope.selectedBuild && $scope.selectedBuild.nextBuild) {
                        _goToBuildDownstream($scope.selectedBuild.nextBuild);
                    }
                };

                const goToNextBuildUpstream = () => {
                    if ($scope.selectedBuild && $scope.selectedBuild.nextBuild) {
                        _goToBuildUpstream($scope.selectedBuild.nextBuild);
                    }
                };

                const goToLastBuildDownstream = () => {
                    if ($scope.selectedBuild) {
                        const lastBuild = otExtensionAutoVersioningDependencyGraph.getLastBuild($scope.selectedBuild);
                        if (lastBuild) {
                            _goToBuildDownstream(lastBuild);
                        }
                    }
                };

                const goToLastBuildUpstream = () => {
                    if ($scope.selectedBuild) {
                        const lastBuild = otExtensionAutoVersioningDependencyGraph.getLastBuild($scope.selectedBuild);
                        if (lastBuild) {
                            _goToBuildUpstream(lastBuild);
                        }
                    }
                };

                const goToLastEligibleBuildDownstream = () => {
                    if ($scope.selectedBuild && $scope.selectedBuild.autoVersioning && $scope.selectedBuild.autoVersioning.lastEligibleBuild) {
                        _goToBuildDownstream($scope.selectedBuild.autoVersioning.lastEligibleBuild);
                    }
                };

                const goToLastEligibleBuildUpstream = () => {
                    if ($scope.selectedBuild && $scope.selectedBuild.autoVersioning && $scope.selectedBuild.autoVersioning.lastEligibleBuild) {
                        _goToBuildUpstream($scope.selectedBuild.autoVersioning.lastEligibleBuild);
                    }
                };

                const selectBuildRight = () => {
                    graph.selectBuildRight();
                };

                const selectBuildLeft = () => {
                    graph.selectBuildLeft();
                };

                const selectBuildUp = () => {
                    graph.selectBuildUp();
                };

                const selectBuildDown = () => {
                    graph.selectBuildDown();
                };
            }
        };
    })

    .controller('AutoVersioningDependencyGraphCtrl', function ($stateParams, $scope, ot) {
        $scope.rootBuildId = $stateParams.buildId;

        const view = ot.view();
        let viewInitialized = false;
        $scope.rootBuildSetter = (rootBuild) => {
            $scope.rootBuild = rootBuild;
            if (!viewInitialized) {
                view.breadcrumbs = ot.buildBreadcrumbs($scope.rootBuild);
                view.commands = [
                    ot.viewCloseCommand(`/build/${$scope.rootBuild.id}`)
                ];
                viewInitialized = true;
            }
        };
    })

    .controller('AutoVersioningDependencyGraphUpstreamCtrl', function ($stateParams, $scope, ot) {
        $scope.rootBuildId = $stateParams.buildId;

        const view = ot.view();
        let viewInitialized = false;
        $scope.rootBuildSetter = (rootBuild) => {
            $scope.rootBuild = rootBuild;
            if (!viewInitialized) {
                view.breadcrumbs = ot.buildBreadcrumbs($scope.rootBuild);
                view.commands = [
                    ot.viewCloseCommand(`/build/${$scope.rootBuild.id}`)
                ];
                viewInitialized = true;
            }
        };
    })

    .controller('AutoVersioningDependencyGraphBranchCtrl', function ($stateParams, $scope, ot) {
        $scope.branchId = $stateParams.branchId;

        const view = ot.view();
        let viewInitialized = false;
        $scope.rootBuildSetter = (rootBuild) => {
            $scope.rootBuild = rootBuild;
            if (!viewInitialized) {
                view.breadcrumbs = ot.branchBreadcrumbs($scope.rootBuild.branch);
                view.commands = [
                    ot.viewCloseCommand(`/branch/${$scope.rootBuild.branch.id}`)
                ];
                viewInitialized = true;
            }
        };

    })

    .controller('AutoVersioningDependencyGraphBranchUpstreamCtrl', function ($stateParams, $scope, ot) {
        $scope.branchId = $stateParams.branchId;

        const view = ot.view();
        let viewInitialized = false;
        $scope.rootBuildSetter = (rootBuild) => {
            $scope.rootBuild = rootBuild;
            if (!viewInitialized) {
                view.breadcrumbs = ot.branchBreadcrumbs($scope.rootBuild.branch);
                view.commands = [
                    ot.viewCloseCommand(`/branch/${$scope.rootBuild.branch.id}`)
                ];
                viewInitialized = true;
            }
        };

    })
;