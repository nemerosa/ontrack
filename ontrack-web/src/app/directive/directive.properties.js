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

                // Loading the editable properties
                function loadProperties() {
                    ot.call($http.get(scope.entity._editableProperties)).then(function (propertyTypeDescriptors) {
                        scope.propertyTypeDescriptors = propertyTypeDescriptors;
                    });
                }

                scope.$watch('entity', loadProperties);
            }
        };
    })
;