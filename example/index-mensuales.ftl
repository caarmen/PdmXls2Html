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
<div class="root">
    <!--#include virtual="/sidebar.html"-->
    <div class="content">
        <div class="left-border"></div>
        <div class="right-border"></div>
        <div class="top-border"></div>
        <div class="content-text">
            <div class="logo logo-small">${collection.title}</div>
            <table class="portal-table">
                <tr>
                <#list pages as page>
                    <td class="portal-entry"><a href="/mensuales/${collection.id}/${page.id}.html">${page.title}</a></td>
                    <#if page_index % 3 == 2 && page_has_next>
                </tr>
                <tr>
                    </#if>
                </#list>
                </tr>
            </table>
        </div>
        <div class="bottom-border"></div>
    </div>
</div>
</body>
</html>