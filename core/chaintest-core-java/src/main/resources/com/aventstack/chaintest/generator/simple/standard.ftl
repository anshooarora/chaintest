<#list tests as test>
<div class="col-12">
  <div class="card card-custom test-result ${test.result?lower_case}">
    <div class="card-body">
      <div class="d-flex justify-content-between">
        <div>
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
          <#if test.tags?? && test.tags?has_content>
          <div class="tag-list mt-2">
            <#list test.tags as tag>
            <span class="badge rounded-pill text-bg-secondary">${tag.name}</span>
            </#list>
          </div>
          </#if>
        </div>
        <div class="ms-1 small">
          <i class="bi bi-clock"></i> <span class="ms-1">${test.startedAt?number_to_datetime}</span>
          <i class="bi bi-hourglass ms-2"></i> <span class="ms-1" data-duration="${test.durationMs}">${test.durationPretty}</span>
        </div>
      </div>
      <#if test.error??>
  <pre class="py-2">${test.error}</pre>
      </#if>
      <div>
        <#if build.isBDD()>
        <div class="mt-3">
          <#list test.children as scenario>
          <div class="mt-3">
            <div class="py-1 px-2 ${scenario.result?lower_case} bg d-flex justify-content-between">
              <div>${scenario.name}</div>
              <div><span class="badge bg-outline-light">${scenario.durationPretty}</span></div>
            </div>
            <#list scenario.children as step>
            <div class="${step.result?lower_case} ps-3 pe-2 bg">
              <div class="d-flex justify-content-between">
                <div>${step.name}</div>
                <div><span class="badge bg-outline-light">${step.durationPretty}</span></div>
              </div>
              <#if step.error??>
<pre class="py-2">${step.error}</pre>
              </#if>
            </div>
            </#list>
          </div>
          </#list>
        </div>
        </#if>
      </div>
    </div>
  </div>
</div>
</#list>