angular.module('ot.dialog.branch.copy', [
    'ot.service.core',
    'ot.service.structure'
])
    .controller('otDialogBranchCopy', function ($scope, $modalInstance, $http, config, ot, otStructureService) {
        // Inject the configuration into the scope
        $scope.config = config;
        // Selection object
        $scope.data = {
            propertyReplacement: config.targetBranch.name,
            promotionLevelReplacement: config.targetBranch.name,
            validationStampReplacement: config.targetBranch.name
        };
        // Loading the projects
        otStructureService.getProjects().then(function (projects) {
            $scope.projects = projects.resources;
        });
        // Loading the branches on project selection
        $scope.$watch('data.project', function (project) {
            if (project) {
                ot.call($http.get(project._branches)).then(function (branches) {
                    $scope.branches = branches.resources;
                    if ($scope.branches.length == 1) {
                        $scope.data.branch = $scope.branches[0];
                    }
                });
            }
        });
        // Cancelling the dialog
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
        // Submitting the dialog
        $scope.submit = function (isValid) {
            if (isValid) {
                $modalInstance.close($scope.data.branch);
            }
        };
    })
;