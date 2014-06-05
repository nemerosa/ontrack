angular.module('ot.directive.properties', [

])
    .directive('otEntityProperties', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.properties.tpl.html',
            scope: {
                entity: '@',
                entityId: '@'
            }
        };
    })
;