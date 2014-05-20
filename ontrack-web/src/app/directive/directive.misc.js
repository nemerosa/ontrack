angular.module('ot.directive.misc', [

])
    .directive('otNoentry', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.noentry.tpl.html',
            transclude: true,
            scope: {
                list: '='
            }
        };
    })
    .directive('otSectionTitle', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.sectionTitle.tpl.html',
            transclude: true
        };
    })
    .directive('otFileModel', function ($parse) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var model = $parse(attrs.otFileModel);
                var modelSetter = model.assign;
                element.bind('change', function () {
                    scope.$apply(function () {
                        modelSetter(scope, element[0].files[0]);
                    });
                });
            }
        };
    })
;