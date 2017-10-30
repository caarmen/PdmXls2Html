<!DOCTYPE html public "-//W3C//DTD HTML 4.0//EN">
<html>
 <head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <meta name="viewport" content="width=610px">
  <link rel="stylesheet" type="text/css" href="/common.css">
  <link rel="stylesheet" type="text/css" href="/mensuales/common.css">
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
   <div class="title light-text">${webpage.pageNumber}</div>
   <div id="index" class="index">
    <h1>Índice</h1>
    <#if webpage.breverias?has_content>
    <div class="index-heading"><a href="#breverias">Breverías</a></div>
    </#if>

    <#if webpage.sonnets?has_content>
    <div class="index-heading">Sonetos:</div>
    <#list webpage.sonnets as poem>
     <a href="#${poem.id}">${poem.title}</a>
    </#list>
    </#if>

    <#if webpage.others?has_content>
    <div class="index-heading">Poemas:</div>
    <#list webpage.others as poem>
     <a href="#${poem.id}">${poem.title}</a>
    </#list>
    </#if>
   </div>
   <div class="poem-divider">
    <img src= "/mensuales/divider.png">
   </div>
   <div class="poems">
    <#if webpage.breverias?has_content>
    <h1 id="breverias">Breverías</h1>
     <div class="poem breveria">
    <#list webpage.breverias as poem>
      <#if (poem_index = 0)>
      <div class="card-top"></div>
      </#if>
      <div class="card-left"></div>
      <div class="poem-left-border"></div>
      <div class="poem-right-border"></div>
      <div id="${poem.id}" class="poem-title">${poem.poemNumber}</div>
       <#if poem.preContent?has_content>
       <div class="poem-pre-content">${poem.preContent}</div>
       </#if>
      <div class="poem-content">${poem.content}</div>
      <div class="card-right"></div>
      <#if poem_has_next>
      <br>
      <#else>
      <div class="link-to-index"><a href="#index">Índice</a></div>
      <div class="card-bottom"></div>
      </#if>
    </#list>
     </div>
    </#if>
    <#if webpage.sonnets?has_content>
    <h1>Sonetos</h1>
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
      <div class="link-to-index"><a href="#index">Índice</a></div>
      <div class="card-right"></div>
      <div class="card-bottom"></div>
     </div>
     <#if poem_has_next>
     <div class="poem-divider">
      <img src= "/mensuales/divider.png">
     </div>
     </#if>
    </#list>
    </#if>
    <#if webpage.others?has_content>
    <h1>Poemas</h1>
    <#list webpage.others as poem>
     <#if poem.content??>
      <div class="poem otros">
       <div class="card-top"></div>
       <div class="card-left"></div>
       <div class="poem-left-border"></div>
       <div class="poem-right-border"></div>
       <div id="${poem.id}" class="poem-title">${poem.title}</div>
       <#if poem.preContent?has_content>
       <div class="poem-pre-content">${poem.preContent}</div>
       </#if>
       <div class="poem-content">${poem.content}</div>
       <div class="poem-date">${poem.locationDate}</div>
       <div class="link-to-index"><a href="#index">Índice</a></div>
       <div class="card-right"></div>
       <div class="card-bottom"></div>
      </div>
      <#if poem_has_next>
      <div class="poem-divider">
       <img src= "/mensuales/divider.png">
      </div>
      </#if>
     </#if>
    </#list>
    </#if>
   </div>
   <div class="nav">
    <#if webpage.prevPageNumber?has_content>
    <a href="${webpage.prevPageNumber}"><span class="nav-prev">&larr;&nbsp;Anterior</span></a>
    </#if>
    <#if webpage.nextPageNumber?has_content>
    <a href="${webpage.nextPageNumber}"><span class="nav-next">Siguiente&nbsp;&rarr;</span></a>
    </#if>
   </div>
   <div class="fineprint">
   Diseño: Carmen Álvarez<br>
   Poemas &copy; Francisco Álvarez Hidalgo, Familia Álvarez, 1997-2014. <a href="/enlacesindice.html#derechos">Todos derechos reservados.</a>
   </div>
  </div>
 </body>
</html>
