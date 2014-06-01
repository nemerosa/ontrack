angular.module('ot.view.settings', [
    'ui.router',
    'ot.service.core'
])
    .config(function ($stateProvider) {
        $stateProvider.state('settings', {
            url: '/settings',
            templateUrl: 'app/view/view.settings.tpl.html',
            controller: 'SettingsCtrl'
        });
    })
    .controller('SettingsCtrl', function ($scope, $http, ot) {
        var view = ot.view();
        view.title = "Settings";
        view.description = "General settings for the ontrack application";
        view.commands = [
            ot.viewCloseCommand('/home')
        ];

        // Loading of the settings
        function loadSettings() {
            ot.call($http.get('settings')).then(function (forms) {
                $scope.forms = forms;
            });
        }

        // Initialisation
        loadSettings();
    })
;