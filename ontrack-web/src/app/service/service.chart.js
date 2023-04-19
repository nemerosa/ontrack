angular.module('ot.service.chart', [
    'ot.service.core',
    'ot.service.form',
    'ot.service.graphql'
])
    .service('otChartService', function (ot, $q, $http, otFormService, otGraphqlService) {
        const self = {};

        /**
         * Loading the chart options from the local storage
         */
        self.loadChartOptions = (key, defaultChartOptions) => {
            const stored = localStorage.getItem(key);
            if (stored) {
                return JSON.parse(stored);
            } else {
                return defaultChartOptions;
            }
        };

        /**
         * Private method to edit some general chart options
         * @param initialOptions
         * @returns {*}
         */
        const editChartOptions = (initialOptions) => {
            return otFormService.display({
                form: {
                    fields: [{
                        name: 'interval',
                        type: 'selection',
                        label: "Interval",
                        help: "How far to get the data.",
                        required: true,
                        value: initialOptions.interval,
                        items: [{
                            id: '1w',
                            name: 'One week',
                        }, {
                            id: '2w',
                            name: 'Two weeks',
                        }, {
                            id: '4w',
                            name: 'Four weeks',
                        }, {
                            id: '1m',
                            name: 'One month',
                        }, {
                            id: '2m',
                            name: 'Two months',
                        }, {
                            id: '3m',
                            name: 'Three months',
                        }, {
                            id: '4m',
                            name: 'Four months',
                        }, {
                            id: '6m',
                            name: 'Six months',
                        }, {
                            id: '1y',
                            name: 'One year',
                        }, {
                            id: '2y',
                            name: 'Two years',
                        }],
                        itemId: 'id',
                        itemName: 'name',
                    }, {
                        name: 'period',
                        type: 'selection',
                        label: "Period",
                        help: "Granularity of the statistics.",
                        required: true,
                        value: initialOptions.period,
                        items: [{
                            id: '1d',
                            name: 'One day',
                        }, {
                            id: '2d',
                            name: 'Two days',
                        }, {
                            id: '3d',
                            name: 'Three days',
                        }, {
                            id: '1w',
                            name: 'One week',
                        }, {
                            id: '2w',
                            name: 'Two weeks',
                        }, {
                            id: '4w',
                            name: 'Four weeks',
                        }, {
                            id: '1m',
                            name: 'One month',
                        }, {
                            id: '2m',
                            name: 'Two months',
                        }, {
                            id: '3m',
                            name: 'Three months',
                        }, {
                            id: '4m',
                            name: 'Four months',
                        }, {
                            id: '6m',
                            name: 'Six months',
                        }, {
                            id: '1y',
                            name: 'One year',
                        }, {
                            id: '2y',
                            name: 'Two years',
                        }],
                        itemId: 'id',
                        itemName: 'name',
                    }
                    ]
                },
                title: "Chart options",
                submit: (submitData) => {
                    return submitData;
                }
            });
        };

        /**
         * Common code for creating a chart service for a chart
         *
         * @param config.chartOptionsKey Storage key for the chart options
         * @param config.chartOptions General chart options
         * @param config.query Function which takes some [GetChartOptions] as a parameter and returns a complete GraphQL query.
         * ---
         * @param config.queryVariables Optional function which return the query variables as a map
         * @param config.chartData Initial empty data
         * @param config.legend True if a legend based on categories must be displayed
         * @param config.yAxis Y axis configuration
         * @param config.series Function of chartData which returns the list of series
         * @param config.onData Method called when data is received from the server
         * @return Chart object.
         */
        const abstractCreateChart = (config) => {

            // Chart object to return
            const chart = {};

            // Default chart options
            chart.chartOptions = config.chartOptions;

            // Graph data to inject into the options
            chart.chartData = config.chartData;

            // Base options
            chart.options = {
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'cross',
                        crossStyle: {
                            color: '#999'
                        }
                    }
                },
                toolbox: {
                    feature: {
                        dataView: {show: true, readOnly: true},
                        saveAsImage: {show: true},
                        myZoom: {
                            show: true,
                            title: 'Zoom in/out',
                            icon: 'path://M1014.237213 1013.761259a28.517906 28.517906 0 0 1-22.344547 10.118285h-287.979654a32.011123 32.011123 0 0 1 0-63.962019l210.345905 0.963647-345.105808-345.135922 45.201031-45.261259 345.527403 345.527403v-212.062399a31.981009 31.981009 0 1 1 63.962019 0L1023.90379 990.965003c0 8.973956-3.764243 16.984265-9.666577 22.796256z m-22.344547-661.753938a31.981009 31.981009 0 0 1-32.011123-32.011123l1.023874-210.345904-345.196149 345.105807-45.201032-45.261259L915.975411 64.027667h-212.062399a32.011123 32.011123 0 0 1 0-64.022246h287.076236c8.913728 0 16.984265 3.764243 22.796256 9.666576a28.608248 28.608248 0 0 1 10.058058 22.344547v287.979654c0 17.676886-14.334238 32.011123-31.950896 32.011123z m-582.373579 101.393653L63.991685 107.873571v212.122627a32.011123 32.011123 0 1 1-63.962019 0V32.919962C0.029666 24.006235 3.733681 15.935697 9.636014 10.123706A28.578134 28.578134 0 0 1 32.040789 0.005421h287.919427a32.011123 32.011123 0 0 1 0 64.022246l-210.345905-1.023874 345.166036 345.196149-45.26126 45.201032zM32.040789 671.937871c17.616658 0 31.950895 14.334238 31.950896 32.011124l-0.963647 210.345904 345.135922-345.166035 45.261259 45.261259L107.897816 959.917525h212.0624a31.981009 31.981009 0 1 1 0 63.962019H32.944208c-8.973956 0-16.984265-3.704015-22.796257-9.606348A28.668475 28.668475 0 0 1 0.029666 991.868421v-287.919426c0-17.676886 14.334238-32.011123 32.011123-32.011124z',
                            onclick: () => onZoom()
                        }
                    }
                },
                legend: {
                    show: config.legend,
                    data: chart.chartData.categories
                },
                xAxis: [
                    {
                        type: 'category',
                        data: chart.chartData.dates,
                        axisPointer: {
                            type: 'shadow'
                        },
                        axisLabel: {
                            rotate: 45
                        }
                    }
                ],
                yAxis: config.yAxis,
                series: config.series(config.chartData)
            };

            // Dynamic chart options
            chart.run = () => {
                const query = config.query(chart.chartOptions);
                let queryVariables = {};
                if (config.queryVariables) {
                    queryVariables = config.queryVariables(chart.chartOptions);
                }
                return otGraphqlService.pageGraphQLCall(query, queryVariables).then(data => {
                    config.onData(data, chart.chartData, chart.options);
                    chart.options.series = config.series(config.chartData);
                    return chart.options;
                });
            };

            // Editing the chart options
            chart.editChartOptions = () => {
                editChartOptions(chart.chartOptions).then(newOptions => {
                    angular.copy(newOptions, chart.chartOptions);
                    if (config.chartOptionsKey) {
                        localStorage.setItem(config.chartOptionsKey, JSON.stringify(chart.chartOptions));
                    }
                    chart.run();
                });
            };

            // List of chart listeners
            const chartListeners = [];

            // Registering a listener
            chart.addChartListener = (listener) => {
                chartListeners.push(listener);
            };

            // Zoom management
            let zoomed = false;
            const onZoom = () => {
                zoomed = !zoomed;
                chartListeners.forEach(listener => {
                    if (listener.onZoom) {
                        listener.onZoom(zoomed);
                    }
                });
            };

            // OK
            return chart;
        };

        /**
         * Creating a chart service for a duration chart (with mean, 90th percentile & max).
         *
         * @param config.chartOptionsKey Storage key for the chart options
         * @param config.chartOptions General chart options
         * @param config.query Function which takes some [GetChartOptions] as a parameter and returns a complete GraphQL query.
         * @return Chart object.
         */
        self.createDurationChart = (config) => {

            return abstractCreateChart({
                chartOptionsKey: config.chartOptionsKey,
                chartOptions: config.chartOptions,
                query: config.query,
                queryVariables: config.queryVariables,
                chartData: {
                    categories: [],
                    dates: [],
                    data: {
                        mean: [],
                        percentile90: [],
                        maximum: []
                    }
                },
                legend: true,
                yAxis: [
                    {
                        type: 'value',
                        name: 'Duration',
                        min: 0,
                        axisLabel: {
                            formatter: (value) => {
                                if (value === 0) {
                                    return '0';
                                } else {
                                    return moment.duration(value, 'seconds').humanize({
                                        h: 72,
                                        m: 300,
                                        s: 300,
                                        ss: 0
                                    });
                                }
                            }
                        }
                    }
                ],
                series: (chartData) => {
                    return [
                        {
                            name: 'Mean',
                            type: 'bar',
                            tooltip: {
                                valueFormatter: function (value) {
                                    return value + ' s';
                                }
                            },
                            data: chartData.data.mean
                        },
                        {
                            name: '90th percentile',
                            type: 'line',
                            tooltip: {
                                valueFormatter: function (value) {
                                    return value + ' s';
                                }
                            },
                            data: chartData.data.percentile90
                        },
                        {
                            name: 'Maximum',
                            type: 'line',
                            tooltip: {
                                valueFormatter: function (value) {
                                    return value + ' s';
                                }
                            },
                            data: chartData.data.maximum
                        }
                    ];
                },
                onData: (data, chartData) => {
                    chartData.categories.length = 0;
                    chartData.categories.push(...data.getChart.categories);

                    chartData.dates.length = 0;
                    chartData.dates.push(...data.getChart.dates);

                    chartData.data.mean.length = 0;
                    chartData.data.mean.push(...data.getChart.data.mean);

                    chartData.data.percentile90.length = 0;
                    chartData.data.percentile90.push(...data.getChart.data.percentile90);

                    chartData.data.maximum.length = 0;
                    chartData.data.maximum.push(...data.getChart.data.maximum);
                }
            });
        };

        /**
         * Creating a chart service for a list of metrics.
         *
         * @param config.chartOptionsKey Storage key for the chart options
         * @param config.chartOptions General chart options
         * @param config.query Function which takes some [GetChartOptions] as a parameter and returns a complete GraphQL query.
         * @return Chart object.
         */
        self.createMetricsChart = (config) => {

            return abstractCreateChart({
                chartOptionsKey: config.chartOptionsKey,
                chartOptions: config.chartOptions,
                query: config.query,
                queryVariables: config.queryVariables,
                chartData: {
                    categories: [],
                    dates: [],
                    data: {}
                },
                legend: true,
                yAxis: [
                    {
                        type: 'value',
                        name: 'Count',
                        min: 0
                    }
                ],
                series: (chartData) => {
                    return chartData.categories.map(metric => {
                        return {
                            name: metric,
                            type: 'line',
                            connectNulls: true,
                            data: chartData.data[metric]
                        };
                    });
                },
                onData: (data, chartData, options) => {
                    chartData.categories.length = 0;
                    const metricNames = data.getChart.metricNames;
                    const metricColors = data.getChart.metricColors;
                    chartData.categories.push(...metricNames);

                    if (metricNames.length > 4) {
                        options.legend.type = 'scroll';
                        options.legend.pageButtonPosition = 'start';
                        options.legend.selectedMode = 'multiple';
                        options.legend.selector = ['all', 'inverse'];
                        options.legend.selectorPosition = 'start';
                    } else {
                        options.legend.type = 'plain';
                    }

                    if (metricColors) {
                        options.color = metricColors;
                    }

                    metricNames.forEach(metricName => {
                        if (chartData.data[metricName] !== undefined) {
                            chartData.data[metricName].length = 0;
                        } else {
                            chartData.data[metricName] = [];
                        }
                    });

                    chartData.dates.length = 0;
                    chartData.dates.push(...data.getChart.dates);

                    const metricValues = data.getChart.metricValues;

                    metricValues.forEach(point => {
                        metricNames.forEach(metricName => {
                            const value = point[metricName];
                            chartData.data[metricName].push(value);
                        });
                    });
                }
            });
        };

        /**
         * Creating a chart service for a count chart.
         *
         * @param config.chartOptionsKey Storage key for the chart options
         * @param config.chartOptions General chart options
         * @param config.query Function which takes some [GetChartOptions] as a parameter and returns a complete GraphQL query.
         * @return Chart object.
         */
        self.createCountChart = (config) => {

            return abstractCreateChart({
                chartOptionsKey: config.chartOptionsKey,
                chartOptions: config.chartOptions,
                query: config.query,
                queryVariables: config.queryVariables,
                chartData: {
                    categories: [],
                    dates: [],
                    data: {
                        value: []
                    }
                },
                legend: false,
                yAxis: [
                    {
                        type: 'value',
                        name: 'Count',
                        min: 0
                    }
                ],
                series: (chartData) => {
                    return [
                        {
                            name: 'Value',
                            type: 'bar',
                            data: chartData.data.value
                        }
                    ];
                },
                onData: (data, chartData) => {
                    chartData.dates.length = 0;
                    chartData.dates.push(...data.getChart.dates);

                    chartData.data.value.length = 0;
                    chartData.data.value.push(...data.getChart.data);
                }
            });
        };

        /**
         * Creating a chart service for a percentage chart.
         *
         * @param config.chartOptionsKey Storage key for the chart options
         * @param config.name Name of the % axis
         * @param config.chartOptions General chart options
         * @param config.query Function which takes some [GetChartOptions] as a parameter and returns a complete GraphQL query.
         * @return Chart object.
         */
        self.createPercentageChart = (config) => {

            return abstractCreateChart({
                chartOptionsKey: config.chartOptionsKey,
                chartOptions: config.chartOptions,
                query: config.query,
                queryVariables: config.queryVariables,
                chartData: {
                    dates: [],
                    data: {
                        value: []
                    }
                },
                legend: false,
                yAxis: [
                    {
                        type: 'value',
                        name: config.name,
                        min: 0,
                        max: 100
                    }
                ],
                series: (chartData) => {
                    return [
                        {
                            name: 'Value',
                            type: 'bar',
                            data: chartData.data.value
                        }
                    ];
                },
                onData: (data, chartData) => {
                    chartData.dates.length = 0;
                    chartData.dates.push(...data.getChart.dates);

                    chartData.data.value.length = 0;
                    chartData.data.value.push(...data.getChart.data);
                }
            });
        };

        self.createGenericChart = (config) => {
            switch (config.chartType) {
                case 'duration':
                    return self.createDurationChart(config);
                case 'count':
                    return self.createCountChart(config);
                case 'metrics':
                    return self.createMetricsChart(config);
                case 'percentage':
                    config.name = config.chartConfig.name;
                    return self.createPercentageChart(config);
            }
        };

        return self;
    })
;