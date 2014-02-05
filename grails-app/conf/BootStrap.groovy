class BootStrap {

    def init = { servletContext ->
        def importedLess = new File('target/lessc/less/main-imported.less')
        importedLess.parentFile.mkdirs()
        if (!importedLess.exists()) {
            importedLess.createNewFile()
        }
        importedLess.text = new File('web-app/less/simple/main-imported.less').text
    }
}