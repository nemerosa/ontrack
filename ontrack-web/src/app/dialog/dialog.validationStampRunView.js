angular.module('ot.dialog.validationStampRunView', [
    'ot.service.structure',
    'ot.service.graphql'
])
    .controller('otDialogValidationStampRunView', function ($scope, $modalInstance, config, otStructureService, otGraphqlService) {
        // General configuration
        $scope.config = config;
        // Query and pagination
        const queryParams = {
            buildId: $scope.config.build.id,
            validationStamp: $scope.config.validationStamp.name,
            offset: 0,
            size: 4
        };
        // Loading the validation runs
        function loadValidationRuns() {
            $scope.loadingValidationRuns = true;
            otGraphqlService.pageGraphQLCall(`query ValidationRuns($buildId: Int!, $validationStamp: String!, $offset: Int!, $size: Int!) {
              builds(id: $buildId) {
                id
                name
                validations(validationStamp: $validationStamp) {
                  validationStamp {
                      id
                      name
                      image
                      _image
                      validationRunsPaginated(buildId: $buildId, offset: $offset, size: $size) {
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
                            description
                            runInfo {
                              sourceType
                              sourceUri 
                              triggerType
                              triggerData
                              runTime
                            }
                            validationRunStatuses {
                              creation {
                                user
                                time
                              }
                              description
                              statusID {
                                id
                                name
                              }
                            }
                            links {
                              _validationRunStatusChange
                            }
                          }
                      }
                  }
                }
              }
            }`, queryParams).then(function (data) {
                $scope.build = data.builds[0];
                $scope.validation = data.builds[0].validations[0];
            }).finally(function () {
                $scope.loadingValidationRuns = false;
            });
        }
        loadValidationRuns();
        // Navigating
        $scope.navigate = function (pageRequest) {
            queryParams.offset = pageRequest.offset;
            loadValidationRuns();
        };
        // Cancelling the dialog
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
        // Changing the status of a validation run
        $scope.changeStatus = function (validationRun) {
            // Closes this dialog first
            $scope.cancel();
            // Calling the service
            otStructureService.create(
                validationRun.links._validationRunStatusChange,
                'Status'
            ).then(function () {
                if (config.callbackOnStatusChange) {
                    config.callbackOnStatusChange();
                } else {
                    // Goes to the validation run page
                    location.href = '#/validationRun/' + validationRun.id;
                }
            });
        };
    })
;