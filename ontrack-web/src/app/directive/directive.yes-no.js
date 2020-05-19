angular.module('ot.directive.yes-no', [])
    .directive('otYesNo', function () {
        return {
            restrict: 'E',
            scope: {
                value: '='
            },
            templateUrl: 'app/directive/directive.yes-no.tpl.html'
        };
    })
;