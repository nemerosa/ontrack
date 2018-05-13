angular.module('ot.directive.creation', [])
    .directive('otCreation', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.creation.tpl.html',
            scope: {
                creation: '='
            }
        };
    })
;