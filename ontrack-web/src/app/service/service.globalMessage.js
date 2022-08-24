angular.module('ot.service.globalMessage', [
    'ot.service.core'
])
    .service('otGlobalMessageService', function (ot, $log, $interval, $http, $rootScope, otAlertService) {
        var self = {};

        self.loadInfo = function () {
            ot.call($http.get('rest/global-messages')).then(function (messages) {
                $rootScope.globalMessages = messages;
            });
        };

        /**
         * Initialization of the service
         */
        self.init = function () {
            $log.debug('[global-message] init');
            self.loadInfo();
        };

        return self;
    })
;