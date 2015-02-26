angular.module('ot.dialog.validationstampfilter', [
    'ot.service.core',
    'ot.service.form'
])
    .controller('otDialogValidationStampFilter', function ($scope, $modalInstance, $http, config, ot, otFormService) {
        // Inject the configuration into the scope
        $scope.config = config;

        // Toggles selection
        $scope.toggleValidationStampSelection = function (validationStamp) {
            validationStamp.selected = !validationStamp.selected;
        };

        // Selects all validation stamps
        $scope.selectAllValidationStamps = function () {
            angular.forEach($scope.config.validationStamps, function (stamp) {
                stamp.selected = true;
            });
        };

        // Selects no validation stamp
        $scope.selectNoValidationStamp = function () {
            angular.forEach($scope.config.validationStamps, function (stamp) {
                stamp.selected = false;
            });
        };

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