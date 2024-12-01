<#list tests as feature>
<div class="col-12">
  <div class="card card-custom test-result ${feature.result?lower_case}">
    <div class="card-body">
      <div class="d-flex justify-content-between">
        <div>
          <#if feature.tags??>
          <div class="tag-list mb-2">
            <#list feature.tags as tag>
            <span class="badge rounded-pill text-bg-secondary">${tag.name}</span>
            </#list>
          </div>
          </#if>
          <a href="#" class="secondary feature">
            <span class="h6">
              <#if feature.result=='PASSED'>
              <i class="bi bi-check-circle-fill text-success me-1"></i>
              <#else>
              <i class="bi bi-exclamation-octagon-fill text-danger me-1"></i>
              </#if>
              ${feature.name}
            </span>
          </a>
        </div>
        <div class="ms-1 small">
          <i class="bi bi-clock"></i> <span class="ms-1">${feature.startedAt?number_to_datetime?string(config['datetimeFormat'])}</span>
          <i class="bi bi-hourglass ms-2"></i> <span class="ms-1" data-duration="${feature.durationMs}">${feature.durationPretty}</span>
        </div>
      </div>
      <div>
        <#if build.isBDD()>
        <div class="mt-3">
          <#list feature.children as scenario>
          <div class="mt-3">
            <div class="py-1 pe-2 scenario ${scenario.result?lower_case} d-flex justify-content-between">
              <div>
                <#if scenario.result=='PASSED'>
                  <i class="bi bi-check-circle-fill text-success me-1"></i>
                  <#else>
                  <i class="bi bi-exclamation-octagon-fill text-danger me-1"></i>
                </#if>
                ${scenario.name}
              </div>
              <div><span class="badge bg-outline-light">${scenario.durationPretty}</span></div>
            </div>
            <#list scenario.children as step>
            <div class="step ${step.result?lower_case} ps-3 pe-2 bg">
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