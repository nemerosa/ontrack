angular.module('ot.directive.userMenuGroup', [])
    .directive('otUserMenuGroup', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.userMenuGroup.tpl.html',
            scope: {
                group: '='
            },
            controller: function ($scope) {
                $scope.toggleGroup = () => {
                    $scope.group.collapsed = !$scope.group.collapsed;
                };
            }
        };
    })
;