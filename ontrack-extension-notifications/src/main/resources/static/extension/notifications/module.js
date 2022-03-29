angular.module('ontrack.extension.notifications', [
    'ot.service.core'
])
    .config(function ($stateProvider) {
        $stateProvider.state('entity-subscriptions', {
            url: '/extension/notifications/entity-subscriptions/{type}/{id}',
            templateUrl: 'extension/notifications/entity-subscriptions.tpl.html',
            controller: 'EntitySubscriptionsCtrl'
        });
    })
    .controller('EntitySubscriptionsCtrl', function ($scope, $http, ot) {
        const view = ot.view();
        view.title = "Subscriptions";
        // TODO Breadcrumbs to the entity
        view.breadcrumbs = ot.homeBreadcrumbs();
        // TODO Close command to the entity
        view.commands = [
            ot.viewCloseCommand('/home')
        ];
    })
;