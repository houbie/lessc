<!DOCTYPE html>
<html>
<head>
    <title>LESS CSS test page</title>
    <link type="text/css" rel="stylesheet" href="${resource(dir: 'less/simple', file: 'adhoc-text-decoration.less')}"/>
    <r:require module="mainLess"/>
    <r:require module="withAttrs"/>
    <r:layoutResources/>
</head>

<body>
<h1 class="simple">This should be green on a yellow background</h1>
<p class="label-warning glyphicon-cloud ">Background should be blue</p>
<r:layoutResources/>
</body>
</html>
