angular.module('ot.directive.field.replacements', [

])
    .directive('otFieldReplacements', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.field.replacements.tpl.html',
            transclude: true,
            scope: {
                replacements: '='
            },
            controller: function ($scope) {
                // Default value
                if (!$scope.replacements) {
                    $scope.replacements = [];
                }
                // Adding an entry
                $scope.addEntry = function (replacements) {
                    replacements.push({
                        regex: '',
                        replacement: ''
                    });
                };
                // Removing an entry
                $scope.removeEntry = function (replacements, replacement) {
                    var idx = replacements.indexOf(replacement);
                    if (idx >= 0) {
                        replacements.splice(idx, 1);
                    }
                };
            }
        };
    })
;