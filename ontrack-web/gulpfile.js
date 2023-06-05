const gulp = require('gulp');
const less = require('gulp-less');
const minifyCss = require('gulp-minify-css');
const concat = require('gulp-concat');
const liveReload = require('gulp-livereload');
const inject = require('gulp-inject');
const jshint = require('gulp-jshint');
const uglify = require('gulp-uglify');
const series = require('stream-series');
const templateCache = require('gulp-angular-templatecache');
const ngAnnotate = require('gulp-ng-annotate');
const ngFilesort = require('gulp-angular-filesort');
const debug = require('gulp-debug');
const minimist = require('minimist');
const babel = require("gulp-babel");
const sourcemaps = require('gulp-sourcemaps');

// Arguments

const knownOptions = {
    string: 'version',
    default: {version: 'snapshot'}
};

const options = minimist(process.argv.slice(2), knownOptions);

// Paths

const web = 'src';
const webPath = './' + web;
const assetResources = webPath + '/assets/**';
const extensionAssetResources = [webPath + '/extension/**/*.png'];

const graphiqlIndexResource = webPath + '/graphiql.html';

const lessResources = webPath + '/less/*.less';

const jsResources = webPath + '/app/**/*.js';

const templateResources = webPath + '/app/**/*.html';

const indexResource = webPath + '/index.html';
const vendor = './vendor';

const build = 'build/web';

const buildPath = build + '/dev';
const buildTemplates = buildPath + '/templates';
const buildConvertedJs = buildPath + '/converted';
const buildAngular = buildPath + '/angular';
const buildCss = buildPath + '/css';

const outputPath = build + '/prod';
const output = './' + outputPath;
const outputCss = './' + outputPath + '/css';
const outputJs = './' + outputPath + '/js';
const outputFonts = './' + outputPath + '/fonts';
const outputAssets = './' + outputPath + '/assets';
const outputExtensionAssets = './' + outputPath + '/extension/';

// Vendor resources

const vendorJsResources = [
    'jquery/dist/jquery.js',
    'jquery-ui/jquery-ui.js',
    'angular/angular.js',
    'angular-ui-router/release/angular-ui-router.js',
    'angular-ui-sortable/sortable.js',
    'angular-multi-select/angular-multi-select.js',
    'angular-taglist/js/angular-taglist-directive.js',
    'angular-sanitize/angular-sanitize.js',
    'angular-bootstrap/ui-bootstrap-tpls.js',
    'moment/min/moment.min.js',
    'oclazyload/dist/ocLazyLoad.min.js',
    'echarts/dist/echarts.js'
].map(function (rel) {
    return vendor + '/' + rel;
});

const vendorCssResources = [
    'angular-multi-select/angular-multi-select.css',
    'angular-taglist/css/angular-taglist-directive.css'
].map(function (rel) {
    return vendor + '/' + rel;
});

// Javascript handling

gulp.task('lint', function () {
    return gulp.src(jsResources)
        .pipe(debug({title: 'lint:'}))
        .pipe(jshint({esversion: 6}))
        .pipe(jshint.reporter('default'))
        .pipe(jshint.reporter('fail'))
        .pipe(liveReload());
});

gulp.task('templates', function () {
    return gulp.src(templateResources)
        .pipe(debug({title: 'templates:'}))
        .pipe(templateCache({module: 'ontrack', root: 'app/'}))
        .pipe(gulp.dest(buildTemplates))
        .pipe(liveReload());
});

/**
 * Converted files
 */

gulp.task('js:conversion', gulp.series(
    'lint',
    () => gulp.src(jsResources)
        .pipe(debug({title: 'js:conversion:input'}))
        .pipe(babel())
        .pipe(gulp.dest(buildConvertedJs))
        .pipe(debug({title: 'js:conversion:output'}))
));

/**
 * Sorted and annotated Angular files
 */
gulp.task('js:angular', gulp.series(
    'lint',
    'js:conversion',
    'templates',
    () => gulp.src([buildTemplates + '/*.js', buildConvertedJs + '/**/*.js'])
        .pipe(debug({title: 'js:angular:input'}))
        .pipe(ngAnnotate())
        .pipe(ngFilesort())
        .pipe(sourcemaps.init())
        .pipe(concat('ci-angular.js'))
        .pipe(sourcemaps.write("."))
        .pipe(gulp.dest(buildAngular))
        .pipe(debug({title: 'js:angular:output'}))
))

gulp.task('js:concat', gulp.series(
    'js:angular',
    () => {
        const jsSource = vendorJsResources;
        jsSource.push(buildAngular + '/*.js');
        return gulp.src(jsSource)
            .pipe(debug({title: 'js:concat:input'}))
            .pipe(concat('ci-' + options.version + '.js'))
            .pipe(uglify())
            .pipe(gulp.dest(outputJs))
            .pipe(debug({title: 'js:concat:output'}))
            ;
    }
))

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
        .src([
            vendor + '/font-awesome/fonts/*.*',
            vendor + '/bootstrap/fonts/*.*'
        ])
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

gulp.task('extensionAssets', function () {
    return gulp
        .src(extensionAssetResources)
        .pipe(debug({title: 'assets:input:extensions'}))
        .pipe(gulp.dest(outputExtensionAssets));
});

// Injection in index.html

gulp.task('index:dev', gulp.series(
    'less',
    'fonts',
    'js:conversion',
    'templates',
    () => {
        const cssSources = gulp.src([buildCss + '/*.css'], {read: false});
        const vendorJsSources = gulp.src(vendorJsResources, {read: false});
        const vendorCssSources = gulp.src(vendorCssResources, {read: false});
        const appSources = gulp.src([buildTemplates + '/*.js', buildConvertedJs + '/**/*.js']).pipe(ngFilesort());

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
    }
))

gulp.task('index:prod', gulp.series(
    'css:concat',
    'assets',
    'fonts',
    'templates',
    'js:concat',
    () => {
        const cssSources = gulp.src([outputCss + '/*.css'], {read: false});
        const jsSources = gulp.src(outputJs + '/*.js', {read: false});

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
    }
))

// Injection in graphiql.html

gulp.task('graphiql:dev', () => gulp.src(graphiqlIndexResource)
    .pipe(debug({title: 'graphiql:dev:input'}))
    .pipe(gulp.dest(buildPath))
    .pipe(debug({title: 'graphiql:dev:output'}))
    .pipe(liveReload()));

gulp.task('graphiql:prod:index', () => gulp.src(graphiqlIndexResource)
    .pipe(debug({title: 'graphiql:prod:input'}))
    .pipe(gulp.dest(output))
    .pipe(debug({title: 'graphiql:prod:output'})));

// Default build

gulp.task('dev', gulp.series('index:dev', 'graphiql:dev', 'fonts'));

gulp.task('default', gulp.series('index:prod', 'graphiql:prod:index', 'assets', 'extensionAssets', 'fonts'));

// Watch setup

gulp.task('watch', function () {
    liveReload.listen();
    gulp.watch(lessResources, ['less']);
    gulp.watch(indexResource, ['index:dev']);
    gulp.watch(jsResources, ['index:dev']);
    gulp.watch(templateResources, ['templates']);
});
