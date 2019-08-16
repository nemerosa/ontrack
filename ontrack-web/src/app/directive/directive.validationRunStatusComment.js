angular.module('ot.directive.validationRunStatusComment', [
    'ot.service.core',
    'ot.service.form'
])
    .directive('otValidationRunStatusComment', (ot, otFormService) => ({
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
                        // TODO Change the description
                    });
                }
            };
        }
    }))
;