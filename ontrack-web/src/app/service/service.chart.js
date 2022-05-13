angular.module('ot.service.chart', [
    'ot.service.core',
    'ot.service.form',
    'ot.service.graphql'
])
    .service('otChartService', function (ot, $q, $http, otFormService, otGraphqlService) {
        const self = {};

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
         * Creating a chart service for a duration chart (with mean, 90th percentile & max).
         *
         * @param config.chartOptions General chart options
         * @param config.query Function which takes some [GetChartOptions] as a parameter and returns a complete GraphQL query.
         * @return Chart object.
         */
        self.createDurationChart = (config) => {

            // Chart object to return
            const chart = {};

            // Default chart options
            chart.chartOptions = config.chartOptions;

            // Graph data to inject into the options
            chart.chartData = {
                categories: [],
                dates: [],
                data: {
                    mean: [],
                    percentile90: [],
                    maximum: []
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
                        dataView: { show: true, readOnly: true },
                        // magicType: { show: true, type: ['line', 'bar'] },
                        // restore: { show: true },
                        saveAsImage: { show: true }
                    }
                },
                legend: {
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
                series: [
                    {
                        name: 'Mean',
                        type: 'bar',
                        tooltip: {
                            valueFormatter: function (value) {
                                return value + ' s';
                            }
                        },
                        data: chart.chartData.data.mean
                    },
                    {
                        name: '90th percentile',
                        type: 'line',
                        tooltip: {
                            valueFormatter: function (value) {
                                return value + ' s';
                            }
                        },
                        data: chart.chartData.data.percentile90
                    },
                    {
                        name: 'Maximum',
                        type: 'line',
                        tooltip: {
                            valueFormatter: function (value) {
                                return value + ' s';
                            }
                        },
                        data: chart.chartData.data.maximum
                    }
                ]
            };

            // Dynamic chart options
            chart.run = () => {
                const query = config.query(chart.chartOptions);
                return otGraphqlService.pageGraphQLCall(query).then(data => {
                    chart.chartData.categories.length = 0;
                    chart.chartData.categories.push(...data.getChart.categories);

                    chart.chartData.dates.length = 0;
                    chart.chartData.dates.push(...data.getChart.dates);

                    chart.chartData.data.mean.length = 0;
                    chart.chartData.data.mean.push(...data.getChart.data.mean);

                    chart.chartData.data.percentile90.length = 0;
                    chart.chartData.data.percentile90.push(...data.getChart.data.percentile90);

                    chart.chartData.data.maximum.length = 0;
                    chart.chartData.data.maximum.push(...data.getChart.data.maximum);

                    return chart.options;
                });
            };

            // Editing the chart options
            chart.editChartOptions = () => {
                editChartOptions(chart.chartOptions).then(newOptions => {
                    angular.copy(newOptions, chart.chartOptions);
                    chart.run();
                });
            };

            // OK
            return chart;
        };

        /**
         * Creating a chart service for a percentage chart.
         *
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
                        dataView: { show: true, readOnly: true },
                        // magicType: { show: true, type: ['line', 'bar'] },
                        // restore: { show: true },
                        saveAsImage: { show: true }
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
                    chart.run();
                });
            };

            // OK
            return chart;
        };

        return self;
    })
;