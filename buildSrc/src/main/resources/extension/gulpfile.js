/**
 * Gulp script used to package the web resources of an extension
 */
const gulp = require('gulp');
const concat = require('gulp-concat');
const jshint = require('gulp-jshint');
const uglify = require('gulp-uglify');
const ngAnnotate = require('gulp-ng-annotate');
const ngFilesort = require('gulp-angular-filesort');
const debug = require('gulp-debug');
const babel = require("gulp-babel");

// Arguments

const options = {
    version: process.env.VERSION,
    src: process.env.SRC,
    extension: process.env.EXTENSION,
    target: process.env.TARGET,
}

// Sources

const src = options.src;

const jsSources = src + '/**/*.js';

// Targets

const build = options.target;

const buildConverted = build + '/converted';
const buildPath = build + '/web';
const buildDist = buildPath + '/dist';

// JS Linting

gulp.task('js:lint', function () {
    return gulp.src(jsSources)
        .pipe(debug({title: 'lint:'}))
        .pipe(jshint({esversion: 6}))
        .pipe(jshint.reporter('default'))
        .pipe(jshint.reporter('fail'))
        ;
});

/**
 * Converted files
 */

gulp.task('js:conversion', gulp.series(
    'js:lint',
    () => gulp.src(jsSources)
        .pipe(debug({title: 'js:conversion:input'}))
        .pipe(babel({
            "presets": [
                "env"
            ],
            "plugins": [
                "transform-es2015-template-literals"
            ]
        }))
        .pipe(gulp.dest(buildConverted))
        .pipe(debug({title: 'js:conversion:output'}))
))

// Sorted and annotated Angular files

gulp.task('js', gulp.series(
    'js:lint',
    'js:conversion',
    function () {
        return gulp.src([buildConverted + '/**/*.js'])
            .pipe(debug({title: 'js:input'}))
            .pipe(ngAnnotate())
            .pipe(ngFilesort())
            .pipe(uglify())
            .pipe(concat('module.js'))
            .pipe(gulp.dest(buildDist))
            .pipe(debug({title: 'js:output'}))
    }
))

// Default build

gulp.task('default', gulp.series('js'));
