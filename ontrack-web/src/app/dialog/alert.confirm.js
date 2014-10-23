angular.module('ot.alert.confirm', [])
    .controller('otAlertConfirm', function ($scope, $modalInstance, $document, alertConfig) {

        $scope.config = alertConfig;

        var keyHandler = function (e) {
            if (e.which == 13) {
                $scope.submit();
                e.preventDefault();
            }
        };
        $document.bind('keydown', keyHandler);
        $modalInstance.result.finally(function () {
            $document.unbind('keydown', keyHandler);
        });

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.submit = function () {
            $modalInstance.close('ok');
        };

    })
;