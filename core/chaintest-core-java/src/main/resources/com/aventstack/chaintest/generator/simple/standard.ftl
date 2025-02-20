<#if child.children?has_content>
  <div class="mt-3 border-top">
    <#list child.children as leaf>
    <div class="leaf ${leaf.result?lower_case} pb-1">
      <div class="d-flex justify-content-between mt-3">
        <div>
          <#if leaf.result=='PASSED'>
            <i class="bi bi-check-circle-fill text-success"></i>
          <#elseif leaf.result=='SKIPPED'>
            <i class="bi bi-exclamation-octagon-fill text-info"></i>
          <#else>
            <i class="bi bi-exclamation-octagon-fill text-danger"></i>
          </#if>
          <span class="fs-6 ms-2">${leaf.name}</span>
        </div>
        <div>
          <#if leaf.tags?has_content>
            <span class="tag-list">
              <#list leaf.tags as tag>
                <span class="badge rounded-pill text-bg-secondary">${tag.name}</span>
              </#list>
            </span>
            <span class="mx-1">&middot;</span>
          </#if>
          <span class="badge text-dark bg-light">${leaf.durationPretty}</span>
        </div>
      </div>
      <#if leaf.description??><p class="desc">${leaf.description}</p></#if>
      <#if leaf.error??>
        <pre class="py-2 mt-2 mb-4">${leaf.error}</pre>
      </#if>
      <#if leaf.logs?has_content>
        <div class="mt-3">
          <pre class="pb-0">
          <#list leaf.logs as log>
${log.message}
          </#list>
          </pre>
        </div>
      </#if>
      <#if leaf.embeds?has_content>
        <div class="row mt-3">
          <#list leaf.embeds as embed>
            <div class="embed col-2 mb-1">
              <img src="resources/${embed.name}" />
            </div>
          </#list>
        </div>
      </#if>
    </div>
    </#list>
  </div>
</#if>

