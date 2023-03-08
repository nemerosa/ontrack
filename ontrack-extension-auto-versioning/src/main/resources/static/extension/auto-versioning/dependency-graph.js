angular.module('ontrack.extension.auto-versioning.dependency-graph', [
    'ot.service.core',
    'ot.service.graphql'
])
    .config(function ($stateProvider) {
        $stateProvider.state('auto-versioning-dependency-graph', {
            url: '/extension/auto-versioning/dependency-graph/build/{buildId}',
            templateUrl: 'extension/auto-versioning/dependency-graph.tpl.html',
            controller: 'AutoVersioningDependencyGraphCtrl'
        });
    })

    .controller('AutoVersioningDependencyGraphCtrl', function ($stateParams, $scope,
                                                               ot, otGraphqlService) {
        $scope.rootBuildId = $stateParams.buildId;

        const view = ot.view();

        // GraphQL fragments
        const gqlBuildMinInfo = `
            fragment BuildMinInfo on Build {
                id
                name
                releaseProperty {
                    value
                }
                links {
                    _page
                }
                promotionRuns(lastPerLevel: true) {
                    promotionLevel {
                        name
                        image
                        _image
                    }
                }
            }
        `;

        const gqlBuildInfo = `
            fragment BuildInfo on Build {
                branch {
                    id
                    name
                    project {
                        id
                        name
                        links {
                            _page
                        }
                    }
                    links {
                        _page
                    }
                }
                ...BuildMinInfo
            }
            
            ${gqlBuildMinInfo}
        `;

        // Loading the first node
        const loadRootNode = () => {
            otGraphqlService.pageGraphQLCall(`
                query RootNode($rootBuildId: Int!) {
                    build(id: $rootBuildId) {
                        ...BuildInfo
                    }
                }
                ${gqlBuildInfo}
            `, {rootBuildId: $scope.rootBuildId}
            ).then(data => {
                $scope.rootBuild = data.build;
                view.breadcrumbs = ot.buildBreadcrumbs($scope.rootBuild);
                view.commands = [
                    ot.viewCloseCommand(`/build/${$scope.rootBuild.id}`)
                ];
            });
        };

        loadRootNode();
    })
;