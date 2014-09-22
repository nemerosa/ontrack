angular.module('ot.directive.entity', [
    'ot.service.core',
    'ot.service.event'
])
    .directive('otEntityImage', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.entityImage.tpl.html',
            transclude: true,
            scope: {
                entity: '=',
                link: '@'
            }
        };
    })
    .directive('otEntityEvents', function ($http, ot, otEventService) {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.entityEvents.tpl.html',
            scope: {
                entity: '='
            },
            link: function (scope) {
                scope.$watch('entity', function () {
                    if (scope.entity) {
                        ot.call($http.get(scope.entity._events)).then(function (events) {
                            scope.renderEvent = otEventService.renderEvent;
                            scope.renderSince = function (eventTime) {
                                return moment(eventTime).fromNow();
                            };
                            scope.events = events;
                        });
                    }
                });
            }
        };
    })
    .directive('otEntityDecorations', function ($http, ot) {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.entityDecorations.tpl.html',
            transclude: true,
            scope: {
                entity: '='
            },
            link: function (scope) {
                scope.$watch('entity', function () {
                    if (scope.entity) {
                        ot.call($http.get(scope.entity._decorations)).then(function (decorations) {
                            scope.decorations = decorations;
                            scope.decorationImagePath = function (decoration) {
                                return 'assets/extension/decoration/' + decoration.decorationType + '/' + decoration.id + '.png';
                            };
                            scope.decorationClassName = function (decoration) {
                                return (decoration.decorationType + '.' + decoration.id).replace(/\./g, '-');
                            };
                        });
                    }
                });
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
                    if (scope.entity) {
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