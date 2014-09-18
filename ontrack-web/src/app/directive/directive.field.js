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
            },
            controller: function ($scope) {
                // Adding a field
                $scope.addField = function (repetitionField) {
                    // If the value if not defined, creates it
                    if (!repetitionField.value) {
                        repetitionField.value = [];
                    }
                    // Adds a copy of the field definition
                    repetitionField.value.push(angular.copy(repetitionField.field));
                };
            }
        };
    })
;