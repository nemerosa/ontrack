angular.module('ot.dialog.applicationInfo', [
    'ot.service.info'
])
    .controller('otDialogApplicationInfo', function ($scope, $modalInstance, $http, otInfoService) {
        // Loading the application info
        function loadApplicationMessages() {
            $scope.loadingMessages = true;
            otInfoService.loadApplicationInfo().then(messages => {
                $scope.messages = messages.resources;
            }).finally(() => {
                $scope.loadingMessages = false;
            });
        }

        loadApplicationMessages();
        // Refreshing
        $scope.loadApplicationMessages = loadApplicationMessages;
        // Cancelling the dialog
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    })
;