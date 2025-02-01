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
      </#list>
  </div>
</#if>
