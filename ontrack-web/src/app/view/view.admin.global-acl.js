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

    })

;