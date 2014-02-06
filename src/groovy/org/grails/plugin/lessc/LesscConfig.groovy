package org.grails.plugin.lessc

import com.github.houbie.lesscss.Options
import com.github.houbie.lesscss.engine.LessCompilationEngineFactory

class LesscConfig {
    static DEFAULT_INCLUDES = ['**/*.less']

    private config
    private pluginSettings
    private baseDir

    LesscConfig(config, pluginSettings, baseDir) {
        this.config = config
        this.pluginSettings = pluginSettings
        this.baseDir = baseDir
    }

    def getIncludes() {
        config.includes ?: DEFAULT_INCLUDES
    }

    def getExcludes() {
        config.excludes ?: []
    }

    def getIncludePluginsWebAppPaths() {
        config.includePlugins.collect { getPluginWebAppDir(it) }
    }

    private getPluginWebAppDir(plugin) {
        assert pluginSettings.getPluginInfoForName(plugin): "Error in lessc.includePlugins: plugin '$plugin' could not be resolved"
        new File(pluginSettings.getPluginDirForName(plugin).file, 'web-app')
    }

    String getEngine() {
        config.engine ?: LessCompilationEngineFactory.RHINO
    }

    Reader getCustomJavaScript() {
        def customJs = config.customJavaScript
        if (customJs) {
            switch (customJs) {
                case Reader: return customJs
                case File: return customJs.newReader()
                default: return new StringReader(customJs.toString())
            }
        }
        return null
    }

    Options getCompilerOptions() {
        return new Options(config.options)
    }

    String getEncoding() {
        config.encoding ?: null
    }

    int getDaemonInterval() {
        config.daemonInterval ? config.daemonInterval as Integer : 200
    }
}
