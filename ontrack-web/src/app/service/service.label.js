angular.module('ot.service.label', [])
    .service('otLabelService', function () {
        let self = {};

        self.formatLabel = (label) => {
            if (!label) {
                return "";
            } else if (label.category) {
                return `${label.category}:${label.name}`;
            } else {
                return label.name;
            }
        };

        return self;
    })
;