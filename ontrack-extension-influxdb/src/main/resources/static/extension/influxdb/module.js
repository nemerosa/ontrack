angular.module('ontrack.extension.influxdb', [
    'ui.router',
    'ot.service.core'
])
    .config(function ($stateProvider) {
        $stateProvider.state('influxdb-status', {
            url: '/extension/influxdb/status',
            templateUrl: 'extension/influxdb/status.tpl.html',
            controller: 'InfluxDBStatusCtrl'
        });
    })
    .controller('InfluxDBStatusCtrl', function ($scope, $http, ot) {
        const view = ot.view();
        view.title = "InfluxDB status";
        view.breadcrumbs = ot.homeBreadcrumbs();
        view.commands = [
            ot.viewCloseCommand('/home')
        ];

        const loadStatus = () => {
            $scope.loadingStatus = true;
            ot.pageCall($http.get("extension/influxdb")).then(data => {
                $scope.status = data;
            }).finally(() => {
                $scope.loadingStatus = false;
            });
        };

        loadStatus();

        $scope.reset = () => {
            $scope.loadingStatus = true;
            ot.pageCall($http.post("extension/influxdb")).then(loadStatus);
        };
    })
;
