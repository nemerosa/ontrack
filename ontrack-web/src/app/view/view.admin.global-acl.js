angular.module('ot.view.admin.global-acl', [
    'ui.router',
    'ot.service.core'
])
    .config(function ($stateProvider) {
        $stateProvider.state('admin-global-acl', {
            url: '/admin-global-acl',
            templateUrl: 'app/view/view.admin.global-acl.tpl.html',
            controller: 'AdminGlobalACLCtrl'
        });
    })

    .controller('AdminGlobalACLCtrl', function ($scope, $http, ot) {
        var view = ot.view();
        view.title = "Global permissions";
        view.commands = [
            ot.viewCloseCommand('/admin-accounts')
        ];

        // Loading the global permissions
        function load() {
            ot.pageCall($http.get("accounts/permissions/globals")).then(function (globalPermissions) {
                $scope.globalPermissions = globalPermissions;
                return ot.pageCall($http.get(globalPermissions._globalRoles));
            }).then(function (globalRoles) {
                $scope.globalRoles = globalRoles;
            });
        }

        // Loading the page
        load();

    })

;