angular.module('ot.directive.entity', [

])
    .directive('otEntityImage', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.entity.tpl.html',
            transclude: true,
            scope: {
                entity: '=',
                link: '@'
            }
        };
    })
;