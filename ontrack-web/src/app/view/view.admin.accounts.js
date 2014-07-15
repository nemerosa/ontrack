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
        $stateProvider.state('admin-account', {
            url: '/admin-account/{accountId}',
            templateUrl: 'app/view/view.admin.account.tpl.html',
            controller: 'AdminAccountCtrl'
        });
    })

    .controller('AdminAccountsCtrl', function ($scope, $http, ot, otFormService) {
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
    })

    .controller('AdminAccountCtrl', function ($stateParams, $scope, $http, ot) {

        var accountId = $stateParams.accountId;
        var view = ot.view();

    })

;