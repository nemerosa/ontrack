angular.module('ot.directive.api', [
])
    .directive('otApiResource', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.api.resource.tpl.html',
            scope: {
                resource: '='
            },
            link: function (scope) {
                scope.$watch('resource', function () {
                    if (scope.resource) {
                        var items = [];
                        angular.forEach(scope.resource, function (value, field) {
                            // Field names to exclude
                            if (field.charAt(0) == '$') {
                                // Excluding
                            }
                            // Link
                            else if (field.charAt(0) == '_' && angular.isString(value)) {
                                var linkName = field.substring(1);
                                items.push({
                                    type: 'link',
                                    name: linkName,
                                    link: value
                                });
                            }
                            // Array value
                            else if (angular.isArray(value)) {
                                items.push({
                                    type: 'array',
                                    name: field,
                                    value: value
                                });
                            }
                            // Object value
                            else if (angular.isObject(value)) {
                                items.push({
                                    type: 'object',
                                    name: field,
                                    value: value
                                });
                            }
                            // Simple value
                            else {
                                items.push({
                                    type: 'simple',
                                    name: field,
                                    value: value
                                });
                            }
                        });
                        scope.items = items;
                    }
                });
                scope.followResource = function (link) {
                    location.href = '#/api?link=' + link;
                };
            }
        };
    })
    .directive('otApiResourceObject', function ($compile) {
        return {
            restrict: 'E',
            template: '<div></div>',
            scope: {
                resource: '='
            },
            link: function (scope, element) {
                if (angular.isDefined(scope.resource)) {
                    $compile('<ot-api-resource resource="resource"></ot-api-resource>')(scope, function (cloned, scope) {
                        element.append(cloned);
                    });
                }
            }
        };
    })
;