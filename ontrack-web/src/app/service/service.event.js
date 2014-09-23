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
                    var value = event.values[valueKey];
                    if (!value) {
                        return replacementValueError(event, valueKey);
                    }
                    // Rendering
                    return $interpolate('<span class="ot-event-value">{{value.value}}</span>')({
                        valueKey: valueKey,
                        value: value
                    });
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
                    else if (expression == 'BRANCH') {
                        link.uri = "#/branch/" + entity.id;
                        link.name = entity.name;
                    }
                    else if (expression == 'BUILD') {
                        link.uri = "#/build/" + entity.id;
                        link.name = entity.name;
                    }
                    else if (expression == 'PROMOTION_LEVEL') {
                        link.uri = "#/promotionLevel/" + entity.id;
                        link.name = entity.name;
                    }
                    else if (expression == 'VALIDATION_STAMP') {
                        link.uri = "#/validationStamp/" + entity.id;
                        link.name = entity.name;
                    }
                    else if (expression == 'VALIDATION_RUN') {
                        link.uri = "#/validationRun/" + entity.id;
                        link.name = '#' + entity.runOrder;
                    }
                    // Link rendering
                    return $interpolate('<a href="{{uri}}" class="{{cls}}">{{name}}</a>')(link);
                }
            };
        }

        function replacementEntityError (event, expression) {
            return "#ERROR";
        }

        function replacementValueError (event, key) {
            return "#ERROR";
        }

        return self;

    })
;