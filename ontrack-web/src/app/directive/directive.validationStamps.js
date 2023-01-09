angular.module('ot.directive.validationStamps', [
    'ot.service.core',
    'ot.service.graphql',
    'ot.service.structure'
])
    .directive('otValidationStamps', function ($http, ot, otGraphqlService, otStructureService) {
        return {
            restrict: 'E',
            templateUrl: 'app/directive/directive.validationStamps.tpl.html',
            scope: {
                branchId: '='
            },
            controller: ($scope) => {

                // Loading indicator
                $scope.loadingValidationStamps = true;

                // Query: validation stamps
                const gqlValidationStamps = `
                    query ValidationStamps(
                        $branchId: Int!,
                    ) {
                        branches(id: $branchId) {
                            id
                            name
                            project {
                                id
                                name
                            }
                            links {
                                _createValidationStamp
                                _reorderValidationStamps
                            }
                            validationStamps {
                                id
                                name
                                description
                                image
                                _image
                                decorations {
                                    ...decorationContent
                                }
                                dataType {
                                    descriptor {
                                        id
                                        displayName
                                    }
                                    config
                                }
                            }
                        }
                    }

                    fragment decorationContent on Decoration {
                      decorationType
                      error
                      data
                      feature {
                        id
                      }
                    }
                `;

                // Loading the validation stamps
                const loadValidationStamps = () => {
                    $scope.loadingValidationStamps = true;
                    otGraphqlService.pageGraphQLCall(gqlValidationStamps, {
                        branchId: $scope.branchId
                    }).then(data => {
                        $scope.branch = data.branches[0];
                        $scope.validationStamps = $scope.branch.validationStamps;
                        // Reordering options
                        $scope.validationStampSortOptions = {
                            disabled: !$scope.branch.links._reorderValidationStamps,
                            stop: () => {
                                const ids = $scope.validationStamps.map(vs => vs.id);
                                ot.call($http.put(
                                    $scope.branch.links._reorderValidationStamps,
                                    {ids: ids}
                                ));
                            }
                        };
                    }).finally(() => {
                        $scope.loadingValidationStamps = false;
                    });
                };

                // Loading the validation stamps on branch being ready
                $scope.$watch('branchId', () => {
                    if ($scope.branchId) {
                        loadValidationStamps();
                    }
                });

                // Creation of a new validation stamp
                $scope.createValidationStamp = () => {
                    otStructureService.create($scope.branch.links._createValidationStamp, 'New validation stamp').then(loadValidationStamps);
                };
            }
        };
    })
;