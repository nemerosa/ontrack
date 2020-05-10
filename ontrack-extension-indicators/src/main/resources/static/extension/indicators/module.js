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
                  category
                  name
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
                  category
                  name
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
              labels {
                id
                category
                name
                description
                color
                foregroundColor
              }
            }
        `;

        const queryVariables = {
            id: portfolioId
        };

        const loadPortfolio = () => {
            $scope.loadingPortfolio = true;
            otGraphqlService.pageGraphQLCall(query, queryVariables).then((data) => {
                $scope.labels = data.labels;
                $scope.portfolio = data.indicatorPortfolios[0];
                let currentLabel;
                if ($scope.portfolio.label) {
                    currentLabel = $scope.labels.find((l) => l.id === $scope.portfolio.label.id);
                } else {
                    currentLabel = undefined;
                }
                $scope.portfolioForm = {
                    name: $scope.portfolio.name,
                    nameEdited: false,
                    label: currentLabel
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

        $scope.validatePortfolioNameEdition = () => {
            ot.pageCall($http.put($scope.portfolio.links._update, {
                name: $scope.portfolioForm.name
            })).then(() => {
                $scope.portfolio.name = $scope.portfolioForm.name;
            }).finally(() => {
                $scope.portfolioForm.nameEdited = false;
            });
        };

        $scope.cancelPortfolioNameEdition = () => {
            $scope.portfolioForm.nameEdited = false;
            $scope.portfolioForm.name = $scope.portfolio.name;
        };

        $scope.selectLabel = (label) => {
            $scope.portfolioForm.label = label;
            ot.pageCall($http.put($scope.portfolio.links._update, {
                label: $scope.portfolioForm.label.id
            })).then(() => {
                $scope.portfolio.label = $scope.portfolioForm.label;
            });
        };

        $scope.selectType = () => {
            let selectedTypes = [];
            $scope.categories.forEach((category) => {
                category.types.forEach((type) => {
                    if (type.selected) {
                        selectedTypes.push(type.id);
                    }
                });
            });
            ot.pageCall($http.put($scope.portfolio.links._update, {
                types: selectedTypes
            }));
        };

    })
    .config(function ($stateProvider) {
        $stateProvider.state('portfolio-view', {
            url: '/extension/indicators/portfolios/{portfolioId}',
            templateUrl: 'extension/indicators/portfolio-view.tpl.html',
            controller: 'PortfolioViewCtrl'
        });
    })
    .controller('PortfolioViewCtrl', function ($stateParams, $scope, $http, ot, otGraphqlService) {
        const portfolioId = $stateParams.portfolioId;
        $scope.loadingPortfolio = true;

        const view = ot.view();
        view.title = "Portfolio";
        view.breadcrumbs = ot.homeBreadcrumbs();
        view.commands = [
            ot.viewCloseCommand('/extension/indicators/portfolios')
        ];

        const query = `
            query LoadPortfolio($id: String!) {
              indicatorPortfolios(id: $id) {
                id
                name
                links {
                  _update
                }
                types {
                  id
                  shortName
                  name
                  link
                  category {
                    id
                    name
                  }
                  valueType {
                    name
                    feature {
                      id
                    }
                  }
                }
                projects {
                  id
                  name
                  links {
                    _page
                  }
                  projectIndicators {
                    categories {
                      indicators {
                        type {
                          id
                        }
                        value
                        comment
                        status
                      }
                    }
                  }
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
                view.title = `Portfolio: ${$scope.portfolio.name}`;

                // Grouping portfolio types per categories
                let categories = [];
                let categoriesIndex = {};
                $scope.portfolio.types.forEach((type) => {
                    let category = type.category;
                    let categoryRecord = categoriesIndex[category.id];
                    if (!categoryRecord) {
                        categoryRecord = category;
                        categoryRecord.types = [];
                        categories.push(categoryRecord);
                        categoriesIndex[category.id] = categoryRecord;
                    }
                    type.category = undefined;
                    categoryRecord.types.push(type);
                });
                $scope.categories = categories;

                // Flattens the list of types per category
                let types = [];
                categories.forEach((category) => {
                    category.types.forEach((type) => {
                        types.push(type);
                    });
                });
                $scope.types = types;

                // Indexation of types in projects

                $scope.portfolio.projects.forEach((project) => {
                    project.types = {};
                    types.forEach((type) => {
                        project.projectIndicators.categories.forEach((projectCategory) => {
                            projectCategory.indicators.forEach((projectIndicator) => {
                                if (projectIndicator.type.id === type.id) {
                                    project.types[type.id] = projectIndicator;
                                }
                            });
                        });
                    });
                });

            }).finally(() => {
                $scope.loadingPortfolio = false;
            });
        };

        loadPortfolio();

        /**
         * Gets the project indicator for the given type
         */
        $scope.indicator = (project, type) => {
            return project.types[type.id];
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
                type: '=',
                shortName: '@'
            }
        };
    })

;