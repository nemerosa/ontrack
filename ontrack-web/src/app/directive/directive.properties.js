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
                        // List of properties that could be added
                        var additions = [];
                        angular.forEach(properties.resources, function (property) {
                            if (property.editable && property.empty) {
                                additions.push(property);
                            }
                        });
                        scope.additions = additions;
                    });

                    // Adding a property
                    scope.addProperty = function (property) {
                        alert('Adding ' + property.typeDescriptor.name);
                    };
                }

                scope.$watch('entity', loadProperties);
            }
        };
    })
;