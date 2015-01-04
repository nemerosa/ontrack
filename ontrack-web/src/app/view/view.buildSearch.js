angular.module('ot.view.buildSearch', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure'
])
    .config(function ($stateProvider) {
        $stateProvider.state('build-search', {
            url: '/build-search/{projectId}',
            templateUrl: 'app/view/view.buildSearch.tpl.html',
            controller: 'BuildSearchCtrl'
        });
    })
    .controller('BuildSearchCtrl', function ($location, $scope, $stateParams, $state, $http, ot, otStructureService) {
        var view = ot.view();
        // Project's id
        var projectId = $stateParams.projectId;

        // Loading the project
        function loadProject() {
            otStructureService.getProject(projectId).then(function (projectResource) {
                $scope.project = projectResource;
                // View settings
                view.breadcrumbs = ot.projectBreadcrumbs(projectResource);
                view.title = "Build search";
                // View commands
                view.commands = [
                    ot.viewApiCommand(projectResource._buildSearch),
                    ot.viewCloseCommand('/project/' + projectResource.id)
                ];
                return ot.pageCall($http.get(projectResource._buildDiffActions));
            }).then(function (diffActionResources) {
                $scope.buildDiffActions = diffActionResources.resources;
                return ot.pageCall($http.get($scope.project._buildSearch));
            }).then(function (searchForm) {
                $scope.searchForm = searchForm;
                // Locally saved criteria
                var json = localStorage.getItem('build_search_' + projectId);
                if (json) {
                    $scope.data = JSON.parse(json);
                } else {
                    $scope.data = {
                        maximumCount: 10
                    };
                }
            });
        }

        // Initialization
        loadProject();

        // Selected builds
        $scope.selectedBuilds = [];
        $scope.selectedBuild = {
            from: '',
            to: ''
        };

        // Search
        $scope.searching = false;
        $scope.submitSearch = function () {
            $scope.searching = true;
            ot.pageCall($http.get($scope.searchForm._search, {params: $scope.data})).then(function (result) {
                $scope.buildViews = result.resources;
                // Stores
                localStorage.setItem(
                    'build_search_' + projectId,
                    JSON.stringify($scope.data)
                );
            }).finally(function () {
                $scope.searching = false;
            });
        };

        // Toggle advanced search
        $scope.toggleAdvancedSearch = function () {
            $scope.advancedSearch = !$scope.advancedSearch;
        };

        // Displays the permalink
        $scope.displayPermalink = function () {
            var jsonFilter = JSON.stringify($scope.data);
            $location.hash(jsonFilter);
        };

        // Selects a build
        $scope.selectBuild = function (buildView) {
            if ($scope.selectedBuilds.indexOf(buildView) < 0) {
                $scope.selectedBuilds.push(buildView);
                // Auto selection
                if ($scope.selectedBuilds.length == 2) {
                    $scope.selectedBuild.from = $scope.selectedBuilds[0].build.id;
                    $scope.selectedBuild.to = $scope.selectedBuilds[1].build.id;
                }
                // Removes from the list of results
                var pos = $scope.buildViews.indexOf(buildView);
                if (pos >= 0) {
                    $scope.buildViews.splice(pos, 1);
                }
            }
        };

        // Unselects a build
        $scope.unselectBuild = function (buildView) {
            var pos = $scope.selectedBuilds.indexOf(buildView);
            if (pos >= 0) {
                $scope.selectedBuilds.splice(pos, 1);
                if ($scope.selectedBuilds.length == 1) {
                    $scope.selectedBuild.from = '';
                    $scope.selectedBuild.to = '';
                }
            }
        };

        // Change log between two builds
        $scope.buildDiff = function (buildDiffAction) {
            if ($scope.selectedBuild.from && $scope.selectedBuild.from && $scope.selectedBuild.from != $scope.selectedBuild.to) {
                $state.go(buildDiffAction.id, {
                    from: $scope.selectedBuild.from,
                    to: $scope.selectedBuild.to
                });
            }
        };

    })
;