angular.module('ot.service.event', [

])
    .service('otEventService', function () {

        var self = {};

        self.renderEvent = function (event) {
            return event.template;
        };

        return self;

    })
;