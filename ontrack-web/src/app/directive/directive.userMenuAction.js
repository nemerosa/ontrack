angular.module('ot.directive.userMenuAction', [])
    .directive('otUserMenuAction', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.userMenuAction.tpl.html',
            scope: {
                action: '='
            },
            controller: function ($scope) {
            }
        };
    })
;