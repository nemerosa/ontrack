angular.module('ot.dialog.validationStampRunView', [])
    .controller('otDialogValidationStampRunView', function ($scope, $modalInstance, config) {
        // General configuration
        $scope.config = config;
        // Cancelling the dialog
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    })
;