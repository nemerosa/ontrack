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
            query BranchIssues($id: Int!, $count: Int!) {
              branches(id: $id) {
                id
                name
                project {
                  id
                  name
                }
                validationIssues(passed: false, count: $count) {
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
                    links {
                        _page
                    }
                    validationStamp {
                      name
                      description
                      image
                      _image
                      links {
                        _page
                      }
                    }
                    build {
                      name
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
                    }
                  }
                }
              }
            }
        `;

        // Showing the details
        $scope.displayOptions = {
            showingDetails: false,
            historyLimit: 10
        };

        let viewInitialized = false;

        const loadIssues = () => {
            $scope.loadingIssues = true;

            const queryVariables = {
                id: $stateParams.branch,
                count: $scope.displayOptions.historyLimit
            };

            otGraphqlService.pageGraphQLCall(query, queryVariables).then(data => {
                $scope.branch = data.branches[0];
                if (!viewInitialized) {
                    // Title
                    view.title = `Issues opened for ${$scope.branch.name}`;
                    // View configuration
                    view.breadcrumbs = ot.branchBreadcrumbs($scope.branch);
                    // Commands
                    view.commands = [
                        ot.viewCloseCommand('/branch/' + $scope.branch.id)
                    ];
                    // OK
                    viewInitialized = true;
                }
                // Unique statuses
                $scope.statuses = $scope.branch.validationIssues
                    .map(it => it.issue.status.name)
                    .filter((v, i, a) => a.indexOf(v) === i) // Gets unique elements only
                    .sort();
                // Selection of statuses
                $scope.selectedStatuses = $scope.statuses.map(it => ({
                    status: it,
                    selected: true
                }));
                // Is an issue selected?
                $scope.selectedStatus = function (validationIssue) {
                    let selection = $scope.selectedStatuses.find(it => it.status === validationIssue.issue.status.name);
                    return selection && selection.selected;
                };
            }).finally(() => {
                $scope.loadingIssues = false;
            });
        };

        // Loads the issues
        loadIssues();

        // Reloading the issues
        $scope.reloadIssues = loadIssues;
    })

;