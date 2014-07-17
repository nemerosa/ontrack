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
        function loadAccounts() {
            ot.call($http.get('accounts')).then(function (accounts) {
                $scope.accounts = accounts;
                // Commands
                view.commands = [
                    {
                        id: 'createAccount',
                        name: "Create account",
                        cls: 'ot-command-new',
                        action: $scope.createAccount
                    },
                    ot.viewCloseCommand('/home')
                ];
            });
        }

        // Initialisation
        loadAccounts();

        // Creating an account
        $scope.createAccount = function () {
            otFormService.create($scope.accounts._create, "Account creation").then(loadAccounts);
        };

        // Updating an account
        $scope.updateAccount = function (account) {
            otFormService.update(account._update, "Updating account").then(loadAccounts);
        };

        // Deleting an account
        $scope.deleteAccount = function (account) {
            otAlertService.confirm({
                title: "Account deletion",
                message: "Do you really want to delete this account?"
            }).then(function () {
                ot.call($http.delete(account._delete)).then(loadAccounts);
            });

        };
    })

;