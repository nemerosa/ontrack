angular.module('ot.directive.form', [
    'ot.directive.field'
])
    .directive('otForm', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.form.tpl.html',
            scope: {
                data: '=',
                form: '=',
                formRoot: '='
            },
            controller: function ($scope) {
                // Checking if a field is visible or not
                $scope.isFieldVisible = function (data, field) {
                    if (field.visibleIf) {
                        return data[field.visibleIf];
                    } else {
                        return true;
                    }
                };
            }
        };
    })
;