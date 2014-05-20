angular.module('ot.dialog.image', [])
    .controller('otDialogImage', function ($scope, $modalInstance, config) {
        // General configuration
        $scope.config = config;
        // Cancelling the dialog
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
        // Submitting the dialog
        $scope.submit = function (file) {
            if (file) {
                config.submit(file).then(
                    function success() {
                        $modalInstance.close('ok');
                    },
                    function error(message) {
                        $scope.message = message;
                    });
            }
        };
    })
;