angular.module('ot.service.form', [
    'ot.dialog.form'
])
/**
 * Form service
 */
    .service('otFormService', function ($q, $http, $modal) {
        var self = {};

        /**
         * Gets a form description and displays it
         */
        self.display = function (formConfig) {
            var d = $q.defer();

            // Loading the form
            $http.get(formConfig.uri).success(function (form) {
                $modal.open({
                    templateUrl: 'app/dialog/dialog.form.tpl.html',
                    controller: 'otDialogForm',
                    resolve: {
                        config: function () {
                            return {
                                formConfig: formConfig,
                                form: form
                            };
                        }
                    }
                });
                // TODO Modal promise
            });

            return d.promise;
        };

        return self;
    })
;