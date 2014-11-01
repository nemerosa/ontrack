angular.module('ot.view.api-doc', [
    'ui.router',
    'ot.service.core',
    'ot.directive.api'
])
    .config(function ($stateProvider) {
        $stateProvider.state('api-doc', {
            url: '/api-doc',
            templateUrl: 'app/view/view.api-doc.tpl.html',
            controller: 'APIDocCtrl'
        });
    })
    .controller('APIDocCtrl', function ($http, $scope, ot) {
        var view = ot.view();
        view.title = "API Documentation";
        view.commands = [
            ot.viewCloseCommand('home')
        ];
    })
;