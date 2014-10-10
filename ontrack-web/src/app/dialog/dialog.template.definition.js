angular.module('ot.dialog.template.definition', [
    'ot.service.core',
    'ot.service.form'
])
    .controller('otDialogTemplateDefinition', function ($scope, $modalInstance, templateDefinition, config, otFormService) {
        // Inject the configuration into the scope
        $scope.templateDefinition = templateDefinition;
        $scope.config = config;
        // Preparing the data
        $scope.data = otFormService.prepareForDisplay(templateDefinition);

        // Adding a new parameter
        $scope.addParameter = function () {
            if (!$scope.data.parameters) {
                $scope.data.parameters = [];
            }
            $scope.data.parameters.push({
                name: '',
                description: ''
            });
        };

        // Cancelling the dialog
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
        // Submitting the dialog
        $scope.submit = function (isValid) {
            if (isValid) {
                otFormService.submitDialog(
                    config.submit,
                    $scope.data,
                    $modalInstance,
                    $scope
                );
            }
        };
    })
;