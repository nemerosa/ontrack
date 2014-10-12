angular.module('ot.directive.field.multiForm', [
    'ot.service.form'
])
    .directive('otFieldMultiForm', function (otFormService) {
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
                    var entryForm = angular.copy(form);
                    // Data
                    var entryData = otFormService.prepareForDisplay(entryForm);
                    // Entry
                    var entry = {
                        form: entryForm,
                        data: entryData
                    };
                    // Adds to the entries
                    $scope.formEntries.push(entry);
                };

            }
        };
    })
    .directive('otFieldMultiFormEntry', function ($compile) {
        return {
            restrict: 'E',
            template: '<div></div>',
            scope: {
                form: '=',
                data: '=',
                formRoot: '='
            },
            link: function (scope, element) {
                if (angular.isDefined(scope.form)) {
                    $compile('<ot-form form="form" data="data" form-root="formRoot"></ot-form>')(scope, function (cloned, scope) {
                        element.append(cloned);
                    });
                }
            }
        };
    })
;