var gulp = require('gulp');
var less = require('gulp-less');
var minifyCss = require('gulp-minify-css');
var concat = require('gulp-concat');
var liveReload = require('gulp-livereload');
var inject = require('gulp-inject');
var jshint = require('gulp-jshint');
var uglify = require('gulp-uglify');
var series = require('stream-series');
var templateCache = require('gulp-angular-templatecache');
var ngAnnotate = require('gulp-ng-annotate');
var ngFilesort = require('gulp-angular-filesort');
var debug = require('gulp-debug');
var minimist = require('minimist');
var del = require('del');

// Arguments

var knownOptions = {
    string: 'version',
    default: {version: 'snapshot'}
};

var options = minimist(process.argv.slice(2), knownOptions);

// Paths

var web = 'src';
var webPath = './' + web;
var assetResources = webPath + '/assets/**';

var coreLessResources = webPath + '/less/*.less';
var extensionLessResources = webPath + '/extension/*/less/*.less';
var lessResources = [coreLessResources, extensionLessResources];

var coreJsResources = webPath + '/app/**/*.js';
var extensionJsResources = webPath + '/extension/**/*.js';
var jsResources = [coreJsResources, extensionJsResources];

var templateResources = webPath + '/app/**/*.html';
var extensionTemplateResources = webPath + '/extension/**/*.html';

var indexResource = webPath + '/index.html';
var vendor = './vendor';

var build = 'build/web';

var buildPath = build + '/dev';
var buildTemplates = buildPath + '/templates';
var buildAngular = buildPath + '/angular';
var buildCss = buildPath + '/css';

var outputPath = build + '/prod';
var output = './' + outputPath;
var outputCss = './' + outputPath + '/css';
var outputJs = './' + outputPath + '/js';
var outputFonts = './' + outputPath + '/fonts';
var outputAssets = './' + outputPath + '/assets';

// Vendor resources

var vendorJsResources = [
    'jquery/dist/jquery.js',
    'jquery-ui/jquery-ui.js',
    'angular/angular.js',
    'angular-ui-router/release/angular-ui-router.js',
    'angular-ui-sortable/sortable.js',
    'angular-multi-select/angular-multi-select.js',
    'angular-taglist/js/angular-taglist-directive.js',
    'angular-sanitize/angular-sanitize.js',
    'angular-bootstrap/ui-bootstrap-tpls.js',
    'moment/min/moment.min.js'
].map(function (rel) {
        return vendor + '/' + rel;
    });

var vendorCssResources = [
    'angular-multi-select/angular-multi-select.css',
    'angular-taglist/css/angular-taglist-directive.css'
].map(function (rel) {
        return vendor + '/' + rel;
    });

// Cleaning

gulp.task('clean', function () {
    return del([build]);
});

// Javascript handling

gulp.task('lint', function () {
    return gulp.src(jsResources)
        .pipe(debug({title: 'lint:'}))
        .pipe(jshint())
        .pipe(jshint.reporter('default'))
        .pipe(liveReload());
});

gulp.task('templates', function () {
    return gulp.src(templateResources)
        .pipe(debug({title: 'templates:'}))
        .pipe(templateCache({module: 'ontrack', root: 'app/'}))
        .pipe(gulp.dest(buildTemplates))
        .pipe(liveReload());
});

gulp.task('extensions:templates', function () {
    return gulp.src(extensionTemplateResources)
        .pipe(debug({title: 'extensions:templates:'}))
        .pipe(templateCache({module: 'ontrack', root: 'extension/', filename: 'extensions-templates.js'}))
        .pipe(gulp.dest(buildTemplates))
        .pipe(liveReload());
});

/**
 * Sorted and annotated Angular files
 */
gulp.task('js:angular', ['lint', 'templates', 'extensions:templates'], function () {
    return gulp.src([buildTemplates + '/*.js'].concat(jsResources))
        .pipe(debug({title: 'js:angular:input'}))
        .pipe(ngAnnotate())
        .pipe(ngFilesort())
        .pipe(concat('ci-angular.js'))
        .pipe(gulp.dest(buildAngular))
        .pipe(debug({title: 'js:angular:output'}));
});

