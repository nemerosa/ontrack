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

    .controller('AutoVersioningDependencyGraphCtrl', function ($stateParams, $scope, ot) {
        $scope.buildId = $stateParams.buildId;

        const view = ot.view();
        view.title = "TODO Change after it's been loaded"
    })