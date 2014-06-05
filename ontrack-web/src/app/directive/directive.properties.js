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

                // Loading the properties
                function loadProperties() {
                    ot.call($http.get(scope.entity._properties)).then(function (properties) {
                        scope.properties = properties;
                        // At least one property editable and not set?
                        var addition = false;
                        angular.forEach(properties.resources, function (property) {
                            addition = addition || (property.editable && property.empty);
                        });
                        scope.addition = addition;
                    });
                }

                scope.$watch('entity', loadProperties);
            }
        };
    })
;