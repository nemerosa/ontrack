angular.module('ot.dialog.promotionRuns', [
    'ot.service.core'
])
    .controller('otDialogPromotionRuns', function ($scope, $modalInstance, $http, config, ot) {
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
            ot.call($http.delete(promotionRun._delete)).then(loadRuns);
        };
    })
;