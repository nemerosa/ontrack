angular.module('ot.view.settings', [
    'ui.router',
    'ot.service.core',
    'ot.service.form'
])
    .config(function ($stateProvider) {
        $stateProvider.state('settings', {
            url: '/settings',
            templateUrl: 'app/view/view.settings.tpl.html',
            controller: 'SettingsCtrl'
        });
    })
    .controller('SettingsCtrl', function ($scope, $http, ot, otFormService) {
        var view = ot.view();
        view.title = "Settings";
        view.description = "General settings for the ontrack application";
        view.commands = [
            ot.viewCloseCommand('/home')
        ];

        // Loading of the settings
        function loadSettings() {
            ot.call($http.get('settings')).then(function (forms) {
                // Preparation of all forms
                angular.forEach(forms, function (describedForm) {
                    describedForm.data = otFormService.prepareForDisplay(describedForm.form);
                });
                // Displays the forms
                $scope.forms = forms;
            });
        }

        // Initialisation
        loadSettings();

        // Editing some settings
        $scope.editSettings = function (describedForm) {
            otFormService.display({
                form: describedForm.form,
                title: describedForm.title,
                description: describedForm.description,
                submit: function (data) {
                    return ot.call($http.put(describedForm.uri, data));
                }
            }).then(loadSettings);
        };
    })
;