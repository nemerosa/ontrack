angular.module('ot.directive.userMenuGroup', [])
    .directive('otUserMenuGroup', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.userMenuGroup.tpl.html',
            scope: {
                group: '='
            },
            link: function (scope) {
                scope.groupInitialized = false;
                if (scope.group && !scope.groupInitialized) {
                    const key = `user.menu.group.${scope.group.name}`;
                    const value = localStorage.getItem(key);
                    scope.group.collapsed = value === 'collapsed';
                    scope.groupInitialized = true;
                }
            },
            controller: function ($scope) {
                $scope.toggleGroup = () => {
                    $scope.group.collapsed = !$scope.group.collapsed;
                    const value = $scope.group.collapsed ? 'collapsed' : 'expanded';
                    localStorage.setItem(`user.menu.group.${$scope.group.name}`, value);
                };
            }
        };
    })
;