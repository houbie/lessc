Grails LESS CSS compiler plugin
================================

Grails plugin that pre-compiles LESS code into CSS stylesheets (see <http://lesscss.org>)

This plugin is compatible with LESS version 1.4.1. In fact, it executes the official JavaScript LESS compiler in a JVM.

In development mode, the plugin monitors your LESS files (and all their imports), and automatically compiles them when necessary.
So you only need to save and refresh your browser.

The LESS files are compiled at build time and packaged in the final WAR, hence there is no compilation in production mode.


## Why yet another LESS plugin?

* Lessc has a daemon that automatically re-compiles when a source is modified.
* Lessc caches information about imported source files so that an initial compilation to gather imports is rarely necessary.
* When there is an error in a LESS file, Lessc shows an extract and the location of the error
* Lessc supports all compilation options, which is not the case for the other plugins (at the time of writing).
* Lessc uses a compiler that is up to 5 times faster then other Java implementations.


## Installation

For the time being, you have to build the plugin from source or use it as an inline plugin (in BuildConfig.groovy):

    grails.plugin.location.lessc = '{pathTo}/lessc'

## Defining resources

    modules = {
    
        'twitter-bootstrap' {
            resource url: 'less/bootstrap.less'  //simplest form
        }
    
        mainLess {
            dependsOn 'twitter-bootstrap'
            defaultBundle 'main'
            //defaultBundle does not work for less resources; you need to specify the bundle explicitly for each less resource
            resource url: 'less/simple/main-color-green.less', bundle: 'main'
            resource url: 'less/simple/main-background-color.less', bundle: 'main'
            resource url: 'css/main-margin.css'
        }
    
    
        //type and rel are optional, but if specified, they should be set to resp. 'css' and 'stylesheet/less'
        withAttrs {
            resource url: 'less/simple/args-border.less', attrs: [type: 'css', rel: 'stylesheet/less'], bundle: 'main'
        }
    }

## Configuration

Configuration typically resides in Config.groovy. All entries are optional.

    grails.resources.debug = false //client side less compilation when set to true
    grails {
        resources {
            mappers {
                lessc {
                    includes = ['less/bootstrap.less', 'less/simple/*.less'] //default: ['**/*.less']
                    excludes = ['**/variables.less'] //default: []
                    includePlugins = ['twitter-bootstrap'] //list of plugins to search for less files (using includes/excludes), default: []
    
                    //standard lessc compiler options,
                    options {
                        compress = true //Compress output by removing some whitespaces, default: false
                        optimizationLevel = 1 //The lower the number, the fewer nodes created in the tree.
                                              //Useful for debugging or if you need to access the individual nodes in the tree.
                                              //default: 1
     
                        strictImports = false //Force evaluation of imports, default: false
                        rootPath = '' //Set rootpath for URL rewriting in relative imports and URLs, default: ''
                        relativeUrls = true //Re-write relative URLs to the base less file, default: true
                        dumpLineNumbers = 'NONE' //Outputs filename and line numbers, possible values:
                                                 // COMMENTS: output the debug info within comments.
                                                 // MEDIA_QUERY: outputs the information within a fake media query which is compatible with the SASS format.
                                                 // ALL: does both
                                                 // default: NONE
                        minify = false //minify generated CSS with YUI cssmin, default: false
                        strictMath = false //Use strict math, default: false
                        strictUnits = false //Use strict units, default: false
                    }
                    encoding = 'utf8' //Character encoding, default: null (system)
     
                    //define custom javascript functions that can be used inside LESS code
                    //either a String, a java.util.File or a java.io.Reader
                    //default: null
                    customJavaScript = '''less.tree.functions.add = function (a, b) {
                                             return new(less.tree.Dimension)(a.value + b.value);
                                          };'''
                    daemonInterval = 300 //interval in milliseconds used to check LESS files for changes (only in development mode)
                                         //set to 0 to disable the daemon
                                         //default: 200
                    failOnError = false //exit when a compilation error is encountered, default: false
                    clientSideLess = false //when true, compile the LESS stylesheets in the browser with less-1.4.1.js, default: false
                }
            }
        }
    }

### Tip for compiling Twitter Bootstrap

When using the Twitter Bootstrap grails plugin, it is not necessary to copy all the LESS files to your projects.
Only copy the ones that you want to customize (typically variables.less). Only specify the LESS files that you actually
use to prevent unnecessary compilation of imported LESS files:

    includes = ['less/bootstrap.less']
    includePlugins = ['twitter-bootstrap']

## Compatibility

Lessc passes all the tests of the official JavaScript LESS 1.4.1 compiler, except the test for _data-uri_.
Lessc handles _data-uri_ the same way as the official LESS does when used inside a browser: the _data-uri_'s are translated
into URL's in stead of being embedded in the CSS.

## TODO

(Functional) tests