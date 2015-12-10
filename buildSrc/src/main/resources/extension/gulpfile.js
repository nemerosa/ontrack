/**
 * Gulp script used to package the web resources of an extension
 */

var gulp = require('gulp');
var concat = require('gulp-concat');
var inject = require('gulp-inject');
var jshint = require('gulp-jshint');
var uglify = require('gulp-uglify');
var templateCache = require('gulp-angular-templatecache');
var ngAnnotate = require('gulp-ng-annotate');
var debug = require('gulp-debug');
var minimist = require('minimist');

// Arguments

var knownOptions = {
    string: ['version', 'src'],
    default: {version: 'snapshot'}
};

var options = minimist(process.argv.slice(2), knownOptions);

// Sources

var src = options.src;

var templateSources = src + '/**/*.html';

// Targets

var build = '.'; // Current directory

var buildPath = build + '/dev';
var buildTemplates = buildPath + '/templates';

// NG templates

gulp.task('templates', function () {
    return gulp.src(templateSources)
        .pipe(debug({title: 'templates:input:'}))
        .pipe(templateCache({module: 'ontrack', root: ''}))
        .pipe(gulp.dest(buildTemplates))
        .pipe(debug({title: 'templates:output:'}))
        ;
});

// Default build

gulp.task('default', ['templates']);
