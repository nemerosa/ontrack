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
                // Reference to the chart
                chart: '=',
                // Option to edit the chart options
                chartOptions: '='
            },
            link: (scope, element) => {
                const domChartContainer = angular.element(element[0].children[1]);
                const domChart = element[0].children[1].children[0];

                const eChart = echarts.init(domChart);
            }
        };
    })
;