angular.module('ontrack.extension.indicators', [
    'ot.service.core',
    'ot.service.form',
    'ot.service.graphql'
])
    .config(function ($stateProvider) {
        $stateProvider.state('indicator-categories', {
            url: '/extension/indicators/categories',
            templateUrl: 'extension/indicators/categories.tpl.html',
            controller: 'IndicatorCategoriesCtrl'
        });
    })
    .controller('IndicatorCategoriesCtrl', function ($scope, $http, ot, otGraphqlService, otFormService, otAlertService) {
        $scope.loadingCategories = false;

        const view = ot.view();
        view.title = "Indicator categories";

        const query = `
            {
              indicatorCategories {
                categories {
                  id
                  name
                  source {
                    provider {
                        id
                        name
                    }
                    name
                  }
                  links {
                    _update
                    _delete
                  }
                  types {
                    id
                  }
                }
                links {
                  _create
                }
              }
            }
        `;

        let viewInitialized = false;

        $scope.createCategory = () => {
            otFormService.create($scope.indicatorCategories.links._create, "New indicator category").then(loadCategories);
        };

        const loadCategories = () => {
            $scope.loadingCategories = true;
            otGraphqlService.pageGraphQLCall(query).then((data) => {
                $scope.indicatorCategories = data.indicatorCategories;
                $scope.categories = data.indicatorCategories.categories;

                if (!viewInitialized) {
                    view.commands = [
                        {
                            condition: () => data.indicatorCategories.links._create,
                            id: 'indicator-category-create',
                            name: "Create a category",
                            cls: 'ot-command-new',
                            action: $scope.createCategory
                        },
                        ot.viewCloseCommand('/home')
                    ];
                    viewInitialized = true;
                }

            }).finally(() => {
                $scope.loadingCategories = false;
            });
        };

        loadCategories();

        $scope.editCategory = (category) => {
            otFormService.update(category.links._update, "Edit indicator category").then(loadCategories);
        };

        $scope.deleteCategory = (category) => {
            otAlertService.confirm({
                title: "Delete category",
                message: `Do you want to delete the ${category.name} category?`
            }).then(() => {
                return ot.pageCall($http.delete(category.links._delete));
            }).then(loadCategories);
        };

    })
    .config(function ($stateProvider) {
        $stateProvider.state('indicator-types', {
            url: '/extension/indicators/types',
            templateUrl: 'extension/indicators/types.tpl.html',
            controller: 'IndicatorTypesCtrl'
        });
    })
    .controller('IndicatorTypesCtrl', function ($scope, $http, ot, otGraphqlService, otFormService, otAlertService) {
        $scope.loadingTypes = false;

        const view = ot.view();
        view.title = "Indicator types";

        const query = `
            {
              indicatorTypes {
                links {
                  _create
                }
                types {
                  id
                  name
                  link
                  source {
                    provider {
                        id
                        name
                    }
                    name
                  }
                  computed
                  category {
                    id
                    name
                  }
                  valueType {
                    id
                    name
                    feature {
                      id
                    }
                  }
                  valueConfig
                  links {
                    _update
                    _delete
                  }
                }
              }
            }
        `;

        let viewInitialized = false;

        const loadTypes = () => {
            $scope.loadingTypes = true;
            otGraphqlService.pageGraphQLCall(query).then((data) => {
                $scope.indicatorTypes = data.indicatorTypes;
                const types = data.indicatorTypes.types;

                if (!viewInitialized) {
                    view.commands = [
                        {
                            condition: () => data.indicatorTypes.links._create,
                            id: 'indicator-type-create',
                            name: "Create a type",
                            cls: 'ot-command-new',
                            action: createType
                        },
                        ot.viewCloseCommand('/home')
                    ];
                    viewInitialized = true;
                }

                // Indexation per category
                let categories = [];
                let categoriesIndex = {};
                types.forEach((type) => {
                    let category = categoriesIndex[type.category.id];
                    if (!category) {
                        category = type.category;
                        type.category = undefined;
                        category.types = [];
                        categories.push(category);
                        categoriesIndex[category.id] = category;
                    }
                    category.types.push(type);
                });
                $scope.categories = categories;

            }).finally(() => {
                $scope.loadingTypes = false;
            });
        };

        loadTypes();

        const createType = () => {
            otFormService.create($scope.indicatorTypes.links._create, "New indicator type").then(loadTypes);
        };
        $scope.createType = createType;

        $scope.editType = (type) => {
            otFormService.update(type.links._update, "Edit indicator type").then(loadTypes);
        };

        $scope.deleteType = (type) => {
            otAlertService.confirm({
                title: "Delete type",
                message: `Do you want to delete the ${type.name} type?`
            }).then(() => {
                return ot.pageCall($http.delete(type.links._delete));
            }).then(loadTypes);
        };

    })
    .config(function ($stateProvider) {
        $stateProvider.state('project-indicators', {
            url: '/extension/indicators/project-indicators/{project}',
            templateUrl: 'extension/indicators/project-indicators.tpl.html',
            controller: 'ProjectIndicatorsCtrl'
        });
    })
    .controller('ProjectIndicatorsCtrl', function ($stateParams, $scope, $http, ot, otGraphqlService, otFormService, otAlertService) {

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
                        _delete
                      }
                      type {
                        id
                        name
                        link
                        valueType {
                          id
                          feature {
                            id
                          }
                        }
                        computed
                      }
                      value
                      previousValue {
                        value
                        compliance
                        rating
                        signature {
                          user
                          time
                        }
                        durationSecondsSince
                      }
                      trendSincePrevious
                      compliance
                      rating
                      comment
                      annotatedComment
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

        $scope.deleteIndicator = (indicator) => {
            otAlertService.confirm({
                title: "Indicator deletion",
                message: "Do you want to delete this indicator? History will be kept."
            }).then(() => {
                return ot.pageCall($http.delete(indicator.links._delete));
            }).then(loadIndicators);
        };

    })
    .config(function ($stateProvider) {
        $stateProvider.state('portfolios', {
            url: '/extension/indicators/portfolios',
            templateUrl: 'extension/indicators/portfolios.tpl.html',
            controller: 'PortfoliosCtrl'
        });
    })
    .controller('PortfoliosCtrl', function ($stateParams, $scope, $http, ot, otGraphqlService, otFormService, otAlertService, otLabelService) {

        $scope.loadingPortfolios = true;

        $scope.createPortfolio = () => {
            otFormService.create("/extension/indicators/portfolios/create", "New portfolio").then(loadPortfolios);
        };

        const view = ot.view();
        view.title = "Indicator portfolios";
        view.breadcrumbs = ot.homeBreadcrumbs();

        const query = `
            query LoadPortfolioOfPortfolios($trendDuration: Int) {
              indicatorPortfolioOfPortfolios {
                links {
                  _create
                  _globalIndicators
                }
                portfolios {
                  id
                  name
                  label {
                    id
                    category
                    name
                    color
                    description
                  }
                  globalStats(duration: $trendDuration) {
                    category {
                      id
                      name
                    }
                    stats {
                      total
                      count
                      min
                      minCount
                      minRating
                      avg
                      avgRating
                      max
                      maxCount
                      maxRating
                    }
                    previousStats {
                      stats {
                        avg
                        avgRating
                      }
                      avgTrend
                      durationSeconds
                    }
                  }
                  links {
                    _update
                    _delete
                  }
                }
              }
            }
        `;

        const queryVariables = {
            trendDuration: undefined
        };

        $scope.pageModel = {
            trendDuration: undefined
        };

        let viewInitialized = false;

        const loadPortfolios = () => {
            $scope.loadingPortfolios = true;
            otGraphqlService.pageGraphQLCall(query, queryVariables).then((data) => {
                $scope.portfolioOfPortolios = data.indicatorPortfolioOfPortfolios;
                $scope.portfolios = data.indicatorPortfolioOfPortfolios.portfolios;

                if (!viewInitialized) {
                    view.commands = [
                        {
                            condition: () => $scope.portfolioOfPortolios.links._create,
                            id: 'portfolio-create',
                            name: "Create a portfolio",
                            cls: 'ot-command-new',
                            action: $scope.createPortfolio
                        },
                        {
                            condition: () => $scope.portfolioOfPortolios.links._globalIndicators,
                            id: 'portfolio-global-indicators',
                            name: "Global indicators",
                            cls: "ot-command-update",
                            link: "/extension/indicators/portfolios/global-indicators"
                        },
                        ot.viewCloseCommand('/home')
                    ];
                    viewInitialized = true;
                }
            }).finally(() => {
                $scope.loadingPortfolios = false;
            });
        };

        loadPortfolios();

        $scope.portfolioProjects = (portfolio) => {
            location.href = '#/home?label=' + otLabelService.formatLabel(portfolio.label);
        };

        $scope.deletePortfolio = (portfolio) => {
            otAlertService.confirm({
                title: "Portfolio deletion",
                message: `Do you want to delete the "${portfolio.name}" portfolio?`
            }).then(() => {
                return ot.pageCall($http.delete(portfolio.links._delete));
            }).then(loadPortfolios);
        };

        $scope.selectTrend = () => {
            if ($scope.pageModel.trendDuration) {
                queryVariables.trendDuration = Number($scope.pageModel.trendDuration);
            } else {
                queryVariables.trendDuration = undefined;
            }
            loadPortfolios();
        };
    })
    .config(function ($stateProvider) {
        $stateProvider.state('portfolio-global-indicators', {
            url: '/extension/indicators/portfolios/global-indicators',
            templateUrl: 'extension/indicators/portfolio-global-indicators.tpl.html',
            controller: 'PortfolioGlobalIndicatorsCtrl'
        });
    })
    .controller('PortfolioGlobalIndicatorsCtrl', function ($stateParams, $scope, $http, ot, otGraphqlService) {
        $scope.loadingPortfolioGlobalIndicators = true;

        const view = ot.view();
        view.title = "Portfolio global indicators";
        view.description = "Selection of categories to display for all portfolios.";
        view.breadcrumbs = ot.homeBreadcrumbs().push([
            'portfolios', '#/extension/indicators/portfolios'
        ]);
        view.commands = [
            ot.viewCloseCommand(`/extension/indicators/portfolios`)
        ];

        const query = `
            {
              indicatorPortfolioOfPortfolios {
                links {
                  _globalIndicators
                }
                categories {
                  id
                }
              }
              indicatorCategories {
                categories {
                  id
                  name
                  types {
                    id
                    name
                    link
                  }
                }
              }
            }
        `;

        const loadGlobalIndicators = () => {
            $scope.loadingPortfolioGlobalIndicators = true;
            otGraphqlService.pageGraphQLCall(query).then((data) => {
                $scope.indicatorPortfolioOfPortfolios = data.indicatorPortfolioOfPortfolios;
                $scope.categories = data.indicatorCategories.categories;
                $scope.categories.forEach((category => {
                    category.selected = $scope.indicatorPortfolioOfPortfolios.categories.find((c) => {
                        return c.id === category.id;
                    }) !== undefined;
                }));
            }).finally(() => {
                $scope.loadingPortfolioGlobalIndicators = false;
            });
        };

        loadGlobalIndicators();

        $scope.updateCategories = () => {
            let selectedCategories = [];
            $scope.categories.forEach((category) => {
                if (category.selected) {
                    selectedCategories.push(category.id);
                }
            });
            ot.pageCall($http.put($scope.indicatorPortfolioOfPortfolios.links._globalIndicators, {
                categories: selectedCategories
            }));
        };

        $scope.unfold = (category) => {
            category.unfolded = true;
        };

        $scope.fold = (category) => {
            category.unfolded = false;
        };

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
            ot.viewCloseCommand(`/extension/indicators/portfolios/${portfolioId}`)
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
                categories {
                  id
                }
                links {
                  _update
                }
              }
              indicatorCategories {
                categories {
                  id
                  name
                  types {
                    id
                    name
                    link
                  }
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
                $scope.categories = data.indicatorCategories.categories;
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

                $scope.categories.forEach((category => {
                    category.selected = $scope.portfolio.categories.find((c) => {
                        return c.id === category.id;
                    }) !== undefined;
                }));

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

        $scope.updateCategories = () => {
            let selectedCategories = [];
            $scope.categories.forEach((category) => {
                if (category.selected) {
                    selectedCategories.push(category.id);
                }
            });
            ot.pageCall($http.put($scope.portfolio.links._update, {
                categories: selectedCategories
            }));
        };

        $scope.unfold = (category) => {
            category.unfolded = true;
        };

        $scope.fold = (category) => {
            category.unfolded = false;
        };

    })
    .config(function ($stateProvider) {
        $stateProvider.state('portfolio-view', {
            url: '/extension/indicators/portfolios/{portfolioId}',
            templateUrl: 'extension/indicators/portfolio-view.tpl.html',
            controller: 'PortfolioViewCtrl'
        });
    })
    .controller('PortfolioViewCtrl', function ($stateParams, $scope, $http, $location, ot, otGraphqlService, otAlertService) {
        const portfolioId = $stateParams.portfolioId;
        $scope.loadingPortfolio = true;

        const view = ot.view();
        view.title = "Portfolio";
        view.breadcrumbs = ot.homeBreadcrumbs();

        const query = `
            query LoadPortfolio($id: String!, $duration: Int) {
              indicatorPortfolios(id: $id) {
                id
                name
                links {
                  _update
                  _delete
                }
                categoryStats(duration: $duration) {
                  category {
                    id
                    name
                  }
                  stats {
                    total
                    count
                    minCount
                    minRating
                    avg
                    avgRating
                  }
                  previousStats {
                    stats {
                      avg
                      avgRating
                    }
                    avgTrend
                    durationSeconds
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
                      categoryStats(duration: $duration) {
                        category {
                          id
                          name
                        }
                        stats {
                          total
                          count
                          minCount
                          minRating
                          avg
                          avgRating
                        }
                        previousStats {
                          stats {
                            avg
                            avgRating
                          }
                          avgTrend
                          durationSeconds
                        }
                      }
                    }
                  }
                }
              }
            }
        `;

        const queryVariables = {
            id: portfolioId,
            duration: undefined
        };

        $scope.pageModel = {
            duration: undefined
        };

        const loadPortfolio = () => {
            $scope.loadingPortfolio = true;
            otGraphqlService.pageGraphQLCall(query, queryVariables).then((data) => {
                $scope.portfolio = data.indicatorPortfolios[0];
                view.title = `Portfolio: ${$scope.portfolio.name}`;

                view.commands = [
                    {
                        id: 'editProtfolio',
                        name: 'Edit portfolio',
                        cls: 'ot-command-update',
                        condition: () => $scope.portfolio.links._update,
                        link: `/extension/indicators/portfolios/${portfolioId}/edit`
                    },
                    {
                        id: 'deletePortfolio',
                        name: 'Delete portfolio',
                        cls: 'ot-command-delete',
                        condition: () => $scope.portfolio.links._delete,
                        action: deletePortfolio
                    },
                    ot.viewCloseCommand('/extension/indicators/portfolios')
                ];

                // Filtering projecct categories out
                $scope.portfolio.projects.forEach((project) => {
                    project.projectIndicators.categories.forEach((projectCategory) => {
                        let portfolioCategory = $scope.portfolio.categoryStats.find((stats) => stats.category.id === projectCategory.categoryStats.category.id);
                        projectCategory.enabled = !!portfolioCategory;
                        if (projectCategory.categoryStats.stats.avg === undefined) {
                            projectCategory.stats = {
                                compliance: 0,
                                rating: '-'
                            };
                        } else {
                            projectCategory.stats = {
                                compliance: projectCategory.categoryStats.stats.avg,
                                rating: projectCategory.categoryStats.stats.avgRating
                            };
                        }
                    });
                    project.projectIndicators.categories = project.projectIndicators.categories.filter((category) => category.enabled);
                });

                // Filling portfolio stats when not available
                $scope.portfolio.categoryStats.forEach((categoryStats) => {
                    if (categoryStats.stats.avg === undefined) {
                        categoryStats.compliance = 0;
                        categoryStats.rating = '-';
                    } else {
                        categoryStats.compliance = categoryStats.stats.avg;
                        categoryStats.rating = categoryStats.stats.avgRating;
                    }
                });

            }).finally(() => {
                $scope.loadingPortfolio = false;
            });
        };

        loadPortfolio();

        $scope.selectTrend = () => {
            if ($scope.pageModel.duration) {
                queryVariables.duration = Number($scope.pageModel.duration);
            } else {
                queryVariables.duration = undefined;
            }
            loadPortfolio();
        };

        const deletePortfolio = () => {
            otAlertService.confirm({
                title: "Portfolio deletion",
                message: `Do you want to delete the "${$scope.portfolio.name}" portfolio?`
            }).then(() => {
                return ot.pageCall($http.delete($scope.portfolio.links._delete));
            }).then(() => {
                $location.path(`/extension/indicators/portfolios`);
            });
        };

    })
    .directive('otExtensionIndicatorsStatus', function () {
        return {
            restrict: 'E',
            templateUrl: 'extension/indicators/directive.indicators-status.tpl.html',
            scope: {
                status: '=', // Requires: compliance, rating
                size: "@" // lg, md, sm
            },
            link: (scope) => {

                const backgroundColours = {
                    'A': '#00aa00',
                    'B': '#b0d513',
                    'C': '#eabe06',
                    'D': '#ed7d20',
                    'E': '#ee0000',
                    'F': '#aa0000',
                    '-': '#dddddd'
                };

                switch (scope.size) {
                    case 'lg':
                        scope.dimension = '60px';
                        scope.radius = '30px';
                        scope.statusFontSize = '40px';
                        break;
                    case 'md':
                        scope.dimension = '30px';
                        scope.radius = '15px';
                        scope.statusFontSize = '20px';
                        break;
                    default:
                        scope.dimension = '16px';
                        scope.radius = '8px';
                        scope.statusFontSize = '10px';
                        break;
                }
                scope.statusBackgroundColor = backgroundColours[scope.status.rating];
                scope.statusForegroundColor = '#ffffff';
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
    .directive('otExtensionIndicatorsStatsSummary', function () {
        return {
            restrict: 'E',
            templateUrl: 'extension/indicators/directive.indicators-stats-summary.tpl.html',
            scope: {
                stats: '=',
                previousStats: '=',
                item: '@'
            }
        };
    })
    .directive('otExtensionIndicatorsSource', function () {
        return {
            restrict: 'E',
            templateUrl: 'extension/indicators/directive.indicators-source.tpl.html',
            scope: {
                source: '='
            }
        };
    })
    .directive('otExtensionIndicatorsTrendSelection', function () {
        return {
            restrict: 'E',
            templateUrl: 'extension/indicators/directive.indicators-trend-selection.tpl.html',
            scope: {
                selectId: '@',
                model: '=',
                onTrendChange: '&'
            }
        };
    })
    .directive('otExtensionIndicatorsTrendDisplay', function () {
        return {
            restrict: 'E',
            templateUrl: 'extension/indicators/directive.indicators-trend-display.tpl.html',
            scope: {
                trend: '=',
                secondsSince: '=',
                previousCompliance: '=',
                previousRating: '='
            },
            transclude: true
        };
    })
    .directive('otExtensionIndicatorsMessage', function () {
        return {
            restrict: 'E',
            templateUrl: 'extension/indicators/directive.indicators-message.tpl.html',
            scope: {
            }
        };
    })

;