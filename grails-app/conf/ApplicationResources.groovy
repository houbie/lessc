modules = {
    'twitter-bootstrap' {
        resource url: 'less/bootstrap.less'
    }

    mainLess {
        dependsOn 'twitter-bootstrap'
        defaultBundle 'main'
        resource url: 'less/simple/main-color-green.less', bundle: 'main'
        resource url: 'less/simple/main-background-color.less', bundle: 'main'
        resource url: 'css/main-margin.css'
    }

    mainCss {
        resource url: 'css/main-padding.css', bundle: 'main'
    }


    withAttrs {
        resource url: 'less/simple/args-border.less', attrs: [type: 'css', rel: 'stylesheet/less'], bundle: 'main'
    }

    imported {
        resource url: 'less/simple/main-imported.less'
    }
}