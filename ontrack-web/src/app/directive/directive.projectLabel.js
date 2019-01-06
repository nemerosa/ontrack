angular.module('ot.directive.projectLabel', [
    'ot.service.core'
])
    .directive('otProjectLabel', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.projectLabel.tpl.html',
            scope: {
                label: '=',
                colorBox: '@',
                action: '&'
            },
            controller: function ($scope) {
                $scope.onLabelAction = () => {
                    if ($scope.action) {
                        $scope.action();
                    }
                };
            }
        };
    })
;