angular.module('ot.dialog.form', [
    'ot.service.form'
])
    .controller('otDialogForm', function ($scope, $modalInstance, config, otFormService) {
        // General configuration
        $scope.config = config;
        // Form data
        $scope.data = otFormService.prepareForDisplay(config.form);
        // Cancelling the dialog
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
        // Submitting the dialog
        $scope.submit = function (isValid) {
            if (isValid) {
                otFormService.prepareForSubmit(config.form, $scope.data);
                // Submit
                var submit = config.formConfig.submit($scope.data);
                if (submit === true) {
                    $modalInstance.close('ok');
                } else if (angular.isString(submit)) {
                    $scope.message = submit;
                } else {
                    submit.then(
                        function success() {
                            $modalInstance.close('ok');
                        },
                        function error(message) {
                            $scope.message = message;
                        });
                }
            }
        };
    })
;