angular.module('ontrack.extension.casc', [
    'ot.service.core',
    'ot.service.graphql'
])
    .config(function ($stateProvider) {
        $stateProvider.state('casc-control', {
            url: '/extension/casc/casc-control',
            templateUrl: 'extension/casc/casc-contrl.tpl.html',
            controller: 'CascControlCtrl'
        });
    })
    .controller('CascControlCtrl', function ($scope, ot, otGraphqlService) {
        const view = ot.view();
        view.title = "Configuration as Code";
        view.commands = [
            ot.viewCloseCommand('/home')
        ];

        const loadLocations = () => {
            $scope.loadingLocations = true;
            otGraphqlService.pageGraphQLCall(`
                {
                    casc {
                        locations
                    }
                }
            `).then(data => {
                $scope.locations = data.casc.locations;
            }).finally(() => {
                $scope.loadingLocations = false;
            })
        };

        loadLocations();
    })
    .config(function ($stateProvider) {
        $stateProvider.state('casc-schema', {
            url: '/extension/casc/casc-schema',
            templateUrl: 'extension/casc/casc-schema.tpl.html',
            controller: 'CascSchemaCtrl'
        });
    })
    .controller('CascSchemaCtrl', function ($scope, ot, otGraphqlService) {
        const view = ot.view();
        view.title = "Configuration as Code Schema";
        view.breadcrumbs = ot.homeBreadcrumbs().concat([
            ['CasC', '#/extension/casc/casc-control']
        ])
        view.commands = [
            ot.viewCloseCommand('/extension/casc/casc-control')
        ];

        const loadSchema = () => {
            $scope.loadingSchema = true;
            otGraphqlService.pageGraphQLCall(`
                {
                    casc {
                        schema
                    }
                }
            `).then(data => {
                $scope.schema = data.casc.schema;
            }).finally(() => {
                $scope.loadingSchema = false;
            })
        };

        loadSchema();
    })
    .directive("otExtensionCascSchemaType", () => ({
        restrict: 'E',
        templateUrl: 'extension/casc/directive.casc-schema-type.tpl.html',
        scope: {
            type: '='
        }
    }))
    .directive("otExtensionCascSchemaTypeSimple", () => ({
        restrict: 'E',
        templateUrl: 'extension/casc/directive.casc-schema-type-simple.tpl.html',
        scope: {
            type: '='
        }
    }))
    // Tip for recursive directives
    .directive("otExtensionCascSchemaTypeObject", function ($compile) {
        return {
            restrict: 'E',
            template: '<div></div>',
            scope: {
                type: '='
            },
            link: function (scope, element) {
                if (angular.isDefined(scope.type)) {
                    $compile('<ot-extension-casc-schema-type type="type"</ot-extension-casc-schema-type>')(scope, function (cloned, scope) {
                        element.append(cloned);
                    });
                }
            }

        };
    })
;