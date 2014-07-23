angular.module('ontrack', [
    'ui.bootstrap',
    'ui.router',
    'ngSanitize',
    // Templates as JS
    'ot.templates',
    // Views
    'ot.view.home'
])
    // Routing configuration
    .config(function ($stateProvider, $urlRouterProvider) {
        // For any unmatched url, redirect to /home
        $urlRouterProvider.otherwise("/home");
    })
    // Main controller
    .controller('AppCtrl', function ($scope, $http) {
        $http.get('assets/version.json').success(function (info) {
            $scope.info = info;
        });
    })
;