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
    .controller('SearchCtrl', function ($http, ot) {
    })
;