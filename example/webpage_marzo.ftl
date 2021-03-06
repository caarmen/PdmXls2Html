<!DOCTYPE html>
<html lang="es">
 <head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <meta name="viewport" content="width=610px">
  <link rel="stylesheet" type="text/css" href="sonetos_de_marzo.css">
  <title>${webpage.pageNumber}</title>
 </head>
 <body style="background-image: url(/background.png)">
  <div class="container">
   <div class="left-border-wide"></div>
   <div class="right-border-wide"></div>
   <div class="logo logo-main" data-text="Poesía del Momento"><a href="/">Poesía del Momento</a></div>
   <div class="slogan">Poemas de amor, de soledad, de esperanza
de
   </div>
   <div class="author">Francisco Álvarez Hidalgo</div>
   <div class="title light-text">Sonetos de Marzo</div>
   <div class="poems">
    <#list webpage.sonnets as poem>
     <div class="poem soneto">
      <div class="card-top"></div>
      <div class="card-left"></div>
      <div class="poem-left-border"></div>
      <div class="poem-right-border"></div>
      <div id="${poem.id}" class="poem-title">${poem.poemNumber} - ${poem.title}</div>
       <#if poem.preContent?has_content>
       <div class="poem-pre-content">${poem.preContent}</div>
       </#if>
      <div class="poem-content">${poem.content}</div>
      <div class="poem-date">${poem.locationDate}</div>
      <div class="card-right"></div>
      <div class="card-bottom"></div>
     </div>
    </#list>
   </div>
   <div class="nav">
    <#if webpage.prevPageNumber?has_content>
    <a href="${webpage.prevPageNumber}"><span class="nav-prev">&larr;&nbsp;Anterior</span></a>
    </#if>
    <div class="nav-index dropdown">Índice
        <div class="dropdown-content">
            <!--#include virtual="index.html"-->
        </div>
    </div>
    <#if webpage.nextPageNumber?has_content>
    <a href="${webpage.nextPageNumber}"><span class="nav-next">Siguiente&nbsp;&rarr;</span></a>
    </#if>
   </div>
   <!--#include virtual="/copyright-footer.html"-->
  </div>
 </body>
</html>
