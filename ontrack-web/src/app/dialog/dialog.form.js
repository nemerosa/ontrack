angular.module('ot.dialog.form', [])
    .controller('otDialogForm', function ($scope, $modalInstance, config) {
        // General configuration
        $scope.config = config;
        // Form data
        $scope.data = {
            dates: {},
            times: {}
        };
        angular.forEach(config.form.fields, function (field) {
            $scope.data[field.name] = field.value;
            if (field.regex) {
                field.pattern = new RegExp(field.regex);
            }
            // Date-time handling
            if (field.type == 'dateTime') {
                if (field.value) {
                    var dateTime = new Date(field.value);
                    $scope.data.dates[field.name] = dateTime;
                    $scope.data.times[field.name] = dateTime;
                }
            }
        });
        // Cancelling the dialog
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
        // Submitting the dialog
        $scope.submit = function (isValid) {
            if (isValid) {
                // Processing before submit
                angular.forEach(config.form.fields, function (field) {
                    // Date-time handling
                    if (field.type == 'dateTime') {
                        var date = $scope.data.dates[field.name];
                        var time = $scope.data.times[field.name];
                        var dateTime = date;
                        dateTime.setHours(time.getHours());
                        dateTime.setMinutes(time.getMinutes());
                        dateTime.setSeconds(0);
                        dateTime.setMilliseconds(0);
                        $scope.data[field.name] = dateTime;
                    }
                });
                // Cleaning of pseudo fields
                delete $scope.data.dates;
                delete $scope.data.times;
                // Submit
                config.formConfig.submit($scope.data).then(
                    function success() {
                        $modalInstance.close('ok');
                    },
                    function error(message) {
                        $scope.message = message;
                    });
            }
        };
    })
;