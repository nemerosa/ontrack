angular.module('ot.service.validationstampfilter', [
    'ot.service.core',
    'ot.dialog.validationstampfilter'
])
    .service('otValidationStampFilterService', function (ot, $modal, $http) {
        var self = {};

        self.saveSelection = function (branch, selection) {
            var key = "validationStampFilter_" + branch.id;
            if (selection) {
                localStorage.setItem(key, JSON.stringify(selection));
            } else {
                localStorage.removeItem(key);
            }
        };

        /**
         * Selection of the validation stamp filter
         */
        self.selectValidationStampFilter = function (branch, validationStampSelection) {
            // Loading the validation stamps
            return ot.call($http.get(branch._validationStamps)).then(function (validationStampsResources) {
                // Selected validation stamps
                angular.forEach (validationStampsResources.resources, function (validationStamp) {
                    validationStamp.selected = (validationStampSelection && validationStampSelection.indexOf(validationStamp.name) >= 0);
                });
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