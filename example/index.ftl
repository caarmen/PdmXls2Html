<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <link rel="stylesheet" type="text/css" href="/common.css">
    <link rel="stylesheet" type="text/css" href="/portal.css">
    <link href="https://fonts.googleapis.com/css?family=Berkshire+Swash" rel="stylesheet">
    <title>Poesía del Momento</title>
</head>
<body style="background-image: url(/background.png)">
<!--#include virtual="/header.html"-->
<div>
    <!--#include virtual="/sidebar.html"-->
    <div class="content">
        <div class="left-border"></div>
        <div class="right-border"></div>
        <div class="content-text">
            <div class="logo logo-small">Año ${year?long?c}</div>
            <table class="portal-table">
                <tr>
                <#list webpageIds as webpageId>
                    <td class="portal-entry"><a href="/${year?long?c}/poemas${webpageId.id}.html">${webpageId.title}</a></td>
                    <#if webpageId_index % 3 == 2>
                </tr>
                <tr>
                    </#if>
                </#list>
                </tr>
            </table>
        </div>
    </div>
</div>
</body>
</html>