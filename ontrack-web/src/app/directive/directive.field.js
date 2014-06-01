angular.module('ot.directive.field', [

])
    .directive('otField', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.field.tpl.html',
            transclude: true,
            scope: {
                data: '=',
                field: '='
            }
        };
    })
;