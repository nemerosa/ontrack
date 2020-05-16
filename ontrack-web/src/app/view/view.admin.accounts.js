angular.module('ot.view.admin.accounts', [
    'ui.router',
    'ot.service.core',
    'ot.service.graphql'
])
    .config(function ($stateProvider) {
        $stateProvider.state('admin-accounts', {
            url: '/admin-accounts',
            templateUrl: 'app/view/view.admin.accounts.tpl.html',
            controller: 'AdminAccountsCtrl'
        });
    })

    .controller('AdminAccountsCtrl', function ($scope, $http, ot, otFormService, otAlertService, otGraphqlService) {
        var view = ot.view();
        view.title = "Account management";

        $scope.activeTab = 'accounts';

        $scope.selectAccountsTab = () => {
            $scope.activeTab = 'accounts';
        };

        $scope.selectAccountGroupsTab = () => {
            $scope.activeTab = 'account-groups';
        };

        const query = `
            {
              accounts {
                id
                name
                fullName
                email
                role
                authenticationSource {
                  key
                  provider
                  name
                }
                groups {
                  name
                }
                token {
                  creation
                  valid
                  validUntil
                }
                links {
                  _update
                  _delete
                  _revokeToken
                }
              }
              accountGroups {
                id
                name
                description
                autoJoin
                links {
                  _update
                  _delete
                }
              }
            }
        `;

        $scope.loading = false;

        // Loading the data
        const load = () => {
            $scope.loading = true;
            otGraphqlService.pageGraphQLCall(query).then((data) => {
                $scope.accounts = data.accounts;
                $scope.groups = data.accountGroups;
                // Commands
                view.commands = [
                    // Global permissions
                    {
                        id: 'admin-global-acl',
                        name: "Global permissions",
                        cls: 'ot-command-admin-global-acl',
                        link: '/admin-global-acl'
                    },
                    // Group mappings
                    {
                        id: 'admin-group-mappings',
                        name: "Group mappings",
                        cls: 'ot-command-admin-action',
                        link: '/admin-group-mappings'
                    },
                    ot.viewCloseCommand('/home')
                ];
            }).finally(() => {
                $scope.loading = false;
            });
        };

        // Initialisation
        load();

        $scope.sourceDisplayName = (source) => {
            if (source.key) {
                return `${source.name} (${source.provider})`;
            } else {
                return source.name;
            }
        };

        // Creating an account
        $scope.createAccount = function () {
            otFormService.create('/rest/accounts/create', "Account creation").then(load);
        };

        // Updating an account
        $scope.updateAccount = function (account) {
            otFormService.update(account.links._update, "Updating account").then(load);
        };

        // Deleting an account
        $scope.deleteAccount = function (account) {
            otAlertService.confirm({
                title: "Account deletion",
                message: "Do you really want to delete this account?"
            }).then(function () {
                ot.call($http.delete(account.links._delete)).then(load);
            });

        };

        // Creating a group
        $scope.createGroup = function () {
            otFormService.create('/rest/accounts/groups/create', "Account group creation").then(load);
        };

        // Updating a group
        $scope.updateGroup = function (group) {
            otFormService.update(group.links._update, "Updating group").then(load);
        };

        // Deleting a group
        $scope.deleteGroup = function (group) {
            otAlertService.confirm({
                title: "Account group deletion",
                message: "Do you really want to delete this group?"
            }).then(function () {
                ot.call($http.delete(group.links._delete)).then(load);
            });
        };

        // Revoking a token
        $scope.revokeToken = (account) => {
            otAlertService.confirm({
                title: "Token revocation",
                message: "Do you really want to revoke this token?"
            }).then(function () {
                return ot.pageCall($http.post(account.links._revokeToken));
            }).then(load);
        };

    })

;