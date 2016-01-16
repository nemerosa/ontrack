angular.module('ontrack.extension.git', [
        'ot.extension.git.configuration',
        'ot.extension.git.sync',
        'ot.extension.git.changelog',
        'ot.extension.git.issue',
        'ot.extension.git.commit',
        'ot.service.core',
        'ot.service.structure'
    ])
    .config(function ($stateProvider) {
        // Git project sync
        $stateProvider.state('git-project-sync', {
            url: '/extension/git/project-sync/{projectId}',
            templateUrl: 'extension/git/git.project-sync.tpl.html',
            controller: 'GitProjectSyncCtrl'
        });
    })
    .controller('GitProjectSyncCtrl', function ($http, $scope, $stateParams, ot, otStructureService) {
        // Gets the project ID from the parameters
        var projectId = $stateParams.projectId;
        // View definition
        var view = ot.view();
        view.title = 'Git project synchronisation';
        // Loading the project
        otStructureService.getProject(projectId).then(function (project) {
            $scope.project = project;
            // Sub page of the project
            view.breadcrumbs = ot.projectBreadcrumbs(project);
            // Commands
            view.commands = [
                ot.viewCloseCommand('/project/' + project.id)
            ];
        });
    })
    .directive('otExtensionGitCommitSummary', function () {
        return {
            restrict: 'E',
            transclude: true,
            templateUrl: 'extension/git/directive.commit.summary.tpl.html',
            scope: {
                uiCommit: '=',
                title: '@'
            }
        };
    })
    .directive('otExtensionGitCommitBuilds', function () {
        return {
            restrict: 'E',
            templateUrl: 'extension/git/directive.commit.builds.tpl.html',
            scope: {
                commitInfo: '='
            }
        };
    })
    .directive('otExtensionGitCommitPromotions', function () {
        return {
            restrict: 'E',
            templateUrl: 'extension/git/directive.commit.promotions.tpl.html',
            scope: {
                commitInfo: '='
            }
        };
    })
;