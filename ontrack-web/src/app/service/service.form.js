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
                }).result.then(
                    function success() {
                        d.resolve();
                    },
                    function error() {
                        d.reject();
                    }
                );
            });

            return d.promise;
        };

        /**
         * Prepares a form before being displayed
         */
        self.prepareForDisplay = function (form) {
            // Form data
            var data = {
                dates: {},
                times: {}
            };
            angular.forEach(form.fields, function (field) {
                data[field.name] = field.value;
                if (field.regex) {
                    field.pattern = new RegExp(field.regex);
                }
                // Date-time handling
                if (field.type == 'dateTime') {
                    if (field.value) {
                        var dateTime = new Date(field.value);
                        data.dates[field.name] = dateTime;
                        data.times[field.name] = dateTime;
                    }
                }
            });
            // OK
            return data;
        };

        /**
         * Prepares a form before being submitted
         */
        self.prepareForSubmit = function (form, data) {
            // Processing before submit
            angular.forEach(form.fields, function (field) {
                // Date-time handling
                if (field.type == 'dateTime') {
                    var date = data.dates[field.name];
                    var time = data.times[field.name];
                    var dateTime = date;
                    dateTime.setHours(time.getHours());
                    dateTime.setMinutes(time.getMinutes());
                    dateTime.setSeconds(0);
                    dateTime.setMilliseconds(0);
                    data[field.name] = dateTime;
                }
            });
            // Cleaning of pseudo fields
            delete data.dates;
            delete data.times;
            // OK
            return data;
        };

        return self;
    })
;