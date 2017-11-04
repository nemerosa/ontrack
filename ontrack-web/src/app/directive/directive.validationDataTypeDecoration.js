angular.module('ot.directive.validationDataTypeDecoration', [
    'ot.service.core'
])
    .directive('otValidationDataTypeDecoration', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.validationDataTypeDecoration.tpl.html',
            scope: {
                value: '='
            }
        };
    })
;