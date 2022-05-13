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
                chart: '='
            }
        };
    })
;