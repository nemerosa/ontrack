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

        // Loading of the settings
        function loadSettings(activeId) {
            ot.call($http.get('rest/settings')).then(function (forms) {
                view.commands = [
                    ot.viewCloseCommand('/home')
                ];
                // Preparation of all forms
                angular.forEach(forms.resources, function (describedForm) {
                    describedForm.data = otFormService.prepareForDisplay(describedForm.form);
                    describedForm.active = activeId && describedForm.id === activeId;
                });
                // Displays the forms
                $scope.forms = forms.resources;
            });
        }

        // Initialisation
        loadSettings("general-security");

        // Displays some settings
        $scope.showSettings = (describedForm) => {
            $scope.forms.forEach((it) => it.active = false);
            describedForm.active = true;
        };

        // Editing some settings
        $scope.editSettings = function (describedForm) {
            otFormService.display({
                form: describedForm.form,
                title: describedForm.title,
                description: describedForm.description,
                submit: function (data) {
                    return ot.call($http.put(describedForm.uri, data));
                }
            }).then(() => {
                loadSettings(describedForm.id);
            });
        };

        // Visibility of the fields
        $scope.isFieldVisible = function (form, field) {
            if (field.visibleIf) {
                return otFormService.getFieldValue(form, field.visibleIf);
            } else {
                return true;
            }
        };
    })
;