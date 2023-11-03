angular.module('ot.service.event', [

])
    .service('otEventService', function ($interpolate) {

        var variableRegex = /\$\{([:a-zA-Z_]+)\}/g;
        var self = {};

        self.renderEvent = function (event) {
            if (event.html) {
                return event.html;
            } else {
                // Legacy code
                return event.template.replace(
                    variableRegex,
                    replacementFunction(event)
                );
            }
        };

        function replacementFunction(event) {
            return function (match, expression) {
                var entity;
                if (expression.charAt(0) == ':') {
                    // We want a value
                    var valueKey = expression.substring(1);
                    var value = event.values[valueKey];
                    if (!value) {
                        return "#ERROR:" + valueKey;
                    }
                    // Rendering
                    return $interpolate('<span class="ot-event-value">{{value.value}}</span>')({
                        valueKey: valueKey,
                        value: value
                    });
                } else if (expression == 'REF') {
                    if (event.ref) {
                        entity = event.entities[event.ref];
                        if (!entity) {
                            return "#ERROR:REF:" + event.ref;
                        } else {
                            return renderEntity(event.ref, entity);
                        }
                    } else {
                        return "#ERROR:REF";
                    }
                } else if (expression.startsWith('X_')) {
                    const type = expression.substring(2);
                    entity = event.extraEntities[type];
                    if (!entity) {
                        return "#ERROR:" + expression;
                    }
                    return renderEntity(type, entity);
                } else {
                    // We want an entity reference
                    entity = event.entities[expression];
                    if (!entity) {
                        return "#ERROR:" + expression;
                    }
                    return renderEntity(expression, entity);
                }
            };
        }

        function renderEntity(expression, entity) {
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

        return self;

    })
;