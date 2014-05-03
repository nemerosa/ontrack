module.exports = function (grunt) {

    var devTarget = 'target/dev';

    grunt.initConfig({

        /**
         * Reads the `package.json` so that its data is available to the Grunt tasks.
         */
        pkg: grunt.file.readJSON('package.json'),

        /**
         * Cleaning all directories
         */
        clean: [
            devTarget
        ]

    });

    /**
     * Loading the plug-ins.
     */

    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-less');
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-ngmin');
    grunt.loadNpmTasks('grunt-html2js');

    /**
     * TODO Registering the tasks
     */

};