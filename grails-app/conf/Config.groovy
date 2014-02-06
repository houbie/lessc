// configuration for plugin testing - will not be included in the plugin zip

log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'

    info 'org.grails.plugin.resource'
    info 'ResourcesGrailsPlugin'
    info 'grails.app.resourceMappers.org.grails.plugin.lessc'
    info 'com.github.houbie.lesscss'
}

//grails.resources.debug = true
grails {
    resources {
        mappers {
            lessc {
                includePlugins = ['twitter-bootstrap']
                includes = ['less/bootstrap.less', 'less/simple/*.less']
                excludes = ['**/variables.less']
                options {
                    compress = true
                    optimizationLevel = 1
                    strictImports = false
                    rootPath = ''
                    relativeUrls = true
                    dumpLineNumbers = 'NONE'
                    minify = false
                    strictMath = false
                    strictUnits = false
                }
                encoding = 'utf8'
                customJavaScript = '''less.tree.functions.add = function (a, b) {
                                         return new(less.tree.Dimension)(a.value + b.value);
                                      };'''
                daemonInterval = 300
                failOnError = false
//                clientSideLess = true
            }
        }
    }
}