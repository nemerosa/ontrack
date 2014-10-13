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
                        $scope.formEntry = {
                            form: form,
                            data: data
                        };
                    } else {
                        delete $scope.formEntry;
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