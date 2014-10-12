angular.module('ot.directive.field.multiForm', [

])
    .directive('otFieldMultiForm', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.field.multiForm.tpl.html',
            scope: {
                field: '=',
                data: '=',
                formRoot: '='
            },
            controller: function ($scope) {
            }
        };
    })
;