angular.module('ot.view.admin.console', [
    'ui.router',
    'ot.service.core'
])
    .config(function ($stateProvider) {
        $stateProvider.state('admin-console', {
            url: '/admin-console',
            templateUrl: 'app/view/view.admin.console.tpl.html',
            controller: 'AdminConsoleCtrl'
        });
    })
    .controller('AdminConsoleCtrl', function ($scope, $http, $interval,  ot) {
        var view = ot.view();
        view.title = "Administration console";
        view.description = "Tools for the general management of ontrack";
        view.commands = [
            ot.viewCloseCommand('/home')
        ];

        // Loads the jobs
        function loadJobs() {
            ot.call($http.get('admin/jobs')).then(function (jobs) {
                $scope.jobs = jobs;
            });
        }

        // Initialisation
        loadJobs();

        $interval(loadJobs, 5000);

    })
;