angular.module('ot.dialog.form', [])
    .controller('otDialogForm', function ($scope, $modalInstance, config) {
        // General configuration
        $scope.config = config;
        // Form data
        $scope.data = {};
        angular.forEach(config.form.fields, function (field) {
            $scope.data[field.name] = field.value;
        });
        // Cancelling the dialog
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    })
;