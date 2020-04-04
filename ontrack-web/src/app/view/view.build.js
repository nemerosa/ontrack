angular.module('ot.view.build', [
    'ui.router',
    'ot.service.core',
    'ot.service.structure',
    'ot.service.graphql'
])
    .config(function ($stateProvider) {
        $stateProvider.state('build', {
            url: '/build/{buildId}',
            templateUrl: 'app/view/view.build.tpl.html',
            controller: 'BuildCtrl'
        });
    })
    .controller('BuildCtrl', function ($state, $scope, $stateParams, $http, ot, otStructureService, otAlertService, otGraphqlService) {
            const view = ot.view();
            // Build's id
            const queryParams = {
                buildId: Number($stateParams.buildId),
                usingOffset: 0,
                usedByOffset: 0,
                validationRunsOffset: 0
            };
            // GraphQL query
            const query = `
                query Build($buildId: Int!, $usingOffset: Int!, $usedByOffset: Int!, $validationRunsOffset: Int!) {
                  builds(id: $buildId) {
                    id
                    name
                    description
                    annotatedDescription
                    creation {
                      user
                      time
                    }
                    branch {
                      id
                      name
                      project {
                        id
                        name
                      }
                    }
                    using(offset: $usingOffset, size: 10) {
                      pageInfo {
                        ...pageInfoContent
                      }
                      pageItems {
                        ...buildLinkContent
                      }
                    }
                    usedBy(offset: $usedByOffset, size: 10) {
                      pageInfo {
                        ...pageInfoContent
                      }
                      pageItems {
                        ...buildLinkContent
                      }
                    }
                    runInfo {
                      ...runInfoContent
                    }
                    decorations {
                      decorationType
                      error
                      data
                      feature {
                        id
                      }
                    }
                    links {
                      _self
                      _promote
                      _validate
                      _update
                      _delete
                      _actions
                      _next
                      _previous
                      _changeLogPage
                      _properties
                      _events
                      _extra
                    }
                    promotionRuns {
                      description
                      annotatedDescription
                      creation {
                        user
                        time
                      }
                      links {
                        _delete
                      }
                      promotionLevel {
                        id
                        name
                        _image
                        image
                        links {
                          _page
                        }
                      }
                    }
                    validationRunsPaginated(offset: $validationRunsOffset, size: 10) {
                      pageInfo {
                        ...pageInfoContent
                      }
                      pageItems {
                        id
                        runOrder
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
                        runInfo {
                          ...runInfoContent
                        }
                        links {
                          _page
                        }
                        validationStamp {
                          id
                          name
                          image
                          _image
                          dataType {
                            descriptor {
                              id
                              displayName
                              feature {
                                id
                              }
                            }
                            config
                          }
                          links {
                            _page
                          }
                        }
                      }
                    }
                  }
                }
                
                fragment buildLinkContent on Build {
                  name
                  branch {
                    name
                    links {
                      _page
                    }
                    project {
                      name
                      links {
                        _page
                      }
                    }
                  }
                  links {
                    _page
                  }
                  decorations {
                    decorationType
                    error
                    data
                    feature {
                      id
                    }
                  }
                  promotionRuns(lastPerLevel: true) {
                    promotionLevel {
                      id
                      name
                      image
                      _image
                      links {
                        _page
                      }
                    }
                  }
                }
                
                fragment pageInfoContent on PageInfo {
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
                }
                
                fragment runInfoContent on RunInfo {
                  sourceType
                  sourceUri
                  triggerType
                  triggerData
                  runTime
                }
            `;

            // Loads the build
            function loadBuild() {
                otGraphqlService.pageGraphQLCall(query, queryParams).then(function (data) {
                    const build = data.builds[0];
                    $scope.build = build;
                    // View configuration
                    view.breadcrumbs = ot.branchBreadcrumbs(build.branch);
                    // Commands
                    view.commands = [
                        {
                            condition: function () {
                                return build.links._promote;
                            },
                            id: 'promote',
                            name: "Promote",
                            cls: 'ot-command-promote',
                            action: promote
                        },
                        {
                            condition: function () {
                                return build.links._validate;
                            },
                            id: 'validate',
                            name: "Validation run",
                            cls: 'ot-command-validate',
                            action: validate
                        },
                        {
                            condition: function () {
                                return build.links._update;
                            },
                            id: 'updateBuild',
                            name: "Update build",
                            cls: 'ot-command-build-update',
                            action: function () {
                                otStructureService.update(
                                    build.links._update,
                                    "Update build"
                                ).then(loadBuild);
                            }
                        },
                        {
                            condition: function () {
                                return build.links._delete;
                            },
                            id: 'deleteBuild',
                            name: "Delete build",
                            cls: 'ot-command-build-delete',
                            action: function () {
                                otAlertService.confirm({
                                    title: "Deleting a build",
                                    message: "Do you really want to delete the build " + build.name +
                                    " and all its associated data?"
                                }).then(function () {
                                    return ot.call($http.delete(build.links._delete));
                                }).then(function () {
                                    $state.go('branch', {branchId: build.branch.id});
                                });
                            }
                        },
                        ot.viewApiCommand(build.links._self),
                        ot.viewActionsCommand(build.links._actions),
                        ot.viewCloseCommand('/branch/' + build.branch.id)
                    ];
                    return ot.call($http.get(build.links._next));
                }).then(function (nextBuild) {
                    if (nextBuild && nextBuild.id) {
                        view.commands.splice(0, 0, {
                            id: 'nextBuild',
                            name: "Next build",
                            cls: 'ot-command-next',
                            absoluteLink: nextBuild._page,
                            title: "Go to build " + nextBuild.name
                        });
                    }
                    return ot.call($http.get($scope.build.links._previous));
                }).then(function (previousBuild) {
                    if (previousBuild && previousBuild.id) {
                        view.commands.splice(0, 0, {
                            id: 'previousBuild',
                            name: "Previous build",
                            cls: 'ot-command-previous',
                            absoluteLink: previousBuild._page,
                            title: "Go to build " + previousBuild.name
                        });
                        // Change log since previous?
                        if ($scope.build.links._changeLogPage) {
                            let link = `${$scope.build.links._changeLogPage}?from=${previousBuild.id}&to=${$scope.build.id}`;
                            view.commands.splice(0, 0, {
                                id: 'changeLogSincePreviousBuild',
                                name: "Change log",
                                cls: 'ot-command-changelog',
                                absoluteLink: link,
                                title: "Change log since " + previousBuild.name
                            });
                        }
                    }
                });
            }

            // Page initialisation
            loadBuild();

            // Promotion
            function promote() {
                otStructureService.create($scope.build.links._promote, 'Promotion for the build').then(loadBuild);
            }

            // Validation
            function validate() {
                otStructureService.create($scope.build.links._validate, 'Validation for the build').then(loadBuild);
            }

            // Deleting a promotion run
            $scope.deletePromotionRun = function (promotionRun) {
                otAlertService.confirm({
                    title: "Promotion deletion",
                    message: "Do you really want to delete this promotion?"
                }).then(function () {
                    return ot.call($http.delete(promotionRun.links._delete));
                }).then(loadBuild);
            };

            // Navigating the validation runs
            $scope.navigateValidationRuns = function (pageRequest) {
                queryParams.validationRunsOffset = pageRequest.offset;
                loadBuild();
            };

            // Navigating the "using" section
            $scope.navigateUsing = function (pageRequest) {
                queryParams.usingOffset = pageRequest.offset;
                loadBuild();
            };

            // Navigating the "used by" section
            $scope.navigateUsedBy = function (pageRequest) {
                queryParams.usedByOffset = pageRequest.offset;
                loadBuild();
            };
        }
    )
;