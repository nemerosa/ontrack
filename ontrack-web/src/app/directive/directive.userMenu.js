angular.module('ot.directive.userMenu', [
    'ot.service.core'
])
    .directive('otUserMenu', function ($document, $http, ot) {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.userMenu.tpl.html',
            scope: {
                active: '=',
                closeMenu: '&',
                menu: '='
            },
            controller: function ($scope, $element) {
                angular.element($document).on('mouseup touchend', function(event) {
                    if ($scope.active) {
                        const offCanvas = $element.find('.off-canvas');
                        if (!offCanvas.is(event.target) && offCanvas.has(event.target).length === 0) {
                            const body = $document.find('body');
                            $scope.$apply(() => {
                                $scope.closeMenu()();
                            });
                            body.removeClass('off-canvas-active');
                        }
                    }
                });
                $scope.$watch('active', () => {
                    const body = $document.find('body');
                    if ($scope.active) {
                        body.addClass('off-canvas-active');
                    } else {
                        body.removeClass('off-canvas-active');
                    }
                });
                $scope.onAfterAction = () => {
                    $scope.closeMenu()();
                };

                $scope.nextUI = () => {
                    location.href = '/ui';
                };

                $scope.logout = function () {
                    ot.call($http.post("logout", {})).then(() => {
                        location.href = '/login?logout';
                    });
                };
            }
        };
    })
;