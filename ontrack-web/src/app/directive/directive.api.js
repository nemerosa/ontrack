angular.module('ot.directive.api', [
])
    .directive('otApiResource', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.api.resource.tpl.html',
            scope: {
                resource: '='
            },
            controller: function ($scope) {

            }
        };
    })
;