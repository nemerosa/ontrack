angular.module('ot.directive.decorated-chart', [
])
    .directive('otDecoratedChart', function () {
        return {
            replace: true,
            restrict: 'E',
            templateUrl: 'app/directive/directive.decorated-chart.tpl.html',
            scope: {
                // Title of the chart
                title: '@',
                // Reference to the chart, as being created by otChartService
                chart: '=',
                // Option to edit the chart options
                chartOptions: '='
            },
            link: (scope, element) => {

                const initChart = () => {
                    const domChartContainer = angular.element(element[0].children[1]);
                    const domChart = element[0].children[1].children[0];

                    const eChart = echarts.init(domChart);

                    let loaded = false;

                    // Loading the data
                    const loadData = () => {
                        eChart.showLoading('default', { text: 'Loading data...' });
                        scope.chart.run().then(options => {
                            options = options || {};
                            if (options.forceClear) eChart.clear();
                            if (options.series && options.series.length) {
                                eChart.hideLoading();
                                eChart.setOption(options, options.notMerge);
                                eChart.resize();
                            } else {
                                eChart.showLoading('default', options.errorMsg || { text: 'No data' });
                            }
                            loaded = true;
                        });
                    };

                    // Watching the general chart options
                    scope.$watch("chart.chartOptions", (newVal, oldVal) => {
                        if (scope.chart.chartOptions) {
                            if (loaded) {
                                loadData();
                            }
                        }
                    }, /* objectEquality */ true);

                    // Registers a listener
                    scope.chart.addChartListener({
                        onZoom: (zoomed) => {
                            if (zoomed) {
                                domChartContainer.removeClass('ot-chart-unzoomed');
                                domChartContainer.addClass('ot-chart-zoomed');
                            } else {
                                domChartContainer.removeClass('ot-chart-zoomed');
                                domChartContainer.addClass('ot-chart-unzoomed');
                            }
                            eChart.resize();
                        }
                    });

                    // Loads the data on first call
                    loadData();
                };

                scope.$watch('chart', () => {
                    if (scope.chart) {
                        console.log("Init chart: ", scope.title);
                        initChart();
                    }
                });
            }
        };
    })
;