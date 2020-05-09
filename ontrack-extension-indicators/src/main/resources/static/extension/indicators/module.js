angular.module('ontrack.extension.indicators', [
    'ot.service.core',
    'ot.service.form',
    'ot.service.graphql'
])
    .config(function ($stateProvider) {
        $stateProvider.state('project-indicators', {
            url: '/extension/indicators/project-indicators/{project}',
            templateUrl: 'extension/indicators/project-indicators.tpl.html',
            controller: 'ProjectIndicatorsCtrl'
        });
    })
    .controller('ProjectIndicatorsCtrl', function ($stateParams, $scope, $http, ot, otGraphqlService, otFormService) {

        const projectId = $stateParams.project;
        $scope.loadingIndicators = true;

        const view = ot.view();
        view.title = "";

        const query = `
            query Indicators($project: Int!) {
              projects(id: $project) {
                id
                name
                projectIndicators {
                  categories {
                    category {
                      name
                    }
                    indicators {
                      links {
                        _update
                      }
                      type {
                        name
                        link
                        valueType {
                          name
                          feature {
                            id
                          }
                        }
                      }
                      value
                      status
                      comment
                      signature {
                        user
                        time
                      }
                    }
                  }
                }
              }
            }
        `;

        const queryVars = {
            project: projectId
        };

        let viewInitialized = false;

        const loadIndicators = () => {
            $scope.loadingIndicators = true;
            otGraphqlService.pageGraphQLCall(query, queryVars).then((data) => {

                $scope.project = data.projects[0];
                $scope.projectIndicators = $scope.project.projectIndicators;

                if (!viewInitialized) {
                    // Title
                    view.title = `Project indicators for ${$scope.project.name}`;
                    // View configuration
                    view.breadcrumbs = ot.projectBreadcrumbs($scope.project);
                    // Commands
                    view.commands = [
                        ot.viewCloseCommand('/project/' + $scope.project.id)
                    ];
                    // OK
                    viewInitialized = true;
                }
            }).finally(() => {
                $scope.loadingIndicators = false;
            });
        };

        loadIndicators();

        $scope.editIndicator = (indicator) => {
            otFormService.update(indicator.links._update, "Edit indicator value").then(loadIndicators);
        };

    })
    .config(function ($stateProvider) {
        $stateProvider.state('portfolios', {
            url: '/extension/indicators/portfolios',
            templateUrl: 'extension/indicators/portfolios.tpl.html',
            controller: 'PortfoliosCtrl'
        });
    })
    .controller('PortfoliosCtrl', function ($stateParams, $scope, $http, ot, otGraphqlService, otFormService) {

        $scope.loadingPortfolios = true;

        $scope.createPortfolio = () => {
            otFormService.create("/extension/indicators/portfolios/create", "New portfolio").then(loadPortfolios);
        };

        const view = ot.view();
        view.title = "Indicator portfolios";
        view.breadcrumbs = ot.homeBreadcrumbs();
        view.commands = [
            {
                id: 'portfolio-create',
                name: "Create a portfolio",
                cls: 'ot-command-new',
                action: $scope.createPortfolio
            },
            ot.viewCloseCommand('/home')
        ];

        const query = `
            {
              indicatorPortfolios {
                id
                name
                label {
                  id
                  display
                  color
                  description
                }
                types {
                  id
                  shortName
                  name
                  link
                }
                links {
                  _update
                }
              }
            }
        `;

        const loadPortfolios = () => {
            $scope.loadingPortfolios = true;
            otGraphqlService.pageGraphQLCall(query).then((data) => {
                $scope.portfolios = data.indicatorPortfolios;
            }).finally(() => {
                $scope.loadingPortfolios = false;
            });
        };

        loadPortfolios();
    })
    .config(function ($stateProvider) {
        $stateProvider.state('portfolio-edit', {
            url: '/extension/indicators/portfolios/{portfolioId}/edit',
            templateUrl: 'extension/indicators/portfolio-edit.tpl.html',
            controller: 'PortfolioEditionCtrl'
        });
    })
    .controller('PortfolioEditionCtrl', function ($stateParams, $scope, $http, ot, otGraphqlService, otFormService) {
        const portfolioId = $stateParams.portfolioId;
        $scope.loadingPortfolio = true;

        const view = ot.view();
        view.title = "Portfolio edition";
        view.breadcrumbs = ot.homeBreadcrumbs();
        view.commands = [
            ot.viewCloseCommand('/extension/indicators/portfolios')
        ];

        const query = `
            query LoadPortfolio($id: String!) {
              indicatorPortfolios(id: $id) {
                id
                name
                label {
                  id
                  display
                  color
                  description
                }
                types {
                  id
                  shortName
                  name
                  link
                }
                links {
                  _update
                }
              }
              indicatorCategories {
                name
                types {
                  id
                  shortName
                  name
                  link
                }
              }
            }
        `;

        const queryVariables = {
            id: portfolioId
        };

        const loadPortfolio = () => {
            $scope.loadingPortfolio = true;
            otGraphqlService.pageGraphQLCall(query, queryVariables).then((data) => {
                $scope.portfolio = data.indicatorPortfolios[0];
                $scope.portfolioForm = {
                    name: $scope.portfolio.name,
                    nameEdited: false
                };
                $scope.categories = data.indicatorCategories;
                $scope.categories.forEach((category) => {
                    category.types.forEach((type) => {
                        type.selected = $scope.portfolio.types.some((i) =>
                            i.id === type.id
                        );
                    });
                });
            }).finally(() => {
                $scope.loadingPortfolio = false;
            });
        };

        loadPortfolio();

        $scope.startPortfolioNameEdition = () => {
            $scope.portfolioForm.nameEdited = true;
        };

        $scope.cancelPortfolioNameEdition = () => {
            $scope.portfolioForm.nameEdited = false;
            $scope.portfolioForm.name = $scope.portfolio.name;
        };
    })
    .directive('otExtensionIndicatorsStatus', function () {
        return {
            restrict: 'E',
            templateUrl: 'extension/indicators/directive.indicators-status.tpl.html',
            scope: {
                status: '='
            }
        };
    })
    .directive('otExtensionIndicatorsTypeName', function () {
        return {
            restrict: 'E',
            templateUrl: 'extension/indicators/directive.indicators-type-name.tpl.html',
            scope: {
                type: '='
            }
        };
    })

;