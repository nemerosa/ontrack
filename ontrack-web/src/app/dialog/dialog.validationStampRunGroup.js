angular.module('ot.dialog.validationStampRunGroup', [
])
    .controller('otDialogValidationStampRunGroup', function ($scope, $modalInstance, config) {
        // General configuration
        $scope.config = config;
        // Cancelling the dialog
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
        // Opening a specific validation run
        $scope.selectValidationRun = (validation) => {
            // Closes this dialog first
            $scope.cancel();
            // Opening the validation only
            $scope.config.callbackOnRunOpen(validation.validationStamp);
        };
    })
;