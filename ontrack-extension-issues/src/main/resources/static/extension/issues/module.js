angular.module('ontrack.extension.issues', [
    'ot.service.core',
    'ot.service.graphql'
])
    .config(function ($stateProvider) {
        $stateProvider.state('branch-issues', {
            url: '/extension/issues/branch-issues/{branch}',
            templateUrl: 'extension/issues/branch-issues.tpl.html',
            controller: 'BranchIssuesCtrl'
        });
    })
    .controller('BranchIssuesCtrl', function ($stateParams, $scope, $http, ot, otGraphqlService) {

        $scope.loadingIssues = true;

        const view = ot.view();
        view.title = "";

        const query = `
            query BranchIssues($id: Int!) {
              branches(id: $id) {
                id
                name
                project {
                  id
                  name
                }
                validationIssues(passed: false) {
                  issue {
                    displayKey
                    summary
                    url
                    updateTime
                    status {
                      name
                    }
                  }
                  validationRuns {
                    validationStamp {
                      name
                      image
                      _image
                      links {
                        _page
                      }
                    }
                    build {
                      name
                      releaseProperty {
                        value
                      }
                    }
                  }
                }
              }
            }
        `;

        const queryVariables = {
            id: $stateParams.branch
        };

        otGraphqlService.pageGraphQLCall(query, queryVariables).then(data => {
            $scope.branch = data.branches[0];
            // Title
            view.title = `Issues opened for ${$scope.branch.name}`;
            // View configuration
            view.breadcrumbs = ot.branchBreadcrumbs($scope.branch);
            // Commands
            view.commands = [
                ot.viewCloseCommand('/branch/' + $scope.branch.id)
            ];
        }).finally(() => {
            $scope.loadingIssues = false;
        });
    })

;