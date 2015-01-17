<!DOCTYPE html public "-//W3C//DTD HTML 4.0//EN">
<html>
 <head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <link rel="stylesheet" type="text/css" href="common.css">
  <link rel="stylesheet" type="text/css" href="webpage${webpage.pageNumber}.css">
  <title>Selección de los lectores N°${webpage.pageNumber}</title>
 </head>
 <body>
  <bgsound src="musica${webpage.pageNumber}.mid" loop="infinite"/>
  <div class="container">
   <div class="left-border"></div>
   <div class="right-border"></div>
   <div class="logo"><a href="/"><img src="logo.png" alt="Francisco Álvarez Hidalgo"></a></div>
   <div class="slogan">Poemas de amor, de soledad, de esperanza
de
   </div>
   <div class="author">Francisco Álvarez Hidalgo</div>
   <div class="title">Selección de los lectores</div>
   <div class="subtitle">Edición N°${webpage.pageNumber} - ${webpage.date}</div>
   <div class="painting">
    <img src="painting${webpage.pageNumber}.jpg" alt="${webpage.paintingCaption}">
   </div>
   <div class="caption">
     ${webpage.paintingCaption}
   </div>
   <div id="index" class="index">
    <h1>Índice</h1>
    <div class="index-heading"><a href="#breverias">Breverías</a></div>
    <div class="index-heading">Sonetos:</div>
    <#list webpage.sonnets as poem>
     <a href="#${poem.id}">${poem.title}</a>
    </#list>
    <div class="index-heading">Otros poemas:</div>
    <#list webpage.others as poem>
     <a href="#${poem.id}">${poem.title}</a>
    </#list>
   </div>
   <div class="poem-divider"></div>
   <div class="poems">
    <h1 id="breverias">Breverías</h1>
     <div class="poem breveria">
    <#list webpage.breverias as poem>
      <div class="poem-left-border"></div>
      <div class="poem-right-border"></div>
      <div id="${poem.id}" class="poem-title">${poem.poemNumber}</div>
      <div class="poem-content">${poem.content}</div>
      <div class="poem-date">${poem.locationDate}</div>
      <#if poem_has_next>
      <br>
      <#else>
      <div class="link-to-index"><a href="#index">Índice</a></div>
      </#if>
    </#list>
     </div>
    <h1>Sonetos</h1>
    <#list webpage.sonnets as poem>
     <div class="poem soneto">
      <div class="poem-left-border"></div>
      <div class="poem-right-border"></div>
      <div id="${poem.id}" class="poem-title">${poem.poemNumber} - ${poem.title}</div>
      <div class="poem-content">${poem.content}</div>
      <div class="poem-date">${poem.locationDate}</div>
      <div class="link-to-index"><a href="#index">Índice</a></div>
     </div>
     <#if poem_has_next>
     <div class="poem-divider"></div>
     </#if>
    </#list>
    <h1>Otros</h1>
    <#list webpage.others as poem>
     <#if poem.content??>
      <div class="poem otros">
       <div class="poem-left-border"></div>
       <div class="poem-right-border"></div>
       <div id="${poem.id}" class="poem-title">${poem.title}</div>
       <div class="poem-content">${poem.content}</div>
       <div class="poem-date">${poem.locationDate}</div>
       <div class="link-to-index"><a href="#index">Índice</a></div>
      </div>
      <#if poem_has_next>
      <div class="poem-divider"></div>
      </#if>
     </#if>
    </#list>
   </div>
   <div class="nav">
    <div class="nav-prev"><a href="webpage${webpage.pageNumber - 1}.html">&larr;&nbsp;Anterior</a></div>
    <div class="nav-next"><a href="webpage${webpage.pageNumber + 1}.html">Siguiente&nbsp;&rarr;</a></div>
   </div>
   <div class="footer">
    <!--#set var="formTextColor" value="#BB9"-->
    <!--#set var="formBGColor" value="#000000"-->
    <!--#include virtual="/enlaces/cajita-utf8.html"-->
   </div>
   <div class="fineprint">
   Música: ${webpage.songTitle}. Fuente: <a href="${webpage.songLink}">Mutopia</a>.
   </div>
  </div>
 </body>
</html>
