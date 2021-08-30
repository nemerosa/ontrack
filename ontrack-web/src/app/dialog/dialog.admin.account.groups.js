angular.module('ot.dialog.admin.account.groups', [
    'ot.service.graphql'
])
    .controller('otDialogAdminAccountGroups', function ($scope, $modalInstance, config, otGraphqlService) {
        // GraphQL query to run
        const query = `query AccountGroups($id: Int!) {
              accounts(id: $id) {
                name
                authenticationSource {
                  provider
                  key
                  name
                }
                groups {
                  name
                }
                contributedGroups {
                  name
                  mappings {
                    authenticationSource {
                      provider
                      key
                      name
                    }
                    name
                    group {
                      name
                    }
                  }
                }
                providedGroups
              }
            }`;
        const queryVars = {id: config.account.id};
        // Loading the groups
        $scope.loadingAccountGroups = false;
        function loadAccountGroups() {
            $scope.loadingAccountGroups = true;
            otGraphqlService.pageGraphQLCall(query, queryVars).then((data) => {
                $scope.account = data.accounts[0];
            }).finally(() => {
                $scope.loadingAccountGroups = false;
            });
        }
        loadAccountGroups();
        // Cancelling the dialog
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    })
;