gulp.task('js:concat', ['js:angular'], function () {
    var jsSource = vendorJsResources;
    jsSource.push(buildAngular + '/*.js');
    return gulp.src(jsSource)
        .pipe(debug({title: 'js:concat:input'}))
        .pipe(concat('ci-' + options.version + '.js'))
        .pipe(uglify())
        .pipe(gulp.dest(outputJs))
        .pipe(debug({title: 'js:concat:output'}))
        ;
});

// Translating Less into Minified CSS

gulp.task('less', function () {
    return gulp.src(lessResources)
        .pipe(debug({title: 'less:input:'}))
        .pipe(less())
        .pipe(debug({title: 'less:output:'}))
        .pipe(concat('ci-' + options.version + '.css'))
        .pipe(gulp.dest(buildCss))
        .pipe(liveReload());
});

// Concatenation of all CSS files, including the one generated from less

gulp.task('css:concat', function () {
    return series(
        gulp.src(lessResources)
            .pipe(debug({title: 'less:input:'}))
            .pipe(less())
            .pipe(debug({title: 'less:output:'})),
        gulp.src(vendorCssResources)
    )
        .pipe(debug({title: 'css:concat:input'}))
        .pipe(minifyCss())
        .pipe(concat('ci-' + options.version + '.css'))
        .pipe(debug({title: 'css:concat:output'}))
        .pipe(gulp.dest(outputCss));
});

// Fonts

gulp.task('fonts', function () {
    return gulp
        .src(vendor + '/font-awesome/fonts/*.*')
        .pipe(debug({title: 'fonts:input'}))
        .pipe(gulp.dest(outputFonts));
});

// Copy of assets

gulp.task('assets', function () {
    return gulp
        .src(assetResources)
        .pipe(debug({title: 'assets:input'}))
        .pipe(gulp.dest(outputAssets));
});

// Injection in index.html

gulp.task('index:dev', ['less', 'fonts', 'templates', 'extensions:templates'], function () {
    var cssSources = gulp.src([buildCss + '/*.css'], {read: false});
    var vendorJsSources = gulp.src(vendorJsResources, {read: false});
    var vendorCssSources = gulp.src(vendorCssResources, {read: false});
    var appSources = gulp.src([buildTemplates + '/*.js'].concat(jsResources)).pipe(ngFilesort());

    return gulp.src(indexResource)
        .pipe(debug({title: 'index:dev:input'}))
        .pipe(inject(
            series(
                cssSources,
                vendorJsSources,
                vendorCssSources,
                appSources
            ),
            {relative: false, ignorePath: [outputPath, web, buildPath], addRootSlash: false}))
        .pipe(gulp.dest(buildPath))
        .pipe(debug({title: 'index:dev:output'}))
        .pipe(liveReload());
});

gulp.task('index:prod', ['css:concat', 'assets', 'fonts', 'templates', 'extensions:templates', 'js:concat'], function () {
    var cssSources = gulp.src([outputCss + '/*.css'], {read: false});
    var jsSources = gulp.src(outputJs + '/*.js', {read: false});

    return gulp.src(indexResource)
        .pipe(debug({title: 'index:prod:input'}))
        .pipe(inject(
            series(
                cssSources,
                jsSources
            ),
            {relative: false, ignorePath: [outputPath, web, buildPath], addRootSlash: false}))
        .pipe(gulp.dest(output))
        .pipe(debug({title: 'index:prod:output'}));
});

// Default build

gulp.task('dev', ['index:dev']);

gulp.task('default', ['index:prod']);

// Watch setup

gulp.task('watch', function () {
    liveReload.listen();
    gulp.watch(lessResources, ['less']);
    gulp.watch(indexResource, ['index:dev']);
    gulp.watch(jsResources, ['index:dev']);
    gulp.watch(templateResources, ['templates']);
    gulp.watch(extensionTemplateResources, ['extensions:templates']);
});
