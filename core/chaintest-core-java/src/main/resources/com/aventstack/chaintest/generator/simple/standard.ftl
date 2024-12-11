<#macro node_template node>
<div class="card card-custom test-result ${node.result?lower_case} my-2">
  <div class="card-body">
    <div class="d-flex justify-content-between">
      <div>
        <#if node.tags?? && node.tags?has_content>
          <div class="tag-list mb-2">
            <#list node.tags as tag>
              <span class="badge rounded-pill text-bg-secondary">${tag.name}</span>
            </#list>
          </div>
        </#if>
        <#if node.result=='PASSED'>
          <i class="bi bi-check-circle-fill text-success me-1"></i>
        <#else>
          <i class="bi bi-exclamation-octagon-fill text-danger me-1"></i>
        </#if>
        ${node.name}
      </div>
      <div class="small">
        <i class="bi bi-clock"></i> <span class="ms-1">${node.startedAt?number_to_datetime?string(config['datetimeFormat'])}</span>
        <i class="bi bi-hourglass ms-2"></i> <span class="ms-1" data-duration="${node.durationMs}">${node.durationPretty}</span>
      </div>
    </div>
    <#if node.error??>
      <pre class="ms-2">${node.error}</pre>
    </#if>
  </div>
  <div class="px-3">
    <#list node.children as leaf>
      <@node_template node=leaf />
    </#list>
  </div>
</div>
</#macro>

<#list tests as test>
<div class="col-12">
  <div class="card card-custom test-result ${test.result?lower_case}">
    <div class="card-body">
      <div class="d-flex justify-content-between">
        <div class="test-name">
          <a href="#" class="secondary">
            <span class="h6">
              <#if test.result=='PASSED'>
                <i class="bi bi-check-circle-fill text-success me-1"></i>
              <#else>
                <i class="bi bi-exclamation-octagon-fill text-danger me-1"></i>
              </#if>
              ${test.name}
            </span>
          </a>
          <#if test.tags?has_content>
            <div class="tag-list mt-2">
              <#list test.tags as tag>
                <span class="badge rounded-pill text-bg-secondary">${tag.name}</span>
              </#list>
            </div>
          </#if>
        </div>
        <div class="ms-1 small">
          <i class="bi bi-clock"></i> <span class="ms-1">${test.startedAt?number_to_datetime?string(config['datetimeFormat'])}</span>
          <i class="bi bi-hourglass ms-2"></i> <span class="ms-1" data-duration="${test.durationMs}">${test.durationPretty}</span>
        </div>
      </div>
      <#if test.error??>
        <pre class="ms-4">${test.error}</pre>
      </#if>
      <div>
        <#if test.children?has_content>
        <div class="mt-3">
          <#list test.children as node>
            <@node_template node=node />
          </#list>
        </div>
        </#if>
      </div>
    </div>
  </div>
</div>
</#list>


