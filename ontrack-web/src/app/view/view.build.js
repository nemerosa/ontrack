angular.module('ot.view.build', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure'
])
    .config(function ($stateProvider) {
        $stateProvider.state('build', {
            url: '/build/{buildId}',
            templateUrl: 'app/view/view.build.tpl.html',
            controller: 'BuildCtrl'
        });
    })
    .controller('BuildCtrl', function ($scope, $stateParams, $http, ot, otStructureService) {
        var view = ot.view();
        // Build's id
        var buildId = $stateParams.buildId;

        // Loads the build
        function loadBuild() {
            otStructureService.getBuild(buildId).then(function (build) {
                $scope.build = build;
                // View configuration
                view.title = "Build " + build.name;
                view.description = build.description;
                // Commands
                view.commands = [
                    {
                        condition: function () {
                            return build.promote;
                        },
                        id: 'promote',
                        name: "Promote",
                        cls: 'ot-command-promote',
                        action: promote
                    },
                    ot.viewCloseCommand('/branch/' + build.branch.id)
                ];
            });
        }

        loadBuild();

        // Promotion
        function promote() {

        }
    })
;