<#if child.children?has_content>
  <div class="mt-2">
      <#list child.children as leaf>
        <div class="mt-1 pb-1">
          <#if leaf.result=='PASSED'>
            <i class="bi bi-check-circle-fill text-success"></i>
            <#elseif leaf.result=='FAILED'>
              <i class="bi bi-exclamation-octagon-fill text-danger"></i>
            <#else>
              <i class="bi bi-exclamation-octagon-fill text-info"></i>
          </#if>
          <span class="ms-2">${leaf.name}</span>
          <#if leaf.error??>
            <pre class="py-2 mt-2">${leaf.error}</pre>
          </#if>
        </div>
        <#if leaf.logs?has_content>
        <div>
          <pre class="pb-0">
          <#list leaf.logs as log>
${log.message}
          </#list>
          </pre>
        </div>
      </#if>
      <#if leaf.embeds?has_content>
        <div class="row mt-1">
          <#list leaf.embeds as embed>
            <div class="embed col-2 mb-1">
              <img src="resources/${embed.name}" />
            </div>
          </#list>
        </div>
      </#if>
      </#list>
  </div>
</#if>
