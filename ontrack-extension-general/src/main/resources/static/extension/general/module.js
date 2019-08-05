angular.module('ontrack.extension.general', [
    'ot.service.core'
])
    .directive('otExtensionGeneralMetrics', function () {
        return {
            restrict: 'E',
            templateUrl: 'extension/general/directive.metrics.tpl.html',
            scope: {
                values: '='
            },
            controller: function ($scope) {
                $scope.limit = true;
                $scope.source = Object.entries($scope.values)
                    .map(function (entry) {
                        return {
                            key: entry[0],
                            value: entry[1]
                        };
                    })
                    .sort(function (a, b) {
                        let ak = a.key;
                        let bk = b.key;
                        if (ak < bk) {
                            return -1;
                        } else if (ak > bk) {
                            return 1;
                        } else {
                            return 0;
                        }
                    });
                $scope.display = [];

                function changeDisplay() {
                    if ($scope.limit) {
                        $scope.display = $scope.source.slice(0, 5);
                        $scope.more = ($scope.source.length > 5);
                    } else {
                        $scope.display = $scope.source;
                        $scope.more = false;
                    }
                }

                $scope.displayMore = function() {
                    $scope.limit = false;
                    changeDisplay();
                };

                changeDisplay();
            }
        };
    })
;