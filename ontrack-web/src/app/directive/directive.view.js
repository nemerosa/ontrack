angular.module('ot.directive.view', [

])
    .directive('otView', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.view.tpl.html',
            transclude: true
        };
    })
;