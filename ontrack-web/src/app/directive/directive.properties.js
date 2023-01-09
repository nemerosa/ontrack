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
                    const uri = scope.entity.links && scope.entity.links._properties ? scope.entity.links._properties : scope.entity._properties;
                    ot.call($http.get(uri)).then(properties => {
                        scope.properties = properties;
                        // List of properties with values
                        const valueProperties = [];
                        // List of properties that could be added
                        const additions = [];
                        angular.forEach(properties.resources, property => {
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

                    // Getting the path to a property's template
                    scope.getTemplatePath = property => 'extension/' + property.typeDescriptor.feature.id + '/property/' + property.typeDescriptor.typeName + '.tpl.html';

                    // Adding a property
                    scope.addProperty = property => {
                        otPropertiesService.addProperty(property).then(loadProperties);
                    };

                    /**
                     * Editing a property.
                     *
                     * Note that editing a property is equivalent to create it.
                     */
                    scope.editProperty = property => {
                        scope.addProperty(property);
                    };

                    /**
                     * Deleting a property.
                     */
                    scope.deleteProperty = property => {
                        otPropertiesService.deleteProperty(property).then(loadProperties);
                    };
                }

                scope.$watch('entity', function () {
                    if (scope.entity && ((scope.entity.links && scope.entity.links._properties) || scope.entity._properties)) {
                        loadProperties();
                    }
                });
            }
        };
    })
;