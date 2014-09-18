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
    .directive('otFieldNamedEntries', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.fieldNamedEntries.tpl.html',
            scope: {
                field: '='
            },
            controller: function ($scope) {
                // Adding an entry
                $scope.addEntry = function (field) {
                };
            }
        };
    })
;