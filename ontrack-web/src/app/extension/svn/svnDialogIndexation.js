angular.module('ot.extension.svn.dialog.indexation', [

])
    .controller('svnDialogIndexation', function ($scope, $modalInstance, config) {
        // General configuration
        $scope.config = config;
        // Cancelling the dialog
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        // Full re-indexation
        $scope.fullReindexation = function () {

        };
    })
;