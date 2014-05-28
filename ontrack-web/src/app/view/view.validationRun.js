angular.module('ot.view.validationRun', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure'
])
    .config(function ($stateProvider) {
        $stateProvider.state('validationRun', {
            url: '/validationRun/{validationRunId}',
            templateUrl: 'app/view/view.validationRun.tpl.html',
            controller: 'ValidationRunCtrl'
        });
    })
    .controller('ValidationRunCtrl', function ($scope, $stateParams, $http, ot, otStructureService) {
        var view = ot.view();
        // Validation run's id
        var validationRunId = $stateParams.validationRunId;

        // Loads the validation run
        function loadValidationRun() {
            ot.call($http.get('structure/validationRuns/' + validationRunId)).then(function (validationRunResource) {
                $scope.validationRun = validationRunResource;
                // View configuration
                view.title = validationRunResource.validationStamp.name + " run";
                view.description = validationRunResource.lastStatus.description;
                view.breadcrumbs = ot.buildBreadcrumbs(validationRunResource.build);
            });
        }

        // Initialisation
        loadValidationRun();

    })
;