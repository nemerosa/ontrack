angular.module('ot.service.task', [
])
    .service('otTaskService', function ($log, $interval) {
        var self = {
            tasks: {}
        };

        /**
         * Registers a task
         * @param name Name of the task
         * @param taskFn Function to run as a task
         * @param interval Interval (in milliseconds) between each run
         */
        self.register = function (name, taskFn, interval) {
            // Task object
            var task = {
                name: name,
                run: function () {
                    $log.debug('[task] ' + name);
                    taskFn();
                }
            };
            // Puts in the map
            self.tasks[name] = task;
            // Launches the task and saves the promise
            task.promise = $interval(task.run, interval);
        };

        // OK
        return self;
    })
;