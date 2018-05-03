angular.module('ot.view.validationStamp', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure',
    'ot.service.graphql'
])
    .config(function ($stateProvider) {
        $stateProvider.state('validationStamp', {
            url: '/validationStamp/{validationStampId}',
            templateUrl: 'app/view/view.validationStamp.tpl.html',
            controller: 'ValidationStampCtrl'
        });
    })
    .controller('ValidationStampCtrl', function ($state, $scope, $stateParams, $http, ot, otStructureService, otAlertService, otGraphqlService) {
        const view = ot.view();
        let viewInitialised = false;
        // ValidationStamp's id
        const validationStampId = $stateParams.validationStampId;

        // Initial query parameters
        const queryVariables = {
            validationStampId: validationStampId,
            offset: 0,
            size: 20
        };

        // Query for the validation stamp
        const query = `
            query PaginatedValidationRuns($validationStampId: Int!, $offset: Int = 0, $size: Int = 20) {
                validationStamp(id: $validationStampId) {
                    id
                    name
                    description
                    image
                    _image
                    decorations {
                      decorationType
                      data
                      error
                      feature {
                        id
                      }
                    }
                    branch {
                      id
                      name
                      project {
                        id
                        name
                      }
                    }
                    links {
                      _self
                      _bulkUpdate
                      _update
                      _delete
                      _properties
                      _events
                    }
                    validationRunsPaginated(offset: $offset, size: $size) {
                      pageInfo {
                        totalSize
                        currentOffset
                        currentSize
                        previousPage {
                          offset
                          size
                        }
                        nextPage {
                          offset
                          size
                        }
                        pageIndex
                        pageTotal
                      }
                      pageItems {
                        id
                        runOrder
                        build {
                          name
                        }
                        validationRunStatuses {
                          statusID {
                            id
                            name
                          }
                          description
                          creation {
                            user
                            time
                          }
                        }
                      }
                    }
                }
            }`;

        // Loading the validation stamp
        function loadValidationStamp() {
            otGraphqlService.pageGraphQLCall(query, queryVariables).then(function (data) {
                const validationStamp = data.validationStamp;
                $scope.validationStamp = validationStamp;
                // View setup
                if (!viewInitialised) {
                    // View title
                    view.breadcrumbs = ot.branchBreadcrumbs(validationStamp.branch);
                    // Commands
                    view.commands = [
                        {
                            condition: function () {
                                return validationStamp.links._update;
                            },
                            id: 'updateValidationStampImage',
                            name: "Change image",
                            cls: 'ot-command-validation-stamp-image',
                            action: changeImage
                        },
                        {
                            condition: function () {
                                return validationStamp.links._update;
                            },
                            id: 'updateValidationStamp',
                            name: "Update validation stamp",
                            cls: 'ot-command-validation-stamp-update',
                            action: function () {
                                otStructureService.update(
                                    validationStamp.links._update,
                                    "Update validation stamp"
                                ).then(loadValidationStamp);
                            }
                        },
                        {
                            condition: function () {
                                return validationStamp.links._delete;
                            },
                            id: 'deleteValidationStamp',
                            name: "Delete validation stamp",
                            cls: 'ot-command-validation-stamp-delete',
                            action: function () {
                                otAlertService.confirm({
                                    title: "Deleting a validation stamp",
                                    message: "Do you really want to delete the validation stamp " + validationStamp.name +
                                    " and all its associated data?"
                                }).then(function () {
                                    return ot.call($http.delete(validationStamp.links._delete));
                                }).then(function () {
                                    $state.go('branch', {branchId: validationStamp.branch.id});
                                });
                            }
                        },
                        {
                            condition: function () {
                                return validationStamp.links._bulkUpdate;
                            },
                            id: 'bulkUpdateValidationStamp',
                            name: "Bulk update",
                            cls: 'ot-command-update',
                            action: function () {
                                otAlertService.confirm({
                                    title: "Validation stamps bulk update",
                                    message: "Updates all other validation stamps with the same name?"
                                }).then(function () {
                                    return ot.call($http.put(validationStamp.links._bulkUpdate, {}));
                                }).then(loadValidationStamp);
                            }
                        },
                        ot.viewApiCommand($scope.validationStamp.links._self),
                        ot.viewCloseCommand('/branch/' + $scope.validationStamp.branch.id)
                    ];
                    // View OK now
                    viewInitialised = true;
                }
            });
        }

        // Initialisation
        loadValidationStamp();

        // Changing the image
        function changeImage() {
            otStructureService.changeValidationStampImage($scope.validationStamp).then(loadValidationStamp);
        }

        // Switching the page
        $scope.switchPage = function (pageLink) {
            ot.pageCall($http.get(pageLink)).then(function (validationRunResources) {
                $scope.validationRunResources = validationRunResources;
            });
        };

    })
;