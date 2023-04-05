angular.module('ot.directive.userMenuAction', [])
    .directive('otUserMenuAction', function ($rootScope) {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.userMenuAction.tpl.html',
            scope: {
                action: '=',
                closeAction: '&'
            },
            controller: function ($scope) {
                $scope.onActionClick = () => {
                    if ($scope.action.type === 'LINK') {
                        $scope.closeAction();
                        window.location.href = `#${ $scope.action.uri }`;
                    } else if ($scope.action.type === 'FORM') {
                        $scope.closeAction();
                        $rootScope.showActionForm($scope.action);
                    }
                };
            }
        };
    })
;