angular.module('ot.dialog.validationStampRunView', [
    'ot.service.structure',
    'ot.service.graphql'
])
    .controller('otDialogValidationStampRunView', function ($scope, $modalInstance, config, otStructureService, otGraphqlService) {
        // General configuration
        $scope.config = config;
        // Loading the validation runs
        $scope.loadingValidationRuns = true;
        otGraphqlService.pageGraphQLCall("query ValidationRuns($buildId: Int!, $validationStamp: String!) {\n" +
            "  builds(id: $buildId) {\n" +
            "    id\n" +
            "    name\n" +
            "    validations(validationStamp: $validationStamp) {\n" +
            "      validationStamp {\n" +
            "        id\n" +
            "        name\n" +
            "        image\n" +
            "        _image\n" +
            "      }\n" +
            "      validationRuns {\n" +
            "        id\n" +
            "        runOrder\n" +
            "        description\n" +
            "        creation {\n" +
            "          user\n" +
            "          time\n" +
            "        }\n" +
            "        validationRunStatuses {\n" +
            "          description\n" +
            "          statusID {\n" +
            "            id\n" +
            "            name\n" +
            "          }\n" +
            "        }\n" +
            "        links {\n" +
            "          _validationRunStatusChange\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}\n", {
            buildId: $scope.config.build.id,
            validationStamp: $scope.config.validationStamp.name
        }).then(function (data) {
            $scope.build = data.builds[0];
            $scope.validation = data.builds[0].validations[0];
        }).finally(function () {
            $scope.loadingValidationRuns = false;
        });
        // Cancelling the dialog
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
        // Changing the status of a validation run
        $scope.changeStatus = function (validationRun) {
            // Closes this dialog first
            $scope.cancel();
            // Calling the service
            otStructureService.create(
                validationRun._validationRunStatusChange,
                'Status'
            ).then(function () {
                if (config.callbackOnStatusChange) {
                    config.callbackOnStatusChange();
                } else {
                    // Goes to the validation run page
                    location.href = '#/validationRun/' + validationRun.id;
                }
            });
        };
    })
;