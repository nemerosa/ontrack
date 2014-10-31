angular.module('ot.view.api', [
    'ui.router',
    'ot.service.core',
    'ot.directive.api'
])
    .config(function ($stateProvider) {
        $stateProvider.state('api', {
            url: '/api/{link}',
            templateUrl: 'app/view/view.api.tpl.html',
            controller: 'APICtrl'
        });
    })
    .controller('APICtrl', function ($http, $location, $scope, $state, $stateParams, ot) {
        var view = ot.view();
        view.title = "API";

        // Link to display
        $scope.link = decodeURIComponent($stateParams.link);

        // Loading the API
        function loadAPI(link) {
            $scope.apiLoading = true;

            // Gets the relative path (neat!)
            var a = document.createElement('a');
            a.href = link;
            var path = a.pathname;

            // Calls

            ot.pageCall($http.get("api/describe", {params: {path: path}}))
                .then(function (description) {
                    return ot.pageCall($http.get(link));
                })
                .then(function (resource) {
                    $scope.resource = resource;
                }).finally(function () {
                    $scope.apiLoading = false;
                });
        }

        // Loads the API
        loadAPI($scope.link);
    })
;