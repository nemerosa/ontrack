angular.module('ot.directive.dynamic-chart', [
])
    .directive('otDynamicChart', function () {
        return {
            replace: true,
            restrict: 'E',
            templateUrl: 'app/directive/directive.dynamic-chart.tpl.html',
            scope: {
                // Function which sets up the chart options
                // It must return a promise
                options: '&',
                // Chart options
                chartOptions: '=',
            },
            link: (scope, element, attr) => {
                const chart = echarts.init(element[0]);

                let loaded = false;

                // Loading the data
                const loadData = (chart) => {
                    chart.showLoading('default', { text: 'Loading data...' });
                    scope.options().then(options => {
                        options = options || {};
                        if (options.forceClear) chart.clear();
                        if (options.series && options.series.length) {
                            chart.hideLoading();
                            chart.setOption(options, options.notMerge);
                            chart.resize();
                        } else {
                            chart.showLoading('default', options.errorMsg || { text: 'No data' });
                        }
                        loaded = true;
                    });
                };

                // Watching the general chart options
                scope.$watch("chartOptions", (newVal, oldVal) => {
                    if (scope.chartOptions) {
                        if (loaded) {
                            loadData(chart);
                        }
                    }
                }, /* objectEquality */ true);

                // Loads the data on first call
                loadData(chart);
            }
        };
    })
;