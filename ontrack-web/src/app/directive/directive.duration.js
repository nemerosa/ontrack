angular.module('ot.directive.duration', [

])
    .directive('otDuration', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.duration.tpl.html',
            scope: {
                duration: '='
            },
            link: function (scope) {
                scope.displayDuration = function (duration) {
                    return moment.duration(duration, "seconds").humanize();
                };
            }
        };
    })
;