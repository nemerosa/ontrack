angular.module('ontrack.extension.oidc', [
    'ot.service.core'
])
    .config(function ($stateProvider) {
        $stateProvider.state('oidc-settings', {
            url: '/extension/oidc/oidc-settings',
            templateUrl: 'extension/oidc/oidc-settings.tpl.html',
            controller: 'OidcSettingsCtrl'
        });
    })
    .controller('OidcSettingsCtrl', function ($scope, $http, ot) {
        const view = ot.view();
        view.title = "OIDC Providers";
        view.breadcrumbs = ot.homeBreadcrumbs();
        view.commands = [
            ot.viewCloseCommand('/home')
        ];

        $scope.loadingProviders = false;

        const loadProviders = () => {
            $scope.loadingProviders = true;
            ot.pageCall($http.get("extension/oidc/providers")).then((resources) => {
                $scope.resources = resources;
            }).finally(() => {
                $scope.loadingProviders = false;
            });
        };

        loadProviders();
    })
;