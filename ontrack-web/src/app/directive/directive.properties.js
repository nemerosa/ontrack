angular.module('ot.directive.properties', [
    'ot.service.properties',
    'ot.service.core'
])
    .directive('otEntityProperties', function ($http, ot, otPropertiesService) {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.properties.tpl.html',
            scope: {
                entity: '='
            },
            link: function (scope) {
                scope.$watch('entity', function () {
                    if (scope.entity) {
                        // Loading the editable properties
                        ot.call($http.get(scope.entity._editableProperties)).then(function (propertyTypeDescriptors) {
                            scope.propertyTypeDescriptors = propertyTypeDescriptors;
                            // Edition of properties
                            scope.editProperties = function () {
                                alert('Editing the properties');
                            };
                        });
                    }
                });
            }
        };
    })
;