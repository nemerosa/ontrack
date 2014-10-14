angular.module('ot.directive.field.serviceConfigurator', [
    'ot.service.form'
])
    .directive('otFieldServiceConfigurator', function (otFormService) {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.field.serviceConfigurator.tpl.html',
            scope: {
                field: '=',
                data: '=',
                formRoot: '='
            },
            controller: function ($scope) {

                // Initial selection
                var serviceConfiguration = $scope.data[$scope.field.name];
                if (serviceConfiguration && serviceConfiguration.id) {
                    var source = getSource(serviceConfiguration.id);
                    if (source) {
                        // Updates the form
                        otFormService.updateForm(source.form, serviceConfiguration.data);
                        // Prepares the data
                        var data = otFormService.prepareForDisplay(source.form);
                        // Sets the entry
                        $scope.formEntries = [
                            {
                                sourceId: serviceConfiguration.id,
                                form: source.form,
                                data: data
                            }
                        ];
                        // Sets the selection
                        $scope.sourceId = serviceConfiguration.id;
                    } else {
                        $scope.formEntries = [];
                    }
                } else {
                    $scope.formEntries = [];
                }

                // Custom preparation for submit
                $scope.field.prepareForSubmit = function (data) {
                    // Prepares each form entry individually
                    angular.forEach($scope.formEntries, function (formEntry) {
                        otFormService.prepareForSubmit(
                            formEntry.form,
                            formEntry.data
                        );
                    });
                    // Collects the data
                    if ($scope.formEntries) {
                        var formEntry = $scope.formEntries[0];
                        data[$scope.field.name] = {
                            id: formEntry.sourceId,
                            data: formEntry.data
                        };
                    }
                };

                // Loading of the form on selection
                $scope.$watch('sourceId', function (sourceId) {
                    selectSourceId(sourceId);
                });

                function selectSourceId(sourceId) {
                    var source = getSource(sourceId);
                    if (source) {
                        // Clones the form
                        var form = angular.copy(source.form);
                        // Prepares the data
                        var data = otFormService.prepareForDisplay(form);
                        // Sets the entry
                        $scope.formEntries = [
                            {
                                sourceId: sourceId,
                                form: form,
                                data: data
                            }
                        ];
                    } else {
                        $scope.formEntries = [];
                    }
                }

                function getSource(sourceId) {
                    // Gets the source definition
                    var filteredSources = $scope.field.sources.filter(function (s) {
                        return s.id == sourceId;
                    });
                    if (filteredSources.length) {
                        return filteredSources[0];
                    }
                }

            }
        };
    })
    .directive('otFieldServiceConfiguratorForm', function ($compile) {
        return {
            restrict: 'E',
            template: '<div></div>',
            scope: {
                form: '=',
                data: '=',
                formRoot: '='
            },
            link: function (scope, element) {
                scope.$watch('form', function () {
                    if (angular.isDefined(scope.form)) {
                        $compile('<ot-form form="form" data="data" form-root="formRoot"></ot-form>')(scope, function (cloned, scope) {
                            element.empty();
                            element.append(cloned);
                        });
                    }
                });
            }
        };
    })
;