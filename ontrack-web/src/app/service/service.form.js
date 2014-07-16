angular.module('ot.service.form', [
    'ot.dialog.form',
    'ot.service.core'
])
/**
 * Form service
 */
    .service('otFormService', function ($q, $http, $modal, ot) {
        var self = {};

        /**
         * Getting the form content from its configuration
         */
        self.getForm = function (formConfig) {
            if (formConfig.form) {
                var d = $q.defer();
                d.resolve(formConfig.form);
                return d.promise;
            } else if (formConfig.uri) {
                return ot.call($http.get(formConfig.uri));
            } else {
                throw "Neither `uri` or `form` is set for the form config.";
            }
        };

        /**
         * Gets a form description and displays it
         */
        self.display = function (formConfig) {
            var d = $q.defer();

            self.getForm(formConfig).then(function (form) {
                // Post form treatment
                if (formConfig.postForm) {
                    form = formConfig.postForm(form);
                }
                // Dialog
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
                // Date handling
                if (field.type == 'date') {
                    if (field.value) {
                        data.dates[field.name] = new Date(field.value);
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
                // Date handling
                if (field.type == 'date') {
                    data[field.name] = data.dates[field.name];
                }
            });
            // Cleaning of pseudo fields
            delete data.dates;
            delete data.times;
            // OK
            return data;
        };

        /**
         * Updates the value for a field
         */
        self.updateFieldValue = function (form, fieldName, value) {
            angular.forEach(form.fields, function (field) {
                if (field.name == fieldName) {
                    field.value = value;
                }
            });
            return form;
        };

        /**
         * Gets the value for a field in a form
         */
        self.getFieldValue = function (form, fieldName) {
            for (var i = 0; i < form.fields.length; i++) {
                var field = form.fields[i];
                if (field.name == fieldName) {
                    return field.value;
                }
            }
            throw "Could not find any field with name " + fieldName;
        };

        return self;
    })
;