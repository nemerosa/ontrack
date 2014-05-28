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

        // Loads the validation stamp for the run
        function loadValidationStamp() {
            ot.call($http.get($scope.validationRun.validationStampLink.href)).then(function (validationStampResource) {
                $scope.validationStamp = validationStampResource;
            });
        }

        // Loads the validation run
        function loadValidationRun() {
            ot.call($http.get('structure/validationRuns/' + validationRunId)).then(function (validationRunResource) {
                $scope.validationRun = validationRunResource;
                // View configuration
                view.breadcrumbs = ot.buildBreadcrumbs(validationRunResource.build);
                // Loads the validation stamp
                loadValidationStamp();
                // Commands
                view.commands = [
                    ot.viewCloseCommand('/build/' + validationRunResource.build.id)
                ];
            });
        }

        // Initialisation
        loadValidationRun();

        // Changing the validation run status
        $scope.validationRunStatusChange = function () {
            otStructureService.create(
                $scope.validationRun.validationRunStatusChange.href,
                'Status').then(loadValidationRun);
        };

    })
;