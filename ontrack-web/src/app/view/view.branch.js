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

        // Loading indicators
        $scope.loadingBranch = true;

        // Query: loading the branch
        const gqlBranch = `
            query LoadBranch(
                $branchId: Int!,
            ) {
                branches(id: $branchId) {
                    id
                    name
                    description
                    annotatedDescription
                    project {
                        id
                        name
                    }
                    decorations {
                      ...decorationContent
                    }
                }
            }
            
            fragment decorationContent on Decoration {
                decorationType
                error
                data
                feature {
                    id
                }
            }
        `;

        // Loading the branch
        const loadBranch = () => {
            $scope.loadingBranch = true;
            otGraphqlService.pageGraphQLCall(gqlBranch, {branchId})
                .then(data => {
                    $scope.branch = data.branches[0];
                })
                .finally(() => {
                    $scope.loadingBranch = false;
                });
        };

        // Starts by loading the branch
        loadBranch();
    })
;