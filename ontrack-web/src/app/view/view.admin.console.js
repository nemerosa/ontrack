angular.module('ot.view.admin.console', [
        'ui.router',
        'ot.service.core',
        'ot.service.task'
    ])
    .config(function ($stateProvider) {
        $stateProvider.state('admin-console', {
            url: '/admin-console',
            templateUrl: 'app/view/view.admin.console.tpl.html',
            controller: 'AdminConsoleCtrl'
        });
    })
    .controller('AdminConsoleCtrl', function ($scope, $http, ot, otAlertService, otTaskService, otNotificationService) {
        var view = ot.view();
        view.title = "Administration console";
        view.description = "Tools for the general management of ontrack";

        // Loads the statuses
        function loadHealth() {
            ot.call($http.get('admin/status')).then(function (health) {
                $scope.health = health;
            });
        }

        // Loads the extensions
        function loadExtensions() {
            ot.call($http.get('extensions')).then(function (extensions) {
                $scope.extensions = extensions;
            });
        }

        // Selected category & types
        //noinspection UnnecessaryLocalVariableJS
        var defaultJobCategory = {id: '', name: "Any category", types: []};
        $scope.defaultJobType = {id: '', name: "Any type"};
        $scope.selectedJobCategory = defaultJobCategory;
        $scope.selectedJobType = $scope.defaultJobType;

        // Loads the jobs
        function loadJobs() {
            ot.call($http.get('admin/jobs')).then(function (jobs) {
                $scope.jobs = jobs;
                view.commands = [
                    ot.viewApiCommand(jobs._self),
                    ot.viewCloseCommand('/home')
                ];
                // Computes the categories & types
                var jobCategories = [defaultJobCategory];
                $scope.jobs.resources.forEach(function (job) {
                    var categoryId = job.key.type.category.key;
                    var categoryName = job.key.type.category.name;
                    var typeId = job.key.type.key;
                    var typeName = job.key.type.name;
                    // Existing category
                    var category = jobCategories.find(function (cat) {
                        return cat.id == categoryId;
                    });
                    if (!category) {
                        category = {
                            id: categoryId,
                            name: categoryName,
                            types: []
                        };
                        jobCategories.push(category);
                    }
                    // Existing type
                    var type = category.types.find(function (type) {
                        return type.id == typeId;
                    });
                    if (!type) {
                        type = {
                            id: typeId,
                            name: typeName
                        };
                        category.types.push(type);
                    }
                });
                $scope.jobCategories = jobCategories;
            });
        }

        // Loads the logs
        function loadLogs() {
            ot.call($http.get('admin/logs', {
                params: {
                    offset: 0,
                    count: 20
                }
            })).then(function (logs) {
                $scope.logs = logs;
            });
        }

        // Initialisation
        loadJobs();
        loadLogs();
        loadHealth();
        loadExtensions();

        var interval = 10 * 1000; // 10 seconds
        otTaskService.register('Admin Console Load Jobs', loadJobs, interval);
        otTaskService.register('Admin Console Load Logs', loadLogs, interval);

        // Showing the details of a log entry
        $scope.showLogDetails = function (log) {
            otAlertService.popup({
                data: log,
                template: 'app/dialog/dialog.applicationLogEntry.tpl.html'
            });
        };

        // Duration formatting
        $scope.jobDuration = function (ms) {
            return moment.duration(ms, 'ms').humanize();
        };

        // Launching a job
        $scope.launchJob = function (job) {
            ot.pageCall($http.post(job._launch)).then(function (ack) {
                // Notification
                if (ack.success) {
                    otNotificationService.success("Job was launched.");
                } else {
                    otNotificationService.warning("Job is already running or is disabled and could not be launched.");
                }
                // Reloads the jobs in any case
                loadJobs();
            });
        };

        // Pausing a job
        $scope.pauseJob = function (job) {
            ot.pageCall($http.post(job._pause)).then(function (ack) {
                // Notification
                if (ack.success) {
                    otNotificationService.success("Job was paused.");
                } else {
                    otNotificationService.warning("Job could not be paused.");
                }
                // Reloads the jobs in any case
                loadJobs();
            });
        };

        // Resuming a job
        $scope.resumeJob = function (job) {
            ot.pageCall($http.post(job._resume)).then(function (ack) {
                // Notification
                if (ack.success) {
                    otNotificationService.success("Job schedule was resumed.");
                } else {
                    otNotificationService.warning("Job could not be resumed.");
                }
                // Reloads the jobs in any case
                loadJobs();
            });
        };

        // Deletes a job
        $scope.deleteJob = function (job) {
            ot.pageCall($http.delete(job._delete)).then(function (ack) {
                // Notification
                if (ack.success) {
                    otNotificationService.success("Job schedule was removed.");
                } else {
                    otNotificationService.warning("Job could not be removed.");
                }
                // Reloads the jobs in any case
                loadJobs();
            });
        };

        // Showing the error for a status message
        $scope.showError = function (config) {
            config.errorShown = true;
        };

        // Showing the details for a status message
        $scope.showDetails = function (config) {
            config.detailsShown = true;
        };

        // Job status filter
        $scope.jobStatuses = [
            {id: '', name: "Any status"},
            {id: 'IDLE', name: "Idle jobs"},
            {id: 'RUNNING', name: "Running jobs"},
            {id: 'PAUSED', name: "Paused jobs"},
            {id: 'DISABLED', name: "Disabled jobs"},
            {id: 'INVALID', name: "Invalid jobs"}
        ];

        $scope.selectedJobStatus = $scope.jobStatuses[0];

        $scope.setJobStatus = function (value) {
            $scope.selectedJobStatus = value;
        };

        function jobStatusFilter(job) {
            return $scope.selectedJobStatus.id === '' || $scope.selectedJobStatus.id === job.state;
        }

        // Job error filter
        $scope.jobErrors = [
            {check: false, name: "Any error status"},
            {check: true, name: "Jobs in error"}
        ];
        $scope.selectedJobError = $scope.jobErrors[0];
        $scope.setJobError = function (value) {
            $scope.selectedJobError = value;
        };

        function jobErrorFilter(job) {
            return !$scope.selectedJobError.check || job.lastErrorCount > 0;
        }

        // Job category filter
        $scope.setJobCategory = function (value) {
            $scope.selectedJobCategory = value;
            $scope.selectedJobType = $scope.defaultJobType;
        };

        function jobCategoryFilter(job) {
            return $scope.selectedJobCategory.id === '' || $scope.selectedJobCategory.id === job.key.type.category.key;
        }

        // Job type filter
        $scope.setJobType = function (value) {
            $scope.selectedJobType = value;
        };

        function jobTypeFilter(job) {
            return $scope.selectedJobType.id === '' || $scope.selectedJobType.id === job.key.type.key;
        }

        // Job description filter
        $scope.jobDescription = {value: ''};

        function jobDescriptionFilter(job) {
            return $scope.jobDescription.value === '' || job.description.toLowerCase().indexOf($scope.jobDescription.value.toLowerCase()) >= 0;
        }

        // Job filter

        $scope.clearJobFilter = function () {
            $scope.selectedJobStatus = $scope.jobStatuses[0];
            $scope.selectedJobCategory = defaultJobCategory;
            $scope.selectedJobType = $scope.defaultJobType;
            $scope.selectedJobError = $scope.jobErrors[0];
            $scope.jobDescription.value = '';
        };

        $scope.jobFilter = function (job) {
            return jobStatusFilter(job) &&
                jobCategoryFilter(job) &&
                jobTypeFilter(job) &&
                jobErrorFilter(job) &&
                jobDescriptionFilter(job);
        };

        // Pause & resume all jobs

        $scope.pauseAllSelected = function () {
            $scope.jobs.resources.forEach(function (job) {
                if ($scope.jobFilter(job) && job._pause) {
                    $scope.pauseJob(job);
                }
            });
        };

        $scope.resumeAllSelected = function () {
            $scope.jobs.resources.forEach(function (job) {
                if ($scope.jobFilter(job.id) && job._resume) {
                    $scope.resumeJob(job);
                }
            });
        };

    })
;