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
        let viewInitialized = false;
        // Branch's id
        const branchId = $stateParams.branchId;

        // Loading indicators
        $scope.loadingBranch = true;
        $scope.loadingBuilds = false;

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

        // Query: loading the builds
        const gqlBuilds = `
            query LoadBuilds(
                $branchId: Int!,
            ) {
                branches(id: $branchId) {
                    buildsPaginated {
                        pageInfo {
                            totalSize
                            nextPage {
                                offset
                                size
                            }
                        }
                        pageItems {
                            id
                            name
                            creation {
                              time
                            }
                            decorations {
                              ...decorationContent
                            }
                        }
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
                    if (!viewInitialized) {
                        view.breadcrumbs = ot.projectBreadcrumbs($scope.branch.project);
                        view.commands = [
                            ot.viewCloseCommand('/project/' + $scope.branch.project.id),
                        ];
                        viewInitialized = true;
                    }
                })
                .finally(() => {
                    $scope.loadingBranch = false;
                });
        };

        // Loading the list of builds
        const loadBuilds = () => {
            $scope.loadingBuilds = true;
            const gqlVariables = {
                branchId,
            };
            otGraphqlService.pageGraphQLCall(gqlBuilds, gqlVariables)
                .then(data => {
                    const dataBranch = data.branches[0];
                    const dataBuilds = dataBranch.buildsPaginated;
                    $scope.builds = dataBuilds.pageItems;
                    $scope.buildsPageInfo = dataBuilds.pageInfo;
                })
                .finally(() => {
                    $scope.loadingBuilds = false;
                });
        };

        // Starts by loading the branch
        loadBranch();

        // Loading the builds AFTER the branch is loaded
        $scope.$watch('branch', (value) => {
            if (value) {
                loadBuilds();
            }
        });
    })
;