angular.module('ot.dialog.branch.clone', [
    'ot.service.core',
    'ot.service.form',
    'ot.service.structure',
    'ot.directive.branch.replacements'
])
    .controller('otDialogBranchClone', function ($scope, $modalInstance, $http, config, ot, otFormService) {
        // Inject the configuration into the scope
        $scope.config = config;
        // Selection object
        $scope.data = {
            replacements: [
                {
                    regex: config.sourceBranch.name
                }
            ]
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