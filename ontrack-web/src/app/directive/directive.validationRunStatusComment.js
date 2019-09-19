angular.module('ot.directive.validationRunStatusComment', [
    'ot.service.core',
    'ot.service.form',
    'ot.service.graphql'
])
    .directive('otValidationRunStatusComment', (ot, otFormService, otGraphqlService) => ({
        restrict: 'E',
        templateUrl: 'app/directive/directive.validationRunStatusComment.tpl.html',
        scope: {
            status: '='
        },
        controller: $scope => {
            $scope.editValidationRunStatusComment = () => {
                if ($scope.status.links._comment) {
                    otFormService.update(
                        $scope.status.links._comment,
                        "Edit comment"
                    ).then(function (run) {
                        // Reloading the validation run with the information we need
                        otGraphqlService.graphQLCall(`
                            query ValidationRunStatus($id: Int!) {
                                validationRuns(id: $id) {
                                    validationRunStatuses {
                                      id
                                      description
                                      annotatedDescription
                                    }
                                }
                            }
                        `, {id: run.id}).then(function (data) {
                            let run = data.validationRuns[0];
                            let updatedStatus = run.validationRunStatuses.find(it => it.id === $scope.status.id);
                            $scope.status.description = updatedStatus.description;
                            $scope.status.annotatedDescription = updatedStatus.annotatedDescription;
                        });
                    });
                }
            };
        }
    }))
;