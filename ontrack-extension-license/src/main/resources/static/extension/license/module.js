angular.module('ontrack.extension.license', [
    'ot.service.core'
])
    .config(function ($stateProvider) {
        $stateProvider.state('license', {
            url: '/extension/license/info',
            templateUrl: 'extension/license/info.tpl.html',
            controller: 'LicenseInfoCtrl'
        });
    })
    .controller('LicenseInfoCtrl', function ($scope, $http, ot) {
        const view = ot.view();
        view.title = "License information";

        ot.pageCall($http.get("/extension/license")).then(data => {
            $scope.license = data.license;
        });
    })
;