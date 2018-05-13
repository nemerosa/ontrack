angular.module('ot.directive.duration', [

])
    .directive('otDuration', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.duration.tpl.html',
            scope: {
                duration: '=',
                detail: '@'
            },
            link: function (scope) {
                scope.displayDuration = function (duration) {
                    if (duration === 0 || duration === 1) {
                        return duration + " second";
                    } else if (duration < 60) {
                        return duration + " seconds";
                    } else {
                        let display = moment.duration(duration, "seconds").humanize();
                        if (scope.detail !== "false") {
                            display += " (" + duration + " s)";
                        }
                        return display;
                    }
                };
            }
        };
    })
;