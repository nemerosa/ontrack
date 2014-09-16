angular.module('ot.dialog.promotionRuns', [
    'ot.service.core'
])
    .controller('otDialogPromotionRuns', function ($scope, $modalInstance, $http, config, ot, otAlertService) {
        // General configuration
        $scope.config = config;
        // Loading all the promotion runs
        function loadRuns() {
            ot.call($http.get(config.uri)).then(function (promotionRuns) {
                $scope.promotionRuns = promotionRuns.resources;
            });
        }

        loadRuns();
        // Cancelling the dialog
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        // Deleting a promotion run
        $scope.deletePromotionRun = function (promotionRun) {
            otAlertService.confirm({
                title: "Promotion deletion",
                message: "Do you really want to delete this promotion?"
            }).then(function () {
                return ot.call($http.delete(promotionRun._delete))
            }).then(loadRuns);
        };
    })
;