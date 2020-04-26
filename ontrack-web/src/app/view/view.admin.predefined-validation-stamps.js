angular.module('ot.view.admin.predefined-validation-stamps', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure'
])
    .config(function ($stateProvider) {
        $stateProvider.state('admin-predefined-validation-stamps', {
            url: '/admin-predefined-validation-stamps',
            templateUrl: 'app/view/view.admin.predefined-validation-stamps.tpl.html',
            controller: 'AdminPredefinedValidationStampsCtrl'
        });
    })

    .controller('AdminPredefinedValidationStampsCtrl', function ($scope, $http, ot, otFormService, otAlertService, otStructureService) {
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
            ot.pageCall($http.get('rest/admin/predefinedValidationStamps')).then(function (predefinedValidationStamps) {
                $scope.predefinedValidationStamps = predefinedValidationStamps;
            });
        }

        loadPredefinedValidationStamps();

        function newPredefinedValidationStamp() {
            otFormService.create($scope.predefinedValidationStamps._create, "New predefined validation stamp")
                .then(loadPredefinedValidationStamps);
        }

        // Editing a predefined validation stamp
        $scope.editValidationStamp = function (predefinedValidationStamp) {
            otFormService.update(predefinedValidationStamp._update, "Edit predefined validation stamp")
                .then(loadPredefinedValidationStamps);
        };

        // Deleting a predefined validation stamp
        $scope.deleteValidationStamp = function (predefinedValidationStamp) {
            otAlertService.confirm({
                title: "Predefined validation stamp deletion",
                message: "Do you really want to delete this validation stamp?"
            }).then(function () {
                ot.pageCall($http.delete(predefinedValidationStamp._delete)).then(loadPredefinedValidationStamps);
            });
        };

        // Updating the image for a predefined validation stamp
        $scope.editValidationStampImage = function (predefinedValidationStamp) {
            otStructureService.changeImage(predefinedValidationStamp, {
                title: 'Image for predefined validation stamp ' + predefinedValidationStamp.name
            }).then(loadPredefinedValidationStamps);
        };

    })

;