angular.module('ot.service.configuration', [
    'ot.service.core'
])
    .service('otConfigurationService', function (ot, $q, $http) {
        var self = {};

        /**
         * Testing a configuration
         */
        self.test = function (url, data) {
            var d = $q.defer();
            ot.call($http.post(url, data)).then(function (result) {
                if (result.type == 'OK') {
                    d.resolve({
                        type: 'success',
                        content: "Connection OK"
                    });
                } else {
                    d.resolve({
                        type: 'error',
                        content: result.message
                    });
                }
            });
            return d.promise;
        };

        return self;
    })
;