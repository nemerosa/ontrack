angular.module('ot.service.event', [

])
    .service('otEventService', function ($interpolate) {

        var variableRegex = /\$\{([:a-zA-Z_]+)\}/g;
        var self = {};

        self.renderEvent = function (event) {

            return event.template.replace(
                variableRegex,
                replacementFunction(event)
            );
        };


        function replacementFunction(event) {
            return function (match, expression) {
                if (expression.charAt(0) == ':') {
                    // We want a value
                    var valueKey = expression.substring(1);
                } else {
                    // We want an entity reference
                    var entity = event.entities[expression];
                    if (!entity) {
                        return replacementEntityError(event, expression);
                    }
                    // Link definition
                    var link = {
                        cls: ''
                    };
                    if (expression == 'PROJECT') {
                        link.uri = "#/project/" + entity.id;
                        link.name = entity.name;
                    }
                    // Link rendering
                    return $interpolate('<a href="{{uri}}" class="{{cls}}">{{name}}</a>')(link);
                }
            };
        }

        return self;

    })
;