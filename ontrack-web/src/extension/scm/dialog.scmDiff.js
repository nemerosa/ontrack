angular.module('ontrack.extension.scm.dialog.diff', [])
    .controller('otExtensionScmDialogDiff', function ($scope, $modalInstance, config) {
        // General configuration
        $scope.config = config;
        // Cancelling the dialog
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    })
;