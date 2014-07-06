angular.module('ot.view.search', [
    'ui.router',
    'ot.service.core'
])
    .config(function ($stateProvider) {
        $stateProvider.state('search', {
            url: '/search/{token}',
            templateUrl: 'app/view/view.search.tpl.html',
            controller: 'SearchCtrl'
        });
    })
    .controller('SearchCtrl', function ($location, $stateParams, $scope, $http, ot) {

        // Search token
        $scope.token = $stateParams.token;

        // View definition
        var view = ot.view();
        view.title = "Search results for \"" + $scope.token + "\"";
        view.commands = [ ot.viewCloseCommand('/home') ];

        // Launching the search
        ot.pageCall($http.post('search', {token: $scope.token})).then(function (results) {
            $scope.searchDone = true;
            $scope.results = results;
            // If only one result, switches directly to the correct page
            if (results.length == 1) {
                $location.path(results[0].hint);
            }
        });

    })
;