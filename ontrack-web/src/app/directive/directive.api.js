angular.module('ot.directive.api', [
])
    .directive('otApiResource', function ($state) {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.api.resource.tpl.html',
            scope: {
                resource: '=',
                showLinks: '='
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
                                if (value.length > 0 && angular.isObject(value[0])) {
                                    items.push({
                                        type: 'array',
                                        name: field,
                                        value: value
                                    });
                                } else {
                                    items.push({
                                        type: 'array-simple',
                                        name: field,
                                        value: value
                                    });
                                }
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
                    $state.go('api', {link: encodeURIComponent(link)});
                };
                scope.toggleCollapsed = function (item) {
                    if (['object', 'array', 'array-simple'].indexOf(item.type) >= 0) {
                        console.log('toggle: ', item);
                        item.collapsed = !item.collapsed;
                    }
                };
            }
        };
    })
    .directive('otApiResourceObject', function ($compile) {
        return {
            restrict: 'E',
            template: '<div></div>',
            scope: {
                resource: '=',
                showLinks: '='
            },
            link: function (scope, element) {
                if (angular.isDefined(scope.resource)) {
                    $compile('<ot-api-resource resource="resource" show-links="showLinks"></ot-api-resource>')(scope, function (cloned, scope) {
                        element.append(cloned);
                    });
                }
            }
        };
    })
;