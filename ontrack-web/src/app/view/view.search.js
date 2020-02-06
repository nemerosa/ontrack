angular.module('ot.view.search', [
    'ui.router',
    'ot.service.core'
])
    .config(function ($stateProvider) {
        $stateProvider.state('search', {
            url: '/search',
            templateUrl: 'app/view/view.search.tpl.html',
            controller: 'SearchCtrl'
        });
    })
    .controller('SearchCtrl', function ($location, $stateParams, $scope, $rootScope, $http, $log, ot) {

        // Search token
        $scope.token = $location.search().token;

        // Search type
        $scope.type = $location.search().type;

        // Request
        let request = {
            token: $scope.token
        };
        if ($scope.type) {
            request.type = $scope.type;
        }

        // View definition
        let view = ot.view();
        view.title = "Search results for \"" + $scope.token + "\"";
        view.commands = [ot.viewCloseCommand('/home')];

        // Launching the search
        ot.pageCall($http.post('search', request)).then(function (results) {
            $scope.searchDone = true;
            $scope.results = results;
            // If only one result, switches directly to the correct page
            if (results.length === 1) {
                var result = results[0];
                $log.info('[search] Autoredirect for 1 result: ', result);
                if (result.page) {
                    window.location = result.page;
                } else {
                    $log.error('[search] Could not find any page in the result:', result);
                }
            }
        });

    })
;