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

                // Help field
                $scope.help = {
                    heading: $scope.field.help,
                    fields: []
                };

                // Additional help fields
                for (var i = 0; i < $scope.field.form.fields.length; i++) {
                    var field = $scope.field.form.fields[i];
                    if (field.help) {
                        $scope.help.fields.push({
                            label: field.label,
                            help: field.help
                        });
                    }
                }

                // Removing the help from the field definition
                delete $scope.field.help;

                // Form entries
                $scope.formEntries = $scope.field.value.map(function (entry) {
                    var form = $scope.field.form;
                    // Duplicates the form
                    var entryForm = angular.copy(form);
                    // Removing help
                    for (var i = 0; i < entryForm.fields.length; i++) {
                        var field = entryForm.fields[i];
                        delete field.help;
                    }
                    // Sets the values in the form
                    otFormService.updateForm(entryForm, entry);
                    // Prepares the form
                    var entryData = otFormService.prepareForDisplay(entryForm);
                    // OK
                    return {
                        form: entryForm,
                        data: entryData
                    };
                });

                // Custom preparation for submit
                $scope.field.prepareForSubmit = function (data) {
                    // Prepares each form entry individually
                    angular.forEach($scope.formEntries, function (formEntry) {
                        otFormService.prepareForSubmit(
                            formEntry.form,
                            formEntry.data
                        );
                    });
                    // Collects the data into a list
                    data[$scope.field.name] = $scope.formEntries.map(function (formEntry) {
                        return formEntry.data;
                    });
                };

                // Adding an entry
                $scope.addEntry = function () {
                    // Form definition
                    var form = $scope.field.form;
                    // Duplicates the form
                    var entryForm = angular.copy(form);
                    // Removing help
                    for (var i = 0; i < entryForm.fields.length; i++) {
                        var field = entryForm.fields[i];
                        delete field.help;
                    }
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

                // Removes an entry
                $scope.removeEntry = function (formEntry) {
                    var idx = $scope.formEntries.indexOf(formEntry);
                    if (idx >= 0) {
                        $scope.formEntries.splice(idx, 1);
                    }
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