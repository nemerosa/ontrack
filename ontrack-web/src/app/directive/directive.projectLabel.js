angular.module('ot.directive.projectLabel', [
    'ot.service.core',
    'ot.service.label'
])
    .directive('otProjectLabel', function (otLabelService) {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.projectLabel.tpl.html',
            scope: {
                label: '=',
                colorBox: '@',
                action: '&'
            },
            controller: function ($scope) {
                $scope.formatLabel = otLabelService.formatLabel;
                $scope.onLabelAction = () => {
                    if ($scope.action) {
                        $scope.action();
                    }
                };
            }
        };
    })
;