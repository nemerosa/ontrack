angular.module('ot.service.form', [
    'ot.dialog.form',
    'ot.service.core'
])
/**
 * Form service
 */
    .service('otFormService', function ($filter, $q, $http, $modal, ot) {
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
         * @param formConfig.postForm (optional) function used to configure the form before it is actually displayed
         * @param formConfig.form (optional) form definition
         * @param formConfig.uri (optional) URI to get the form from (either `uri` or `form` must be set)
         * @param formConfig.title Title for the dialog
         * @param formConfig.submit Function to call with the raw form data. See {@link #submitDialog}
         * @param formConfig.size 'sm' (default) or 'lg'
         * @param formConfig.buttons Additional buttons. List of {"title", action}.
         */
        self.display = function (formConfig) {
            const d = $q.defer();

            self.getForm(formConfig).then(function (form) {
                // Post form treatment
                if (formConfig.postForm) {
                    form = formConfig.postForm(form);
                }
                // Size
                var size = 'sm';
                if (formConfig.size) {
                    size = formConfig.size;
                }
                // Dialog
                $modal.open({
                    templateUrl: 'app/dialog/dialog.form.tpl.html',
                    controller: 'otDialogForm',
                    size: size,
                    resolve: {
                        config: function () {
                            return {
                                formConfig: formConfig,
                                form: form
                            };
                        }
                    }
                }).result.then(
                    function success(data) {
                        d.resolve(data);
                    },
                    function error() {
                        d.reject();
                    }
                );
            });

            return d.promise;
        };

        /**
         * Creating from a form, using POST
         */
        self.create = function (uri, title, additionalFormConfig) {
            var formConfig = {
                uri: uri,
                title: title,
                submit: function (data) {
                    return ot.call($http.post(uri, data));
                }
            };
            if (additionalFormConfig) {
                angular.extend(formConfig, additionalFormConfig);
            }
            return self.display(formConfig);
        };

        /**
         * Updating from a form, using PUT
         */
        self.update = function (uri, title, additionalFormConfig) {
            var formConfig = {
                uri: uri,
                title: title,
                submit: function (data) {
                    return ot.call($http.put(uri, data));
                }
            };
            if (additionalFormConfig) {
                angular.extend(formConfig, additionalFormConfig);
            }
            return self.display(formConfig);
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
            var date;
            // Processing before submit
            angular.forEach(form.fields, function (field) {
                // Date-time handling
                if (field.type == 'dateTime') {
                    date = data.dates[field.name];
                    var time = data.times[field.name];
                    var dateTime = date;
                    dateTime.setHours(time.getHours());
                    dateTime.setMinutes(time.getMinutes());
                    dateTime.setSeconds(0);
                    dateTime.setMilliseconds(0);
                    data[field.name] = dateTime;
                }
                // Date handling
                else if (field.type == 'date') {
                    date = data.dates[field.name];
                    if (date) {
                        data[field.name] = $filter('date')(date, 'yyyy-MM-dd');
                    } else {
                        data[field.name] = '';
                    }
                }
                // Multi-selection
                else if (field.type == 'multi-selection') {
                    data[field.name] = field.items
                        .filter(function (item) {
                            return item.selected;
                        })
                        .map(function (item) {
                            return item.id;
                        });
                }
                // Custom mapping
                else if (field.prepareForSubmit) {
                    field.prepareForSubmit(data);
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
         * Updates all values of a form.
         */
        self.updateForm = function (form, data) {
            angular.forEach(form.fields, function (field) {
                field.value = data[field.name];
            });
        };

        /**
         * Gets a field in a form
         */
        self.getField = function (form, fieldName) {
            for (var i = 0; i < form.fields.length; i++) {
                var field = form.fields[i];
                if (field.name == fieldName) {
                    return field;
                }
            }
            throw "Could not find any field with name " + fieldName;
        };

        /**
         * Gets the value for a field in a form
         */
        self.getFieldValue = function (form, fieldName) {
            return self.getField(form, fieldName).value;
        };

        /**
         * Flexible submit method to be used in dialog controllers.
         *
         * The <code>submitFn</code> is expected to return:
         * <ol>
         *     <li>a <code>true</code> value</li>, which then closes the dialog
         *     <li>a <code>String</code> message, which is displayed as an error message and the dialog is not closed
         *     <li>a <code>Object or Array</code> - the dialog is closed and this object is returned as a result
         *     <li>a <code>Promise</code> which is called and its success result is used to close the dialog
         *     whereas its error is displayed in the dialog without closing it.
         * </ol>
         *
         * Returns a promise with the result.
         */
        self.submitDialog = function (submitFn, submitData, modalInstance, messageContainer) {
            var d = $q.defer();
            var submit = submitFn(submitData);
            if (submit === true) {
                modalInstance.close('ok');
            } else if (angular.isString(submit)) {
                messageContainer.message = submit;
            } else if (submit.then && angular.isFunction(submit.then)) {
                submit.then(
                    function success(data) {
                        modalInstance.close(data);
                        d.resolve(data);
                    },
                    function error(message) {
                        messageContainer.message = message;
                        d.reject(message);
                    });
            } else if (angular.isObject(submit) || angular.isArray(submit)) {
                modalInstance.close(submit);
                d.resolve(submit);
            } else {
                throw 'Cannot handle submit result type';
            }
            return d.promise;
        };

        return self;
    })
;