angular.module('ot.dialog.promotionRuns', [
    'ot.service.structure'
])
    .controller('otDialogPromotionRuns', function ($scope, $modalInstance, config, otStructureService) {
        // General configuration
        $scope.config = config;
        // Cancelling the dialog
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    })
;