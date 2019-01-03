var APPLICATION_INFO_HEART_BEAT = 15000; // 15 seconds

angular.module('ot.service.info', [
    'ot.service.core'
])
    .service('otInfoService', function (ot, $log, $interval, $http, $rootScope, otAlertService) {
        var self = {};

        self.loadApplicationInfo = function () {
            return ot.call($http.get($rootScope.info._applicationInfo));
        };

        self.loadInfo = function () {
            ot.call($http.get('info')).then(function (info) {
                $rootScope.info = info;
            });
        };

        /**
         * Initialization of the service
         */
        self.init = function () {
            $log.debug('[info] init');
            // Loading the application information
            self.loadInfo();
        };

        /**
         * Displaying the version information
         */
        self.displayVersionInfo = function (info) {
            otAlertService.popup({
                data: info,
                template: 'app/dialog/dialog.versionInfo.tpl.html'
            });
        };

        return self;
    })
;