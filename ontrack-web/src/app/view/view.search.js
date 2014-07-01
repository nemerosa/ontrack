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
    .controller('SearchCtrl', function ($stateParams, $scope, $http, ot) {

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
        });

    })
;