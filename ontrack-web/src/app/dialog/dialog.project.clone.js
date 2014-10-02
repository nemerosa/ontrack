angular.module('ot.dialog.project.clone', [
    'ot.service.core',
    'ot.service.form',
    'ot.directive.branch.replacements'
])
    .controller('otDialogProjectClone', function ($scope, $modalInstance, $http, config, ot, otFormService) {
        // Inject the configuration into the scope
        $scope.config = config;
        // Selection object
        $scope.data = {
            replacements: [
                {
                    regex: config.sourceProject.name
                }
            ]
        };
        // Loading the branches of the source project
        ot.call($http.get(config.sourceProject._branches)).then(function (branches) {
            $scope.branches = branches.resources;
        });
        // Cancelling the dialog
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
        // Submitting the dialog
        $scope.submit = function (isValid) {
            if (isValid) {
                otFormService.submitDialog(
                    config.submit,
                    $scope.data,
                    $modalInstance,
                    $scope
                );
            }
        };
    })
;