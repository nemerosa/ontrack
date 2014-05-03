module.exports = function (grunt) {

    var devTarget = 'target/dev';
    var tmp = 'target/tmp';

    var appJs = '**/*.js';

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
        ],

        /**
         * `ngmin` annotates the sources before minifying. That is, it allows us
         * to code without the array syntax in AngularJS.
         */
        ngmin: {
            prod: {
                files: [
                    {
                        src: [ '**/*.js' ],
                        cwd: 'src',
                        dest: tmp,
                        expand: true
                    }
                ]
            }
        }

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
     * Registering the tasks
     */

    /**
     * The default task is to prod.
     */
    grunt.registerTask('default', [ 'prod' ]);

    /**
     * The `dev` task gets your app ready to run for development and testing.
     */
    grunt.registerTask('dev', [
        'clean'
        // TODO 'jshint',
        // TODO 'less:dev',
        // TODO 'copy:dev_app_assets',
        // TODO 'copy:dev_vendor_assets',
        // TODO 'copy:dev_vendor_fonts',
        // TODO 'copy:dev_appjs',
        // TODO 'copy:dev_apptpl',
        // TODO 'copy:dev_vendorjs',
        // TODO 'copy:dev_vendorcss',
        // TODO 'html2js:dev',
        // TODO 'index:dev'
    ]);

    /**
     * The `prod` task gets your app ready for deployment by concatenating and
     * minifying your code.
     */
    grunt.registerTask('prod', [
        'clean',
        // TODO 'less:prod',
        // TODO 'copy:prod_assets',
        // TODO 'html2js:prod',
        'ngmin:prod'
        // TODO 'concat:prod_js',
        // TODO 'concat:prod_css',
        // TODO 'uglify:prod',
        // TODO 'index:prod',
        // TODO 'copy:prod_vendor_fonts'
    ]);

};