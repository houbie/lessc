package org.grails.plugin.lessc

import grails.util.BuildSettingsHolder
import grails.util.Environment
import org.grails.plugin.resource.ResourceMeta
import org.grails.plugin.resource.mapper.MapperPhase
import org.springframework.core.io.UrlResource

class LesscResourceMapper {

    static phase = MapperPhase.GENERATION
    static defaultIncludes = ['**/*.less']

    static final String CLIENT_SIDE_LESS_JS_MODULE = '__@less.js@__'

    def grailsApplication
    def grailsResourceProcessor
    def resourceMetaMap = [:]

    @Lazy
    LesscFileResolver lesscFileResolver = new LesscFileResolver(BuildSettingsHolder.settings.baseDir, BuildSettingsHolder.settings.projectTargetDir)

    private Thread watchThread

    def map(ResourceMeta resourceMeta, config) {
        log.debug "mapping resource $resourceMeta.id"

        watchResourceIfDevMode(resourceMeta)

        def clientSideLess = grailsApplication.config.grails.resources.debug || config.clientSideLess

        if (clientSideLess) {
            resourceMeta.module.addModuleDependency(CLIENT_SIDE_LESS_JS_MODULE)
            return resourceMeta
        }

        def cssFile = findPreCompiledCssFile(resourceMeta)
        if (cssFile) {
            return updateResource(resourceMeta, cssFile)
        }
    }

    private File findPreCompiledCssFile(ResourceMeta resourceMeta) {
        def preCompiledResource = resourceMeta.originalResource.createRelative(resourceMeta.originalResource.filename + '.css')
        if (preCompiledResource.exists() && preCompiledResource.readable) {
            return newCssFile(resourceMeta, preCompiledResource.inputStream)
        }

        //does this resource belong to a plugin?
        preCompiledResource = new UrlResource(new URL(removePluginPath(preCompiledResource.URL.toString())))
        if (preCompiledResource.exists() && preCompiledResource.readable) {
            return newCssFile(resourceMeta, preCompiledResource.inputStream)
        }

        //try build target directory
        def preCompiledFile = new File(lesscFileResolver.cssDir, resourceMeta.workDirRelativePath + '.css')
        if (preCompiledFile.exists()) {
            //we have to make a copy because the resources plugin changes the last modified of the file
            return newCssFile(resourceMeta, preCompiledFile.newInputStream())
        }

        return null
    }

    private File newCssFile(ResourceMeta resourceMeta, InputStream inputStream) {
        File cssFile = grailsResourceProcessor.makeFileForURI(resourceMeta.actualUrl + '.css')
        cssFile << inputStream
        return cssFile
    }

    private String removePluginPath(String url) {
        int pluginsIndex = url.lastIndexOf('plugins')
        if (pluginsIndex >= 0) {
            String firstPart = url.substring(0, pluginsIndex)
            String lastPart = url.substring(pluginsIndex).replaceFirst('plugins/[^/]+/', "")
            return firstPart + lastPart
        }
        return url
    }

    private ResourceMeta updateResource(ResourceMeta resourceMeta, File cssFile) {
        resourceMeta.processedFile = cssFile
        resourceMeta.contentType = 'text/css'
        resourceMeta.tagAttributes.rel = 'stylesheet'
        resourceMeta.sourceUrlExtension = 'css'
        resourceMeta.updateActualUrlFromProcessedFile()
        return resourceMeta
    }

    private void watchResourceIfDevMode(ResourceMeta resourceMeta) {
        if (Environment.isDevelopmentMode()) {
            def daemonInterval = grailsApplication.config.grails.resources.mappers.lessc.daemonInterval
            if (daemonInterval) {
                resourceMetaMap[resourceMeta.sourceUrl] = resourceMeta
                startWatchThread(daemonInterval)
            }
        }
    }

    private void startWatchThread(interval) {
        if (watchThread == null) {
            log.info('starting Lessc watchThread')
            watchThread = Thread.startDaemon('LesscResourceMapperWatcher') {
                while (true) {
                    try {
                        reload(readModifiedLessFiles())
                        sleep(interval)
                    } catch (e) {
                        log.error('Error in watchThread', e)
                    }
                }
            }
        }
    }

    private List readModifiedLessFiles() {
        def file = lesscFileResolver.modifiedLessFilesFile()
        if (file.exists() && file.canRead()) {
            def text = file.text
            file.text = ''
            return text.readLines()
        }
        return []
    }

    private void reload(modifiedLessFiles) {
        def reloadNeeded = false
        for (file in modifiedLessFiles) {
            reloadNeeded |= markDirty(file)
        }
        if (reloadNeeded) {
            log.info('triggering reload for modified Less files')
            grailsResourceProcessor.reloadChangedFiles()
        }
    }

    private def markDirty(uri) {
        log.debug("makeDirty $uri")
        def resourceMeta = resourceMetaMap[uri]
        if (resourceMeta) {
            log.debug("updating originalLastMod for $resourceMeta")
            resourceMeta.originalLastMod = System.currentTimeMillis()
            return true
        }
        return false
    }
}