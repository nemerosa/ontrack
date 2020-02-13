angular.module('ot.directive.range-selector', [])
    .directive('otRangeSelector', function () {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.range-selector.tpl.html',
            scope: {
                model: '=',
                item: '=',
                id: '@'
            },
            controller: ($scope) => {
                $scope.rangeSelectorClass = () => {
                    let itemId = id();
                    if (itemId === $scope.model.firstId) {
                        return "ot-range-selector-selected-first";
                    } else if (itemId === $scope.model.secondId) {
                        return "ot-range-selector-selected-second";
                    } else if (!$scope.model.firstId) {
                        return "ot-range-selector-unselected-first";
                    } else if (!$scope.model.secondId) {
                        return "ot-range-selector-unselected-second";
                    } else {
                        return "ot-range-selector-unselected-none";
                    }
                };

                $scope.rangeSelectorTitle = () => {
                    if (!selected() && (!$scope.model.firstId || !$scope.model.secondId)) {
                        return "Compare this item with another item";
                    } else {
                        return "";
                    }
                };

                $scope.rangeSelection = () => {
                    let itemId = id();
                    if (itemId === $scope.model.firstId) {
                        $scope.model.firstId = undefined;
                    } else if (itemId === $scope.model.secondId) {
                        $scope.model.secondId = undefined;
                    } else if (!$scope.model.firstId) {
                        $scope.model.firstId = itemId;
                    } else if (!$scope.model.secondId) {
                        $scope.model.secondId = itemId;
                    } else {
                        // Nothing to do
                    }
                };

                const id = () => $scope.item[$scope.id];

                const selected = () => {
                    let itemId = id();
                    return itemId === $scope.model.firstId || itemId === $scope.model.secondId;
                };
            }
        };
    })
;