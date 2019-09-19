angular.module('ot.view.validationRun', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure',
    'ot.service.graphql'
])
    .config(function ($stateProvider) {
        $stateProvider.state('validationRun', {
            url: '/validationRun/{validationRunId}',
            templateUrl: 'app/view/view.validationRun.tpl.html',
            controller: 'ValidationRunCtrl'
        });
    })
    .controller('ValidationRunCtrl', function ($scope, $stateParams, $http, ot, otStructureService, otGraphqlService) {
        const view = ot.view();
        // Validation run's id
        const validationRunId = $stateParams.validationRunId;
        // GraphQL query to load the validation run
        const query = `
            query LoadValidationRun($validationRunId: Int!) {
                validationRuns(id: $validationRunId) {
                    id
                    runOrder
                    validationStamp {
                        id
                        name
                        image
                        _image
                    }
                    decorations {
                      decorationType
                      data
                      error
                      feature {
                        id
                      }
                    }
                    build {
                      id
                      name
                      branch {
                        id
                        name
                        project {
                          id
                          name
                        }
                      }
                    }
                    creation {
                      user
                      time
                    }
                    data {
                      descriptor {
                        id
                        feature {
                          id
                        }
                      }
                      data
                    }
                    validationRunStatuses {
                      id
                      statusID {
                        id
                        name
                      }
                      creation {
                        user
                        time
                      }
                      description
                      annotatedDescription
                      links {
                        _comment
                      }
                    }
                    links {
                        _self
                        _validationRunStatusChange
                        _properties
                        _extra
                    }
                }
            }
        `;
        const queryVariables = {
            validationRunId: validationRunId
        };
        let viewInitialised = false;

        // Loads the validation run
        function loadValidationRun() {
            otGraphqlService.pageGraphQLCall(query, queryVariables).then(function (data) {
                const validationRun = data.validationRuns[0];
                $scope.validationRun = validationRun;
                if (!viewInitialised) {
                    // View configuration
                    view.breadcrumbs = ot.buildBreadcrumbs(validationRun.build);
                    // Commands
                    view.commands = [
                        ot.viewApiCommand(validationRun.links._self),
                        ot.viewCloseCommand('/build/' + validationRun.build.id)
                    ];
                    // OK
                    viewInitialised = true;
                }
            });
        }

        // Initialisation
        loadValidationRun();

        // Changing the validation run status
        $scope.validationRunStatusChange = function () {
            otStructureService.create(
                $scope.validationRun.links._validationRunStatusChange,
                'Status').then(loadValidationRun);
        };

    })
;