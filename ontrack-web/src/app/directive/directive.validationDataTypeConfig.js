angular.module('ot.directive.validationDataTypeConfig', [
    'ot.service.core'
])
    .directive('otValidationDataTypeConfig', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.validationDataTypeConfig.tpl.html',
            scope: {
                value: '='
            },
            controller: function ($scope) {
                $scope.getTemplatePath = (value) =>
                    `extension/${value.descriptor.feature.id}/validationDataType/${value.descriptor.id}-config.tpl.html`;
            }
        };
    })
;