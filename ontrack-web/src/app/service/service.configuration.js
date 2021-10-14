angular.module('ot.service.configuration', [
    'ot.service.core'
])
    .service('otConfigurationService', function (ot, $q, $http) {
        var self = {};

        /**
         * Creates a test button
         */
        self.testButton = function (url) {
            return {
                title: "Test",
                action: function (data) {
                    return self.test(url, data);
                }
            };
        };

        /**
         * Testing a configuration
         */
        self.test = function (url, data) {
            var d = $q.defer();
            ot.call($http.post(url, data)).then(function (result) {
                if (result.type === 'OK') {
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
            }, (error) => {
                if (error.status === 400) {
                    d.resolve({
                        type: 'error',
                        content: error.content
                    });
                }
            });
            return d.promise;
        };

        return self;
    })
;