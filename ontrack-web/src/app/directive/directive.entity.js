angular.module('ot.directive.entity', [
    'ot.service.core',
    'ot.service.task'
])
    .directive('otEntityImage', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.entityImage.tpl.html',
            transclude: true,
            scope: {
                entity: '=',
                link: '@',
                title: '@',
                className: '@'
            }
        };
    })
    .directive('otEntityDisabled', function ($http, ot) {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.entityDisabled.tpl.html',
            transclude: true,
            scope: {
                entity: '=',
                callback: '&'
            },
            controller: function ($scope) {
                $scope.enableEntity = entity => {
                    let uri;
                    if (entity.links) {
                        uri = entity.links._enable;
                    } else {
                        uri = entity._enable;
                    }
                    if (uri) {
                        ot.pageCall($http.put(uri, entity)).then(() => {
                            entity.disabled = false;
                        }).then($scope.callback);
                    }
                };
            }
        };
    })
    .directive('otEntityEvents', function ($http, $log, ot, otTaskService) {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.entityEvents.tpl.html',
            scope: {
                entity: '='
            },
            link: function (scope) {
                scope.eventsLoaded = false;
                scope.renderEvent = function (event) {
                    return event.html;
                };
                scope.renderSince = function (eventTime) {
                    return moment(eventTime).fromNow();
                };
                scope.$watch('entity', function () {
                    if (scope.entity && scope.entity._events) {
                        scope.events = [];
                        // loadEvents(scope.entity._events);
                    }
                });
                scope.moreEvents = function () {
                    if (!scope.eventsLoaded) {
                        loadEvents(scope.entity._events);
                    } else if (scope.eventsResource.pagination.next) {
                        otTaskService.stop('events');
                        loadEvents(scope.eventsResource.pagination.next);
                    }
                };

                function loadEvents(uri) {
                    scope.loadingEvents = true;
                    ot.call($http.get(uri)).then(function (events) {
                        scope.eventsResource = events;
                        scope.events = scope.events.concat(events.resources);
                        scope.more = (events.resources.length > 0);
                        scope.eventsLoaded = true;
                    }).finally(function () {
                        scope.loadingEvents = false;
                    });
                }
            }
        };
    })
    .directive('otEntityDecorations', function ($http, ot) {
        function updateEntityDecorations(scope, entity) {
            ot.call($http.get(entity._decorations)).then(function (decorations) {
                scope.decorations = decorations.resources;
            });
        }

        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.entityDecorations.tpl.html',
            scope: {
                entity: '='
            },
            link: function (scope) {
                scope.$watch('entity', function () {
                    if (scope.entity) {
                        updateEntityDecorations(scope, scope.entity);
                    }
                });
            }
        };
    })
    .directive('otEntityEmbeddedDecorations', function ($http, ot) {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.entityDecorations.tpl.html',
            scope: {
                decorations: '='
            }
        };
    })
    .directive('otEntityExtra', function ($http, ot) {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.entityExtra.tpl.html',
            scope: {
                entity: '='
            },
            link: function (scope) {
                scope.$watch('entity', function () {
                    if (scope.entity && scope.entity._extra) {
                        scope.infosLoading = true;
                        ot.call($http.get(scope.entity._extra)).then(function (infos) {
                            scope.infosLoading = false;
                            scope.infos = infos;
                        });
                    }
                });
            }
        };
    })
    .directive('otBuildPromotionRuns', function ($modal) {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.buildPromotionRuns.tpl.html',
            scope: {
                promotionRuns: '='
            },
            controller: function ($scope) {
                $scope.displayPromotionRuns = function (promotionRun) {
                    $modal.open({
                        templateUrl: 'app/dialog/dialog.promotionRuns.tpl.html',
                        controller: 'otDialogPromotionRuns',
                        resolve: {
                            config: function () {
                                return {
                                    build: promotionRun.build,
                                    promotionLevel: promotionRun.promotionLevel,
                                    uri: promotionRun._all
                                };
                            }
                        }
                    });
                };
            }
        };
    })
    .directive('otValidationRunStatus', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.validationRunStatus.tpl.html',
            scope: {
                status: '='
            },
            link: function (scope) {
                scope.tooltip = function (validationRunStatus) {
                    const id = validationRunStatus.statusID.name;
                    const description = validationRunStatus.description;
                    if (description) {
                        if (validationRunStatus.creation && validationRunStatus.creation.user) {
                            const user = validationRunStatus.creation.user;
                            return `${id} - ${description} (${user})`;
                        } else {
                            return `${id} - ${description}`;
                        }
                    } else {
                        return id;
                    }
                };
            }
        };
    })
    .directive('otValidationStampRunView', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.validationStampRunView.tpl.html',
            scope: {
                validationStampRunView: '='
            }
        };
    })
    .directive('otValidationRunStatusNone', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.validationRunStatusNone.tpl.html'
        };
    })
    .directive('otSignature', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.signature.tpl.html',
            scope: {
                value: '=',
                user: '=',
                time: '='
            }
        };
    })
;