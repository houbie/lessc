package org.grails.plugin.lessc

class LesscFileResolver {
    static final String WORK_DIR = 'lessc'
    static final String CCS_DIR = 'css'
    static final String CACHE_DIR = 'cache'

    private baseDir
    private targetDir

    LesscFileResolver(baseDir, targetDir) {
        this.baseDir = baseDir
        this.targetDir = targetDir
    }

    File getWebAppDir() {
        new File("$baseDir/web-app")
    }

    File getWorkDir() {
        new File("$targetDir/$WORK_DIR")
    }

    File getCssDir() {
        new File(workDir, CCS_DIR)
    }

    File getCacheDir() {
        new File(workDir, CACHE_DIR)
    }

    File modifiedLessFilesFile() {
        new File(cacheDir, 'modifiedLessFiles.txt')
    }
}
