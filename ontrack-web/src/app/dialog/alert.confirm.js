angular.module('ot.alert.confirm', [])
    .controller('otAlertConfirm', function ($scope, $modalInstance, alertConfig) {

        $scope.config = alertConfig;

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.submit = function () {
            $modalInstance.close('ok');
        };

    })
;