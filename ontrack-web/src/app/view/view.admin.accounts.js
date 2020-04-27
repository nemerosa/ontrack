angular.module('ot.view.admin.accounts', [
    'ui.router',
    'ot.service.core'
])
    .config(function ($stateProvider) {
        $stateProvider.state('admin-accounts', {
            url: '/admin-accounts',
            templateUrl: 'app/view/view.admin.accounts.tpl.html',
            controller: 'AdminAccountsCtrl'
        });
    })

    .controller('AdminAccountsCtrl', function ($scope, $http, ot, otFormService, otAlertService) {
        var view = ot.view();
        view.title = "Account management";

        // Loading the accounts
        function load() {
            ot.call($http.get('rest/accounts')).then(function (accounts) {
                $scope.accounts = accounts;
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
                    // API for this page
                    ot.viewApiCommand(accounts._self),
                    ot.viewCloseCommand('/home')
                ];
                // Loads the groups
                return ot.call($http.get('rest/accounts/groups'));
            }).then(function (groups) {
                $scope.groups = groups;
            });
        }

        // Initialisation
        load();

        // Creating an account
        $scope.createAccount = function () {
            otFormService.create($scope.accounts._create, "Account creation").then(load);
        };

        // Updating an account
        $scope.updateAccount = function (account) {
            otFormService.update(account._update, "Updating account").then(load);
        };

        // Deleting an account
        $scope.deleteAccount = function (account) {
            otAlertService.confirm({
                title: "Account deletion",
                message: "Do you really want to delete this account?"
            }).then(function () {
                ot.call($http.delete(account._delete)).then(load);
            });

        };

        // Creating a group
        $scope.createGroup = function () {
            otFormService.create($scope.groups._create, "Account group creation").then(load);
        };

        // Updating a group
        $scope.updateGroup = function (group) {
            otFormService.update(group._update, "Updating group").then(load);
        };

        // Deleting a group
        $scope.deleteGroup = function (group) {
            otAlertService.confirm({
                title: "Account group deletion",
                message: "Do you really want to delete this group?"
            }).then(function () {
                ot.call($http.delete(group._delete)).then(load);
            });

        };

    })

;