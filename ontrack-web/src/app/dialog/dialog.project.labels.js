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
            return labelFilterUnselectedAutoFn(label) && (labelFilterTextFn(label.category) || labelFilterTextFn(label.name));
        };

        function labelFilterUnselectedAutoFn(label) {
            return label.computedBy == null || label.selected;
        }

        function labelFilterTextFn(text) {
            return !$scope.filter.text || (text && text.toLowerCase().indexOf($scope.filter.text.toLowerCase()) >= 0);
        }

        // Watching for filter text change to decide if we allow the creation of a label
        $scope.labelCreation = false;
        if ($scope.config.project.links._labelFromToken && $scope.config.project.links._labelsCreate) {
            $scope.$watch("filter.text", function () {
                // If there is no match in the labels
                $scope.labelCreation = $scope.filter.text && !$scope.data.some($scope.labelFilterFn);
            });
            // Creation of the label
            $scope.createLabelFromToken = () => {
                if ($scope.filter.text) {
                    // Gets the creation form
                    ot.pageCall($http.post($scope.config.project.links._labelFromToken, {token: $scope.filter.text}))
                        .then((form) => {
                            let formConfig = {
                                form: form,
                                title: "New label",
                                submit: (data) => {
                                    return ot.call($http.post($scope.config.project.links._labelsCreate, data));
                                }
                            };
                            return otFormService.display(formConfig);
                        })
                        .then((label) => {
                            let selectedLabel = label;
                            selectedLabel.selected = true;
                            $scope.data.unshift(selectedLabel);
                            $scope.filter.text = "";
                            $scope.labelCreation = false;
                        });
                }
            };
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