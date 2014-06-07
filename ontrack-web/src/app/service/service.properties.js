angular.module('ot.service.properties', [
    'ot.service.core',
    'ot.service.form'
])
    .service('otPropertiesService', function ($q, $http, ot, otFormService) {
        var self = {};

        self.addProperty = function (property) {
            return otFormService.display({
                uri: property._update,
                title: property.typeDescriptor.name,
                submit: function (data) {
                    return ot.call($http.put(property._update, data));
                }
            });
        };

        self.deleteProperty = function (property) {
            return ot.call($http.delete(property._update));
        };

        return self;
    })
;