<!DOCTYPE html public "-//W3C//DTD HTML 4.0//EN">
<html>
 <head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <meta name="viewport" content="width=610px">
  <link rel="stylesheet" type="text/css" href="common.css">
  <title>${webpage.pageNumber}</title>
 </head>
 <body style="background-image: url(background${webpage.pageNumber}.png)">
  <div class="container">
   <div class="left-border"></div>
   <div class="right-border"></div>
   <div class="logo"><a href="/"><img src="logo.png" alt="Francisco Álvarez Hidalgo"></a></div>
   <div class="slogan">Poemas de amor, de soledad, de esperanza
de
   </div>
   <div class="author">Francisco Álvarez Hidalgo</div>
   <div class="poems">
     <div class="poem breveria">
    <#list webpage.breverias as poem>
      <div class="poem-left-border"></div>
      <div class="poem-right-border"></div>
      <div id="${poem.id}" class="poem-title">Brevería ${poem.poemNumber}</div>
      <div class="poem-content">${poem.content}</div>
      <div class="poem-date">${poem.locationDate}</div>
    </#list>
     </div>
   </div>
   <div class="nav">
    <#if webpage.prevPageNumber??>
    <div class="nav-prev"><a href="webpage${webpage.prevPageNumber}.html">&larr;&nbsp;Anterior</a></div>
    </#if>
    <#if webpage.nextPageNumber??>
    <div class="nav-next"><a href="webpage${webpage.nextPageNumber}.html">Siguiente&nbsp;&rarr;</a></div>
    </#if>
   </div>
   <div class="footer">
    <!--#set var="formTextColor" value="#BB9"-->
    <!--#set var="formBGColor" value="#000000"-->
    <!--#include virtual="/enlaces/cajita-utf8.html"-->
   </div>
   <div class="fineprint">
   Poemas &copy; Francisco Álvarez Hidalgo, Familia Álvarez, 1997-2014. <a href="/enlacesindice.html#derechos">Todos derechos reservados.</a>
   </div>
  </div>
 </body>
</html>
