angular.module('ot.view.api', [
    'ui.router',
    'ot.service.core',
    'ot.directive.api'
])
    .config(function ($stateProvider) {
        $stateProvider.state('api', {
            url: '/api',
            templateUrl: 'app/view/view.api.tpl.html',
            controller: 'APICtrl'
        });
    })
    .controller('APICtrl', function ($http, $location, $scope, $state, ot) {
        var view = ot.view();
        view.title = "API";

        // Link to display
        $scope.link = $location.search().link;

        // Loading the API
        function loadAPI(link) {
            $scope.apiLoading = true;
            ot.pageCall($http.get(link)).then(function (resource) {
                $scope.resource = resource;
            }).finally(function () {
                $scope.apiLoading = false;
            });
        }

        // Loads the API
        loadAPI($scope.link);
    })
;