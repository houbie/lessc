import org.grails.plugin.resource.BundleResourceMapper
import org.grails.plugin.resource.CSSBundleResourceMeta
import org.grails.plugin.resource.CSSRewriterResourceMapper
import org.grails.plugin.resource.CSSPreprocessorResourceMapper
import org.grails.plugin.resource.ResourceProcessor
import org.grails.plugin.resource.ResourceTagLib

class LesscGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/conf/ApplicationResources.groovy",
            "grails-app/views/**",
            "web-app/css/**",
            "web-app/less/**"
    ]

    def title = "Lessc Plugin"
    def author = "Ivo Houbrechts"
    def authorEmail = "ivo@houbrechts-it.be"
    def description = '''\
Compilation of LESS CSS style sheets, compatible with the standard lessc compiler (see http://lesscss.org/)
'''

    def documentation = "http://grails.org/plugin/lessc"

    def license = "APACHE"

    def organization = [name: "Ivo Houbrechts", url: "https://github.com/houbie"]

    // Location of the plugin's issue tracker. TODO
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
    def scm = [url: "https://github.com/houbie/lesscss"]

    def doWithSpring = {
        CSSPreprocessorResourceMapper.defaultIncludes.add('**/*.less')
        CSSRewriterResourceMapper.defaultIncludes.add('**/*.less')

        BundleResourceMapper.MIMETYPE_TO_RESOURCE_META_CLASS.put('stylesheet/less', CSSBundleResourceMeta)
        ResourceProcessor.DEFAULT_MODULE_SETTINGS['less'] = [disposition: 'head']
        ResourceTagLib.SUPPORTED_TYPES['less'] = [
                type: "text/css",
                rel: 'stylesheet/less',
                media: 'screen, projection'
        ]

    }
}
