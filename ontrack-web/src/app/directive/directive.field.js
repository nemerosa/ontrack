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
    .directive('otFieldValue', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.fieldValue.tpl.html',
            transclude: true,
            scope: {
                field: '='
            }
        };
    })
    .directive('otFieldRepetition', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.fieldRepetition.tpl.html',
            scope: {
                field: '='
            }
        };
    })
;