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
            ot.call($http.get('accounts')).then(function (accounts) {
                $scope.accounts = accounts;
                return ot.call($http.get('accounts/groups'));
            }).then(function (groups) {
                $scope.groups = groups;
                // Commands
                view.commands = [
                    {
                        id: 'createAccount',
                        name: "Create account",
                        cls: 'ot-command-new',
                        action: $scope.createAccount
                    },
                    {
                        id: 'createGroup',
                        name: "Create group",
                        cls: 'ot-command-new',
                        action: $scope.createGroup
                    },
                    ot.viewCloseCommand('/home')
                ];
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

    })

;