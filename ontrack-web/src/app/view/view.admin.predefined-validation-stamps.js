angular.module('ot.view.admin.predefined-validation-stamps', [
    'ui.router',
    'ot.service.core'
])
    .config(function ($stateProvider) {
        $stateProvider.state('admin-predefined-validation-stamps', {
            url: '/admin-predefined-validation-stamps',
            templateUrl: 'app/view/view.admin.predefined-validation-stamps.tpl.html',
            controller: 'AdminPredefinedValidationStampsCtrl'
        });
    })

    .controller('AdminPredefinedValidationStampsCtrl', function ($scope, $http, ot, otFormService) {
        var view = ot.view();
        view.title = "Predefined validation stamps";
        view.commands = [
            {
                id: 'admin-predefined-validation-stamps-new',
                name: "New predefined validation stamp",
                cls: 'ot-command-new',
                action: newPredefinedValidationStamp
            },
            ot.viewCloseCommand('/home')
        ];

        function loadPredefinedValidationStamps() {

        }
        loadPredefinedValidationStamps();

        function newPredefinedValidationStamp() {

        }
    })

;