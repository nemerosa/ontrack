angular.module('ot.dialog.versionInfo', [])
    .controller('otDialogVersionInfo', function ($scope, $modalInstance, versionInfo) {

        $scope.versionInfo = versionInfo;

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

    })
;