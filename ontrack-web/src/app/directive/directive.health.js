angular.module('ot.directive.health', [
])
    .directive('otHealthStatus', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.healthStatus.tpl.html',
            scope: {
                value: '='
            }
        };
    })
;