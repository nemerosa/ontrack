angular.module('ot.service.search', [
    'ot.service.core'
])
    .service('otSearchService', function ($http, $log, $rootScope, ot, otNotificationService) {
        let self = {};

        self.launchSearch = (token) => {
            let request = {token: token};
            let uri = `#/search?token=${encodeURIComponent(token)}`;
            if ($rootScope.selectedSearchResultType.id) {
                uri += `&type=${encodeURIComponent($rootScope.selectedSearchResultType.id)}`;
            }
            location.href = uri;
        };

        self.init = () => {
            $log.debug('[search] init');
            $rootScope.selectedSearchResultType = {
                id: "",
                name: "Any"
            };
            $rootScope.selectAllSearchType = () => {
                $rootScope.selectedSearchResultType.id = "";
                $rootScope.selectedSearchResultType.name = "Any";
                $rootScope.selectedSearchResultType.feature = undefined;
            };
            $rootScope.selectSearchType = (type) => {
                $rootScope.selectedSearchResultType.id = type.id;
                $rootScope.selectedSearchResultType.name = type.name;
                $rootScope.selectedSearchResultType.feature = type.feature;
            };
            ot.call($http.get('search/types')).then(
                function success(searchResultTypes) {
                    $log.debug('[search] result types: ', searchResultTypes);
                    // Saves the search result types into the scope
                    $rootScope.searchResultTypes = searchResultTypes;
                    // Clears the error
                    otNotificationService.clear();
                },
                function error(message) {
                    $log.debug('[search] init - no search result type', message);
                    // Empty result types
                    $rootScope.searchResultTypes = [];
                    // Displays a general error
                    otNotificationService.error("Cannot load the search result types. Please try later.");
                }
            );
        };

        return self;
    })
;