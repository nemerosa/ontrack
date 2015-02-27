angular.module('ot.service.validationstampfilter', [
    'ot.service.core',
    'ot.dialog.validationstampfilter'
])
    .service('otValidationStampFilterService', function (ot, $modal, $http) {
        var self = {};

        /**
         * Selection of the validation stamp filter
         */
        self.selectValidationStampFilter = function (branch) {
            // Loading the validation stamps
            return ot.call($http.get(branch._validationStamps)).then(function (validationStampsResources) {
                // Displays the dialog
                return $modal.open({
                    templateUrl: 'app/dialog/dialog.validationstampfilter.tpl.html',
                    controller: 'otDialogValidationStampFilter',
                    resolve: {
                        config: function () {
                            return {
                                validationStamps: validationStampsResources.resources,
                                branch: branch,
                                submit: function (selection) {
                                    return selection;
                                }
                            };
                        }
                    }
                }).result;
            });
        };

        return self;
    })
;