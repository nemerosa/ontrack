angular.module('ot.extension.svn.dialog.indexation', [
    'ot.service.core'
])
    .controller('svnDialogIndexation', function ($scope, $modalInstance, $http, config, ot, otAlertService) {
        // General configuration
        $scope.config = config;
        // Cancelling the dialog
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        // Full re-indexation
        $scope.fullReindexation = function () {
            otAlertService.confirm({
                title: "Full re-indexation",
                message: "Are you sure to fully re-index the repository? All associated cached data (revisions, issues...) will be lost."
            }).then(function () {
                // Launches full reindexation
                ot.call($http.post($scope.config.configuration._indexationFull)).then(
                    function success() {
                        // Closes the dialog
                        $scope.cancel();
                    },
                    function error(message) {
                        $scope.message = message;
                    }
                );
            });
        };
    })
;