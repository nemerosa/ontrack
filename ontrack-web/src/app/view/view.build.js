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
                view.breadcrumbs = ot.branchBreadcrumbs(build.branch);
                // Loads the promotion runs
                loadPromotionRuns();
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

        // Page initialisation
        loadBuild();

        // Loads the promotion runs
        function loadPromotionRuns() {
            ot.call($http.get($scope.build.lastPromotionRuns.href)).then(function (promotionRunCollection) {
                angular.forEach(promotionRunCollection.resources, function (promotionRun) {
                    promotionRun.image = promotionRun.promotionLevel.image;
                });
                $scope.promotionRunCollection = promotionRunCollection;
            });
        }

        // Promotion
        function promote() {
            otStructureService.create($scope.build.promote.href, 'Promotion for the build').then(loadPromotionRuns);
        }
    })
;