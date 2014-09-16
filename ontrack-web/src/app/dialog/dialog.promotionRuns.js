angular.module('ot.dialog.promotionRuns', [
    'ot.service.core'
])
    .controller('otDialogPromotionRuns', function ($scope, $modalInstance, $http, config, ot) {
        // General configuration
        $scope.config = config;
        // Loading all the promotion runs
        ot.call($http.get(config.uri)).then(function (promotionRuns) {
            $scope.promotionRuns = promotionRuns.resources;
        });
        // Cancelling the dialog
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    })
;