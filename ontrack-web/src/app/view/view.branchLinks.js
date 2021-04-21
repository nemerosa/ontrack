angular.module('ot.view.branchLinks', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure',
    'ot.service.graphql',
    'ot.service.task'
])
    .config(function ($stateProvider) {
        $stateProvider.state('branchLinks', {
            url: '/branch/{branchId}/links',
            templateUrl: 'app/view/view.branchLinks.tpl.html',
            controller: 'BranchLinksCtrl'
        });
    })
    .controller('BranchLinksCtrl', function ($location, $scope, $stateParams, $window, ot, otGraphqlService, otTaskService) {
        const branchId = $stateParams.branchId;
        const view = ot.view();

        const parameter = $location.hash();
        let initialDirection = 'USING';
        if (parameter === 'USED_BY') {
            initialDirection = 'USED_BY';
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
            if (context.direction === 'USING') {
                context.direction = 'USED_BY';
            } else {
                context.direction = 'USING';
            }
            $location.hash(context.direction);
            refreshGraph(true);
        };

        // Chart options
        const options = {
            tooltip: {
                trigger: 'item',
                    triggerOn: 'click',
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

        // Refreshes the chart
        const refreshGraph = (clear) => {
            if (context.chart && clear) {
                context.chart.showLoading();
            }
            $scope.loadingData = true;
            loadData().then(raw => {
                try {
                    // View setup
                    if (!$scope.branch) {
                        const branch = raw.branches[0];
                        view.title = '';
                        view.breadcrumbs = ot.branchBreadcrumbs(branch);
                        view.commands = [
                            ot.viewCloseCommand('/branch/' + branch.id)
                        ];
                        $scope.branch = branch;
                    }
                    // Graph data preparation
                    const data = transformData(raw, context.direction);
                    // Graph setup
                    const chart = getOrCreateChart();
                    createOptionWithData(data);
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
            });
        };

        // Creates the chart option with some data
        const createOptionWithData = (data) => {
            options.series[0].data = [data];
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
                if (params.value && params.value.page) {
                    $window.open(params.value.page, "_blank");
                }
            });
        };

        // Transforming the raw data into a graph
        const transformData = (raw, direction) => {
            const branch = raw.branches[0];
            // Case when there is no build
            if (branch.builds.length === 0) {
                return {
                    name: branch.name
                };
            }
            const build = branch.builds[0];
            return transformGraphIntoNode(build.graph, null, direction, 0);
        };

        // Transforming a `BranchLinksNode` into a chart node
        const transformGraphIntoNode = (graph, edge, direction, depth) => {
            // Value is the build ID or the branch ID
            let value;
            if (graph.build) {
                value = {
                    type: 'build',
                    page: graph.build.links._page
                };
            } else {
                value = {
                    type: 'project',
                    page: graph.project.links._page
                };
            }
            // Initial node
            const node = {
                name: graph.project.name,
                value: value
            };
            // Label
            node.label = {
                rich: {}
            };
            // Format lines
            const formatterLines = [];
            // Project name as a line
            formatterLines.push(graph.project.name);
            // Display name in case of a build
            if (graph.build) {
                // Display name is the build name unless there is a release property attached to the build
                let displayName = graph.build.name;
                if (graph.build.releaseProperty && graph.build.releaseProperty.value && graph.build.releaseProperty.value.name) {
                    displayName = graph.build.releaseProperty.value.name;
                }
                // Promotions
                let promotionsFormat = '';
                if (graph.build && graph.build.promotionRuns && graph.build.promotionRuns.length > 0) {
                    const run = graph.build.promotionRuns[graph.build.promotionRuns.length - 1];
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
                // Build line
                formatterLines.push(`${promotionsFormat}{decorationText|${displayName}}`);
            }
            // Decoration labels
            if (edge) {
                edge.decorations.forEach(decoration => {
                    const line = getDecorationFormatterForLabel(decoration, node.label.rich);
                    if (line) {
                        formatterLines.push(line);
                    }
                });
            }
            // Label formatter
            node.label.formatter = formatterLines.join('\n');

            // Decoration tooltip
            if (edge) {
                const tooltipLines = edge.decorations.map(decoration => getDecorationFormatterForTooltip(decoration)).filter(line => line);
                if (tooltipLines.length > 0) {
                    node.tooltip = {
                        show: true,
                        formatter: tooltipLines.join('\n')
                    };
                }
            }

            // Edges
            node.children = graph.edges.map(edge => transformGraphIntoNode(edge.linkedTo, edge, direction, depth));

            // OK
            return node;
        };

        const getDecorationFormatterForTooltip = (decoration) => {
            // No decoration when no text or no link
            if (!decoration.text || !decoration.url) {
                return '';
            }

            let line = '<p>';

            // Icon
            if (decoration.icon) {
                const iconUrl = getDecorationIconUrl(decoration);
                line += `<img src="${iconUrl}" width="16" height="16" alt="${decoration.icon}"/> `;
            }

            // Text
            line += `<a href="${decoration.url}"><span title="${decoration.description}">${decoration.text}</span></a>`;

            line += '</p>';
            return line;
        };

        const getDecorationFormatterForLabel = (decoration, classes) => {
            let line = '';
            if (decoration.icon) {
                const className = `${decoration.feature.id}_${decoration.id}_${decoration.icon}`;
                const iconUrl = getDecorationIconUrl(decoration);
                classes[className] = {
                    backgroundColor: {
                        image: iconUrl
                    },
                    height: 16,
                    weight: 16
                };
                line += `{${className}|}`;
            }
            if (decoration.text) {
                line += `{decorationText|${decoration.text}}`;
            }
            return line;
        };

        const getDecorationIconUrl = (decoration) => {
            if (decoration.icon) {
                return `/extension/${decoration.feature.id}/graph/${decoration.id}/${decoration.icon}.png`;
            } else {
                return '';
            }
        };

        // Loading the raw data
        const loadData = () => {
            return otGraphqlService.pageGraphQLCall(query, {
                branchId: branchId,
                direction: context.direction
            });
        };

        // Query recursive building

        const buildNodeQuery = (levels) => {
            if (levels > 0) {
                return `
                    ...nodeContent
                    edges {
                      direction
                      decorations {
                        ...decorationContent
                      }
                      linkedTo {
                        ${ buildNodeQuery(levels - 1) }
                      }
                    }
                `;
            } else {
                return `
                    ...nodeContent
                `;
            }
        };

        const buildQuery = (levels) => {
            return `
                query BranchBuildGraph(
                  $branchId: Int!,
                  $direction: BranchLinksDirection!
                ) {
                    branches(id: $branchId) {
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
                      builds(count: 1) {
                        name
                        graph(direction: $direction) {
                          ${ buildNodeQuery(levels) }
                        }
                      }
                    }
                }
        
                fragment decorationContent on BranchLinksDecoration {
                  feature {
                    id
                  }
                  id
                  text
                  description
                  icon
                  url
                }
                
                fragment nodeContent on BranchLinksNode {
                  project {
                    id
                    name
                    links {
                      _page
                    }
                  }
                  build {
                    id
                    name
                    links {
                      _page
                    }
                    releaseProperty {
                        value
                    }
                    promotionRuns(lastPerLevel: true) {
                      promotionLevel {
                        name
                        links {
                          _image
                        }
                      }
                    }
                  }
                }
            `;
        };

        const query = buildQuery(10);

        // Loading the data on load
        refreshGraph(true);

        // Refreshing the graph regularly
        const interval = 60 * 1000; // 60 seconds
        otTaskService.register(`Branch links ${branchId}`, () => refreshGraph(false), interval);
    })
;