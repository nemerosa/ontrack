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

    .controller('AdminAccountsCtrl', function ($scope, $http, ot) {
        var view = ot.view();
        view.title = "Account management";
        view.commands = [
            ot.viewCloseCommand('/home')
        ];

        // Loading the accounts
        function loadAccounts() {
            ot.call($http.get('accounts')).then(function (accounts) {
                $scope.accounts = accounts;
            });
        }

        // Initialisation
        loadAccounts();
    })

;