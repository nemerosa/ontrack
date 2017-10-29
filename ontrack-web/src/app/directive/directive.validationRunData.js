angular.module('ot.directive.validationRunData', [
    'ot.service.core'
])
    .directive('otValidationRunData', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.validationRunData.tpl.html',
            scope: {
                value: '='
            },
            controller: function ($scope) {
                $scope.getTemplatePath = (value) =>
                    `validationDataType/${value.id}.tpl.html`;
            }
        };
    })
;