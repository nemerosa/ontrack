angular.module('ot.dialog.branch.bulk', [
    'ot.service.core',
    'ot.service.form',
    'ot.service.structure',
    'ot.directive.branch.replacements'
])
    .controller('otDialogBranchBulk', function ($scope, $modalInstance, $http, config, ot, otStructureService, otFormService) {
        // Inject the configuration into the scope
        $scope.config = config;
        // Selection object
        $scope.data = {
            replacements: [{
                replacement: config.branch.name
            }]
        };
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