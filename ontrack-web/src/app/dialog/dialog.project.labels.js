angular.module('ot.dialog.project.labels', [
    'ot.service.core',
    'ot.service.form'
])
    .controller('otDialogProjectLabels', function ($scope, $modalInstance, $http, config, ot, otFormService) {
        // Inject the configuration into the scope
        $scope.config = config;
        // Preparing the data
        $scope.data = config.labels;
        // Flags each label according to its presence in the project
        angular.forEach($scope.data, (label) => {
            const projectIndex = $scope.config.project.labels.findIndex((projectLabel) => {
                return projectLabel.id === label.id;
            });
            label.selected = (projectIndex >= 0);
        });
        // Label filter
        $scope.filter = {text: ""};
        $scope.labelFilterFn = function (label) {
            return labelFilterInternalFn(label.category) || labelFilterInternalFn(label.name);
        };
        function labelFilterInternalFn(text) {
            return !$scope.filter.text || text.toLowerCase().indexOf($scope.filter.text.toLowerCase()) >= 0;
        }
        // Toggling the label selection
        $scope.toggleLabel = function (label) {
            if (label.computedBy == null) {
                label.selected = !label.selected;
            }
        };
        // Unselecting all (non automated) lavels
        $scope.selectNone = () => {
            angular.forEach($scope.data, (label) => {
                if (label.computedBy == null) {
                    label.selected = false;
                }
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