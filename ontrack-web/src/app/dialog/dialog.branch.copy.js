angular.module('ot.dialog.branch.copy', [
    'ot.service.core',
    'ot.service.form',
    'ot.service.structure',
    'ot.directive.branch.replacements'
])
    .controller('otDialogBranchCopy', function ($scope, $modalInstance, $http, config, ot, otStructureService, otFormService) {
        // Inject the configuration into the scope
        $scope.config = config;
        // Selection object
        $scope.data = {
            propertyReplacements: [{
                replacement: config.targetBranch.name
            }],
            promotionLevelReplacements: [{
                replacement: config.targetBranch.name
            }],
            validationStampReplacements: [{
                replacement: config.targetBranch.name
            }]
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