angular.module('ot.view.home', [
    'ui.router'
])
    .config(function ($stateProvider) {
        $stateProvider.state('home', {
            url: '/home',
            templateUrl: 'view/view.home.tpl.html'
        });
    })
;