angular.module('ot.view.buildSearch', [
    'ui.router',
    'ot.service.core',
    'ot.service.form',
    'ot.service.structure',
    'ot.service.copy'
])
    .config(function ($stateProvider) {
        $stateProvider.state('build-search', {
            url: '/build-search/{projectId}',
            templateUrl: 'app/view/view.buildSearch.tpl.html',
            controller: 'BuildSearchCtrl'
        });
    })
    .controller('BuildSearchCtrl', function ($scope, $stateParams, $state, $http, ot, otStructureService, otFormService) {
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
                return ot.pageCall($http.get(projectResource._buildSearch));
            }).then(function (searchForm) {
                $scope.searchForm = searchForm;
                $scope.searchData = otFormService.prepareForDisplay(searchForm);
            });
        }

        // Initialization
        loadProject();

        // Search
        $scope.submitSearch = function () {
            var data = otFormService.prepareForSubmit($scope.searchForm, $scope.searchData);
            ot.pageCall($http.get($scope.searchForm._search, {params: data}));
        };

    })
;