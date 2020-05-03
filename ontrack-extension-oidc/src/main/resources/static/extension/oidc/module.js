angular.module('ontrack.extension.oidc', [
    'ot.service.core',
    'ot.service.graphql'
])
    .config(function ($stateProvider) {
        $stateProvider.state('oidc-settings', {
            url: '/extension/oidc/oidc-settings',
            templateUrl: 'extension/oidc/oidc-settings.tpl.html',
            controller: 'OidcSettingsCtrl'
        });
    })
    .controller('OidcSettingsCtrl', function ($scope, $http, ot, otGraphqlService) {
        const view = ot.view();
        view.title = "OIDC Providers";
        view.breadcrumbs = ot.homeBreadcrumbs();
        view.commands = [
            ot.viewCloseCommand('/home')
        ];
    })
;