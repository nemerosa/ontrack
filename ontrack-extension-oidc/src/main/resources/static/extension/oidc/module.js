angular.module('ontrack.extension.oidc', [
    'ot.service.core',
    'ot.service.form'
])
    .config(function ($stateProvider) {
        $stateProvider.state('oidc-settings', {
            url: '/extension/oidc/oidc-settings',
            templateUrl: 'extension/oidc/oidc-settings.tpl.html',
            controller: 'OidcSettingsCtrl'
        });
    })
    .controller('OidcSettingsCtrl', function ($scope, $http, ot, otFormService, otAlertService) {
        const view = ot.view();
        view.title = "OIDC Providers";
        view.breadcrumbs = ot.homeBreadcrumbs();
        view.commands = [
            ot.viewCloseCommand('/home')
        ];

        let viewInitialized = false;

        $scope.loadingProviders = false;

        const loadProviders = () => {
            $scope.loadingProviders = true;
            ot.pageCall($http.get("extension/oidc/providers")).then((resources) => {
                $scope.resources = resources;
                if (!viewInitialized) {
                    viewInitialized = true;
                    view.commands.push({
                        condition: function () {
                            return $scope.resources._create;
                        },
                        id: 'createProvider',
                        name: "Create providre",
                        cls: 'ot-command-new',
                        action: $scope.createProvider
                    });
                }
            }).finally(() => {
                $scope.loadingProviders = false;
            });
        };

        loadProviders();

        $scope.createProvider = () => {
            otFormService.create($scope.resources._create, "Create provider").then(loadProviders);
        };

        $scope.deleteProvider = (provider) => {
              otAlertService.confirm({
                  title: "OIDC provider deletion",
                  message: `Do you want to delete the ${provider.name} provider? It won't be available for authentication any longer.`
              }).then(() => ot.pageCall($http.delete(provider._delete))).then(loadProviders);
        };
    })
;