angular.module('ot.service.search', [
    'ot.service.core'
])
    .service('otSearchService', function ($http, $location, $log, $q, $rootScope, ot, otNotificationService) {
        let self = {};

        self.launchSearch = (token, type) => {
            let request = {token: token};
            if (type) {
                request.type = type;
            }
            $location.path("/search").search(request);
        };

        self.defaultResultType = {
            id: "",
            name: "Any",
            description: "Searching in all entities",
            feature: undefined
        };

        self.loadSearchResultTypes = () => {
            let d = $q.defer();
            ot.call($http.get('rest/search/types')).then(
                function success(searchResultTypes) {
                    $log.debug('[search] result types: ', searchResultTypes);
                    otNotificationService.clear();
                    d.resolve(searchResultTypes);
                },
                function error(message) {
                    $log.debug('[search] init - no search result type', message);
                    otNotificationService.error("Cannot load the search result types. Please try later.");
                    d.reject();
                }
            );
            return d.promise;
        };

        return self;
    })
;