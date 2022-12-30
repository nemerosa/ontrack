angular.module('ot.view.branch', [
    'ui.router',
    'ot.service.core',
    'ot.service.task',
    'ot.service.form',
    'ot.service.structure',
    'ot.service.buildfilter',
    'ot.service.copy',
    'ot.dialog.validationStampRunView',
    'ot.dialog.validationStampRunGroup',
    'ot.dialog.promotionRuns',
    'ot.service.graphql',
    'ot.service.user'
])
    .config(function ($stateProvider) {
        $stateProvider.state('branch', {
            url: '/branch/{branchId}',
            templateUrl: 'app/view/view.branch.tpl.html',
            controller: 'BranchCtrl'
        });
    })
    .controller('BranchCtrl', function ($state, $scope, $stateParams, $http, $modal, $location, $rootScope,
                                        ot, otFormService, otStructureService, otAlertService, otTaskService, otNotificationService, otCopyService,
                                        otBuildFilterService, otGraphqlService, otUserService) {
        const view = ot.view();
        // Branch's id
        const branchId = $stateParams.branchId;
    })
;