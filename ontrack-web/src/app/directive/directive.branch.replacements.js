angular.module('ot.directive.branch.replacements', [
])
    .directive('otBranchReplacements', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.branchReplacements.tpl.html',
            scope: {
                data: '='
            },
            controller: function ($scope) {
                $scope.copyReplacements = function (source, targets) {
                    angular.forEach(targets, function (target) {
                        target.length = 0;
                        angular.forEach(source, function (item) {
                            target.push(angular.copy(item));
                        });
                    });
                };
            }
        };
    })
;