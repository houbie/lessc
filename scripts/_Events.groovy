eventPackagingEnd = {
    def start = System.currentTimeMillis()
    def lesscFileResolver = createInstance('org.grails.plugin.lessc.LesscFileResolver', basedir, projectTargetDir)
    def lesscConfig = createInstance('org.grails.plugin.lessc.LesscConfig', config.grails.resources.mappers.lessc, pluginSettings, basedir)
    def lesscCompiler = createInstance('org.grails.plugin.lessc.LesscCompiler', lesscConfig, lesscFileResolver, ant)
    try {
        setVariable('lesscCompiler', lesscCompiler)
        setVariable('lesscFileResolver', lesscFileResolver)
        lesscCompiler.compile()
    } catch (Throwable t) {
        grailsConsole.error "Error during less compilation ${t.class.name}: ${t.message}", t
        if (config.grails.resources.mappers.lessc.failOnError) {
            exit 1
        }
    }

    println "eventPackagingEnd in ${System.currentTimeMillis() - start}"
}

eventRunAppStart = {
    lesscCompiler.startDaemon()
}

eventCreateWarStart = { warName, stagingDir ->
    ant.copy(todir: stagingDir) {
        fileset dir: (lesscFileResolver.cssDir)
    }
}

eventCleanStart = {
    ant.delete(dir: "$projectTargetDir/lessc")
}

//this script gets compiled before the utility classes, hence we cannot import them
createInstance = { String className, ... args ->
    getClass().classLoader.loadClass(className).newInstance(args)
}