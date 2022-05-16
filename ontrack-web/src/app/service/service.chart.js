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
                        // magicType: { show: true, type: ['line', 'bar'] },
                        // restore: { show: true },
                        saveAsImage: {show: true}
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
                return otGraphqlService.pageGraphQLCall(query).then(data => {
                    config.onData(data, chart.chartData);
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
                            formatter: '{value} s'
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

            // Chart object to return
            const chart = {};

            // Default chart options
            chart.chartOptions = config.chartOptions;

            // Graph data to inject into the options
            chart.chartData = {
                dates: [],
                data: {
                    value: []
                }
            };

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
                        // magicType: { show: true, type: ['line', 'bar'] },
                        // restore: { show: true },
                        saveAsImage: {show: true}
                    }
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
                yAxis: [
                    {
                        type: 'value',
                        name: config.name,
                        min: 0,
                        max: 100
                    }
                ],
                series: [
                    {
                        name: 'Value',
                        type: 'bar',
                        data: chart.chartData.data.value
                    }
                ]
            };

            // Dynamic chart options
            chart.run = () => {
                const query = config.query(chart.chartOptions);
                return otGraphqlService.pageGraphQLCall(query).then(data => {

                    chart.chartData.dates.length = 0;
                    chart.chartData.dates.push(...data.getChart.dates);

                    chart.chartData.data.value.length = 0;
                    chart.chartData.data.value.push(...data.getChart.data);

                    return chart.options;
                });
            };

            // Editing the chart options
            chart.editChartOptions = () => {
                editChartOptions(chart.chartOptions).then(newOptions => {
                    angular.copy(newOptions, chart.chartOptions);
                    if (config.chartOptionsKey) {
                        localStorage.setItem(config.chartOptionsKey, chart.chartOptions);
                    }
                    chart.run();
                });
            };

            // OK
            return chart;
        };

        return self;
    })
;