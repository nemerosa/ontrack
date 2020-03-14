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
                    if (itemId === firstId()) {
                        return "ot-range-selector-selected-first";
                    } else if (itemId === secondId()) {
                        return "ot-range-selector-selected-second";
                    } else if (!firstId()) {
                        return "ot-range-selector-unselected-first";
                    } else if (!secondId()) {
                        return "ot-range-selector-unselected-second";
                    } else {
                        return "ot-range-selector-unselected-none";
                    }
                };

                $scope.rangeSelectorTitle = () => {
                    let itemId = id();
                    if (itemId === firstId() || itemId === secondId()) {
                        return "Click to not compare to this item";
                    } else if (!firstId() || !secondId()) {
                        return "Compare this item with another one";
                    } else {
                        return "Unselect other items to change the range";
                    }
                };

                $scope.rangeSelection = () => {
                    let itemId = id();
                    if (itemId === firstId()) {
                        $scope.model.first = undefined;
                    } else if (itemId === secondId()) {
                        $scope.model.second = undefined;
                    } else if (!firstId()) {
                        $scope.model.first = $scope.item;
                    } else if (!secondId()) {
                        $scope.model.second = $scope.item;
                    } else {
                        // Nothing to do
                    }
                };

                const id = () => $scope.item[$scope.id];

                const firstId = () => {
                    if ($scope.model.first) {
                        return $scope.model.first[$scope.id];
                    } else {
                        return undefined;
                    }
                };

                const secondId = () => {
                    if ($scope.model.second) {
                        return $scope.model.second[$scope.id];
                    } else {
                        return undefined;
                    }
                };

                const selected = () => {
                    let itemId = id();
                    return itemId === firstId() || itemId === secondId();
                };
            }
        };
    })
;