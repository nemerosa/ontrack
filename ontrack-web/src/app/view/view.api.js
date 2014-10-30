angular.module('ot.view.api', [
    'ui.router',
    'ot.service.core'
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
    })
;