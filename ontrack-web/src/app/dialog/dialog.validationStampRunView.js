angular.module('ot.dialog.validationStampRunView', [
    'ot.service.structure'
])
    .controller('otDialogValidationStampRunView', function ($scope, $modalInstance, config, otStructureService) {
        // General configuration
        $scope.config = config;
        // Cancelling the dialog
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
        // Changing the status of a validation run
        $scope.changeStatus = function (validationRun) {
            // Closes this dialog first
            $scope.cancel();
            // Calling the service
            otStructureService.create(
                validationRun._validationRunStatusChange,
                'Status'
            ).then(function () {
                if (config.callbackOnStatusChange) {
                    config.callbackOnStatusChange();
                } else {
                    // Goes to the validation run page
                    location.href = '#/validationRun/' + validationRun.id;
                }
            });
        };
    })
;