angular.module('ot.dialog.validationstampfilter', [
    'ot.service.core',
    'ot.service.form'
])
    .controller('otDialogValidationStampFilter', function ($scope, $modalInstance, $http, config, ot, otFormService) {
        // Inject the configuration into the scope
        $scope.config = config;
        // Cancelling the dialog
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
        // Submitting the dialog
        $scope.submit = function (isValid) {
            if (isValid) {
                //otFormService.submitDialog(
                //    config.submit,
                //    $scope.data,
                //    $modalInstance,
                //    $scope
                //);
            }
        };
    })
;