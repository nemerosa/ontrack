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
                        // List of properties with values
                        var valueProperties = [];
                        // List of properties that could be added
                        var additions = [];
                        angular.forEach(properties.resources, function (property) {
                            if (property.editable && property.empty) {
                                additions.push(property);
                            }
                            if (!property.empty) {
                                valueProperties.push(property);
                            }
                        });
                        scope.additions = additions;
                        scope.valueProperties = valueProperties;
                    });

                    // Adding a property
                    scope.addProperty = function (property) {
                        otPropertiesService.addProperty(scope.entity, property);
                    };
                }

                scope.$watch('entity', function () {
                    if (scope.entity) {
                        loadProperties();
                    }
                });
            }
        };
    })
;