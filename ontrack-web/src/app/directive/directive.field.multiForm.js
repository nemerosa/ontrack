angular.module('ot.directive.field.multiForm', [

])
    .directive('otFieldMultiForm', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.field.multiForm.tpl.html',
            scope: {
                field: '=',
                data: '=',
                formRoot: '='
            },
            controller: function ($scope) {

                // TODO Form entries
                $scope.formEntries = [];

                // Adding an entry
                $scope.addEntry = function () {
                    // Form definition
                    var form = $scope.field.form;
                    // Duplicates the form
                    var newFormEntry = angular.copy(form);
                    // Adds to the entries
                    $scope.formEntries.push(newFormEntry);
                };

            }
        };
    })
;