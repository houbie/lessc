package org.grails.plugin.lessc

import com.github.houbie.lesscss.builder.CompilationListener
import com.github.houbie.lesscss.builder.CompilationTask
import com.github.houbie.lesscss.builder.CompilationUnit
import com.github.houbie.lesscss.engine.LessCompilationEngineFactory
import com.github.houbie.lesscss.resourcereader.FileSystemResourceReader
import com.github.houbie.lesscss.resourcereader.ResourceReader

class LesscCompiler implements CompilationListener {
    private LesscConfig lesscConfig
    private LesscFileResolver fileResolver
    private AntBuilder ant
    private CompilationTask compilationTask
    private Map fileNameToPath = [:]

    LesscCompiler(LesscConfig lesscConfig, LesscFileResolver fileResolver, AntBuilder ant) {
        this.lesscConfig = lesscConfig
        this.fileResolver = fileResolver
        this.ant = ant
        createCompilationTask()
    }

    private void createCompilationTask() {
        def engine = LessCompilationEngineFactory.create(lesscConfig.engine)
        compilationTask = new CompilationTask(engine, (Reader) lesscConfig.customJavaScript, fileResolver.cacheDir)
        compilationTask.compilationListener = this
    }

    void compile() {
        createDirs()
        def options = lesscConfig.compilerOptions
        for (file in lessFiles) {
            def destination = getDestination(file)
            destination.parentFile.mkdirs()
            compilationTask.compilationUnits << new CompilationUnit(file.name, destination, options, getResourceReader(file))
            fileNameToPath[file.name] = getWebAppRelativePath(file)
        }
        compilationTask.execute()
    }


    void startDaemon() {
        if (lesscConfig.daemonInterval) {
            if (fileResolver.modifiedLessFilesFile().exists()) {
                fileResolver.modifiedLessFilesFile().delete()
            }
            compilationTask.startDaemon(lesscConfig.daemonInterval)
        }
    }

    void createDirs() {
        ant.mkdir(dir: fileResolver.workDir)
        ant.mkdir(dir: fileResolver.cssDir)
        ant.mkdir(dir: fileResolver.cacheDir)
    }

    def getLessFiles() {
        ant.fileScanner {
            for (dir in searchPaths) {
                fileset(dir: dir) {
                    for (incl in lesscConfig.includes) {
                        include(name: incl)
                    }
                    for (excl in lesscConfig.excludes) {
                        exclude(name: excl)
                    }
                }
            }
        }
    }

    List getSearchPaths() {
        [fileResolver.webAppDir] + lesscConfig.includePluginsWebAppPaths
    }

    File getDestination(File source) {
        new File(fileResolver.cssDir, getWebAppRelativePath(source) + '.css')
    }

    ResourceReader getResourceReader(File source) {
        def webAppRelativeDir = getWebAppRelativePath(source.parentFile)
        File[] searchDirs = searchPaths.collect { new File(it, webAppRelativeDir) }
        return new FileSystemResourceReader(lesscConfig.encoding, searchDirs)
    }

    String getWebAppRelativePath(File file) {
        def canonicalPath = file.canonicalPath
        for (searchPath in searchPaths*.canonicalPath) {
            if (canonicalPath.startsWith(searchPath)) {
                return canonicalPath - searchPath
            }
        }
        return file.name
    }

    @Override
    void notifySuccessfulCompilation(Collection<CompilationUnit> compilationUnits) {
        def modifiedLessFilesFile = fileResolver.modifiedLessFilesFile()
        if (!modifiedLessFilesFile.exists()) {
            modifiedLessFilesFile.createNewFile()
        }

        modifiedLessFilesFile.withWriterAppend { writer ->
            for (unit in compilationUnits) {
                writer << fileNameToPath[unit.sourceLocation] << '\n'
            }
        }
    }
}
