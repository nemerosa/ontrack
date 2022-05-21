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
                        saveAsImage: {show: true},
                        myZoom: {
                            show: true,
                            title: 'Zoom in/out',
                            icon: 'path://M432.45,595.444c0,2.177-4.661,6.82-11.305,6.82c-6.475,0-11.306-4.567-11.306-6.82s4.852-6.812,11.306-6.812C427.841,588.632,432.452,593.191,432.45,595.444L432.45,595.444z M421.155,589.876c-3.009,0-5.448,2.495-5.448,5.572s2.439,5.572,5.448,5.572c3.01,0,5.449-2.495,5.449-5.572C426.604,592.371,424.165,589.876,421.155,589.876L421.155,589.876z M421.146,591.891c-1.916,0-3.47,1.589-3.47,3.549c0,1.959,1.554,3.548,3.47,3.548s3.469-1.589,3.469-3.548C424.614,593.479,423.062,591.891,421.146,591.891L421.146,591.891zM421.146,591.891',
                            onclick: () => {
                                console.log("Zooming...");
                            }
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
                            formatter: (value) => {
                                if (value === 0) {
                                    return '0';
                                } else {
                                    return moment.duration(value, 'seconds').humanize({
                                        h: 72,
                                        m: 300,
                                        s: 300
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

            return abstractCreateChart({
                chartOptionsKey: config.chartOptionsKey,
                chartOptions: config.chartOptions,
                query: config.query,
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

        return self;
    })
;