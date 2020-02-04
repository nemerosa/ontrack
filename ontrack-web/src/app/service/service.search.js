angular.module('ot.service.search', [
    'ot.service.core'
])
    .service('otSearchService', function ($http, $log, $rootScope, ot, otNotificationService) {
        let self = {};

        self.init = () => {
            $log.debug('[search] init');
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