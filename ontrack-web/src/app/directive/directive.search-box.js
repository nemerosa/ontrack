angular.module('ot.directive.search-box', [
    'ot.service.graphql',
    'ot.service.search'
])
    .directive('otSearchBox', function (otGraphqlService, otSearchService) {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.search-box.tpl.html',
            scope: {
                boxId: '@',
                config: '='
            },
            controller: ($scope) => {
                let config = $scope.config;
                let resetOnSearch = config.resetOnSearch === undefined ? false : config.resetOnSearch;
                let otSearchBox = {
                    searchToken: config.token
                };
                // Default type
                otSearchBox.defaultResultType = otSearchService.defaultResultType;
                otSearchBox.selectedSearchResultType = angular.copy(otSearchBox.defaultResultType);
                // Selections
                otSearchBox.selectAllSearchType = () => {
                    otSearchBox.selectSearchType(otSearchBox.defaultResultType);
                };
                otSearchBox.selectSearchType = (type) => {
                    otSearchBox.selectedSearchResultType.id = type.id;
                    otSearchBox.selectedSearchResultType.name = type.name;
                    otSearchBox.selectedSearchResultType.description = type.description;
                    otSearchBox.selectedSearchResultType.feature = type.feature;
                };
                // Loading the types
                otSearchService.loadSearchResultTypes().then((searchResultTypes) => {
                    otSearchBox.searchResultTypes = searchResultTypes;
                    if (config.type) {
                        let selectedType = otSearchBox.searchResultTypes.find((it) => it.id === config.type);
                        if (selectedType) {
                            otSearchBox.selectSearchType(selectedType);
                        }
                    }
                });
                // OK
                $scope.otSearchBox = otSearchBox;
                // Performing the search
                $scope.performSearch = () => {
                    if ($scope.otSearchBox.searchToken) {
                        otSearchService.launchSearch($scope.otSearchBox.searchToken, otSearchBox.selectedSearchResultType.id);
                        if (resetOnSearch) {
                            $scope.otSearchBox.searchToken = "";
                        }
                    }
                };
            }
        };
    })
;