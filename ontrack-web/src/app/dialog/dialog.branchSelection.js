angular.module('ot.dialog.branchSelection', [
    'ot.service.core',
    'ot.service.structure'
])
    .controller('otDialogBranchSelection', function ($scope, $modalInstance, $http, ot, otStructureService) {
        // Selection object
        $scope.data = {
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
                $modalInstance.close('ok');
            }
        };
    })
;