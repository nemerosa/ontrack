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
            $log.debug('[task] Registering "' + name + '" with interval ' + interval);
            // Task object
            var task = {
                name: name,
                run: function () {
                    if (!task.running) {
                        try {
                            task.running = true;
                            $log.debug('[task] Launching "' + name + "'");
                            taskFn();
                        } finally {
                            task.running = false;
                        }
                    }
                }
            };
            // Puts in the map
            self.tasks[name] = task;
            // Launches the task and saves the promise
            task.promise = $interval(task.run, interval);
        };

        /**
         * Stopping one task
         */
        self.stop = function (name) {
            var task = self.tasks[name];
            if (task) {
                if (task.promise) {
                    $log.debug('[task] Stopping ' + name);
                    $interval.cancel(task.promise);
                }
                delete self.tasks[name];
            }
        };

        /**
         * Stops all the tasks.
         */
        self.stopAll = function () {
            angular.forEach(self.tasks, function (task) {
                if (task.promise) {
                    $log.debug('[task] Stopping ' + task.name);
                    $interval.cancel(task.promise);
                }
            });
            self.tasks = {};
        };

        // OK
        return self;
    })
;