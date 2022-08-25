angular.module('ot.service.globalMessage', [
    'ot.service.core'
])
    .service('otGlobalMessageService', function (ot, $log, $interval, $http, $rootScope) {
        var self = {};

        self.loadInfo = function () {
            ot.call($http.get('rest/global-messages')).then(function (data) {
                console.log("rest/global-messages = ", data);
                $rootScope.globalMessages = data.messages;
                // $rootScope.globalMessages.length = 0;
                // $rootScope.globalMessages.push(...data.messages);
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