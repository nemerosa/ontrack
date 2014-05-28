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

    })
;