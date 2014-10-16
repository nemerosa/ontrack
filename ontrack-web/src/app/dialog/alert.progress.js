angular.module('ot.alert.progress', [])
/**
 * @param config.title Title of the dialog
 * @param config.promptMessage Optional. If set, the task won't start before the user has confirmed.
 * @param config.waitingMessage Message to display during the execution of the task
 * @param config.endMessage Message to display when task has been completed successfully
 * @param config.task Function that must return a promise for the execution of the task
 */
    .controller('otAlertProgress', function ($scope, $modalInstance, config) {

        $scope.config = config;

        // Initial state
        if (config.promptMessage) {
            $scope.state = 'ask-prompt';
        } else {
            $scope.state = 'start';
        }

        $scope.promptOk = function () {
            $scope.state = 'start';
        };

        $scope.doneOk = function () {
            $modalInstance.close($scope.result);
        };

        function start () {
            $scope.state = 'started';
            config.task().then(
                function success(data) {
                    $scope.state = 'done';
                    $scope.result = data;
                },
                function error(e) {
                    $scope.state = 'error';
                    $scope.message = e;
                }
            );
        }

        $scope.$watch('state', function (state) {
            if (state == 'start') {
                start();
            }
        });

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

    })
;