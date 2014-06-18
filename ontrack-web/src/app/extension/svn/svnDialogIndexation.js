angular.module('ot.extension.svn.dialog.indexation', [
    'ot.service.core',
    'ot.service.form'
])
    .controller('svnDialogIndexation', function ($scope, $modalInstance, $http, config, ot, otAlertService, otFormService) {
        // General configuration
        $scope.config = config;
        // Range form
        $scope.range = {
            from: 1,
            to: 1
        };

        // Getting the last revision info
        ot.call($http.get(config.configuration._indexation)).then(function (lastRevisionInfo) {
            $scope.lastRevisionInfo = lastRevisionInfo;
        });

        // Getting the range
        ot.call($http.get(config.configuration._indexationRange)).then(function (form) {
            $scope.range.from = otFormService.getFieldValue(form, 'from');
            $scope.range.to = otFormService.getFieldValue(form, 'to');
        });

        // Cancelling the dialog
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        // Indexation from latest
        $scope.indexFromLatest = function () {
            ot.call($http.post($scope.config.configuration._indexationFromLatest)).then(
                function success() {
                    // Closes the dialog
                    $scope.cancel();
                },
                function error(message) {
                    $scope.message = message;
                }
            );
        };

        // Range indexation
        $scope.indexRange = function () {
            var to = $scope.range.to;
            var from = $scope.range.from;
            // FIXME Calling the range indexation
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