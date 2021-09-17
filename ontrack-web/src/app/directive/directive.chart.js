angular.module('ot.directive.chart', [
])
    .directive('otChart', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.chart.tpl.html',
            scope: {
                // Function which sets up the chart options
                // It must returns a promise
                options: '&',
                // Height in CSS
                height: '='
            },
            controller: ($scope) => {
                // Chart context
                $scope.context = {
                    // the chart, not initialized at first
                    chart: undefined
                };

                // Gets the chart or creates it
                const getOrCreateChart = () => {
                    if ($scope.context.chart) {
                        return $scope.context.chart;
                    }
                    $scope.context.chart = createChart();
                    return $scope.context.chart;
                };

                // Creates & inits the chart
                const createChart = () => {
                    const graph = document.getElementById('graph');
                    // OK
                    return echarts.init(graph);
                };

                // Loading the data & configuring the chart
                const loadData = () => {
                    $scope.loadingChart = true;
                    $scope.options().then(options => {
                        if ($scope.context.chart) {
                            $scope.context.chart.showLoading();
                        }
                        // Graph setup
                        const chart = getOrCreateChart();
                        // Sets the options
                        chart.setOption(options);
                    }).finally(()=> {
                        $scope.loadingChart = false;
                        if ($scope.context.chart) {
                            $scope.context.chart.hideLoading();
                        }
                    });
                };

                // Initial call
                loadData();
            }
        };
    })
;