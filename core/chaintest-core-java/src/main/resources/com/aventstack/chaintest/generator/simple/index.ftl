<#if build.isBDD()>
  <#assign
    title1 = 'Features', title2 = 'Scenarios', title3 = 'Steps', statLevel = 1
  >
<#elseif build.testRunner == 'testng'>
  <#if build.runStats?size gte 3>
    <#assign
      title1 = 'Suites', title2 = 'Classes', title3 = 'Methods', statLevel = 2
    >
    <#else>
      <#assign
        title1 = 'Classes', title2 = 'Methods', statLevel = 1
      >
  </#if>
<#elseif build.testRunner == 'junit' || build.testRunner == 'junit-jupiter'>
  <#assign
    title1 = 'Classes', title2 = 'Methods', statLevel = 1
  >
<#else>
    <#assign
        title1 = 'Tests', title2 = 'Methods', title3 = 'Events', statLevel = 1
    >
</#if>
<#if build.runStats?? && build.runStats?has_content && (statLevel gte build.runStats?size) && build.runStats?size gte 1>
    <#assign statLevel = build.runStats?size - 1>
</#if>

<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="UTF-8">
  <meta name='viewport' content='width=device-width, initial-scale=1.0'>
  <title>${config['documentTitle']}</title>
  <#if config['offline']>
    <link id="style" href="resources/bootstrap.min.css" rel="stylesheet">
    <link id="icons" href="resources/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="resources/template.css">
  <#else>
    <link href="https://cdn.jsdelivr.net/gh/anshooarora/chaintest/cdn/simple/chaintest-pkg.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:ital,opsz,wght@0,14..32,100..900;1,14..32,100..900&display=swap" rel="stylesheet">
  </#if>
  <#if config['css']??><style>${config['css']}</style></#if>
</head>

<body data-bs-theme="${config['darkTheme']?then('dark', '')}">
  <nav class="navbar border-bottom">
    <div class="container d-flex justify-content-between">
      <div>
        <a class="navbar-brand" href="#">ChainTest</a>
        <span class="ms-3 fs-6">${config['projectName']}</span>
      </div>
      <div>
        <span class="badge badge-outline text-lg"><i class="bi bi-hourglass me-1"></i> ${build.durationPretty}</span>
        <span class="badge badge-outline text-lg"><i class="bi bi-clock me-1"></i> Started ${build.startedAt?number_to_datetime?string(config['datetimeFormat'])}</span>
        <span class="badge badge-outline text-lg"><i class="bi bi-clock me-1"></i> Ended ${build.endedAt?number_to_datetime?string(config['datetimeFormat'])}</span>
      </div>
      <div>
        <#if build.systemInfo?? && build.systemInfo?has_content>
          <button role="button" id="sys-info" class="btn btn-outline-primary smaller" title="View System Info">
            <i class="bi bi-pc-display-horizontal"></i></button>
        </#if>
        <button role="button" id="shortcuts" class="btn btn-outline-primary smaller" title="Shortcuts">
            <i class="bi bi-info-circle"></i></button>
      </div>
    </div>
  </nav>

  <div id="summary" class="container-fluid bg-body-tertiary">
    <!-- dashboard section -->
    <#if build.runStats?? && build.runStats?size !=0>
    <div id="dashboard" class="py-5">
      <div class="container">
        <div class="row">
          <div class="col-4">
            <div class="card card-custom" style="height: 175px">
              <div class="card-header">
                ${title1}
              </div>
              <div class="card-body d-flex justify-content-center" style="height: 90px;">
                <div class="chart-view center" style="margin-left: -1rem;">
                  <canvas id="stats1"></canvas>
                </div>
              </div>
              <div class="card-footer small">
                <span id="sp1">${build.runStats[0].passed}</span> Passed,
                <span id="sf1">${build.runStats[0].failed}</span> Failed,
                <span id="ss1">${build.runStats[0].skipped}</span> Skipped
              </div>
            </div>
          </div>
          <#if build.runStats?size gte 2>
          <div class="col-4">
            <div class="card card-custom" style="height: 175px">
              <div class="card-header">
                ${title2}
              </div>
              <div class="card-body d-flex justify-content-center" style="height: 90px;">
                <div class="chart-view center" style="margin-left: -1rem;">
                  <canvas id="stats2"></canvas>
                </div>
              </div>
              <div class="card-footer small">
                <span id="sp2">${build.runStats[1].passed}</span> Passed,
                <span id="sf2">${build.runStats[1].failed}</span> Failed,
                <span id="ss2">${build.runStats[1].skipped}</span> Skipped
              </div>
            </div>
          </div>
          </#if>
          <#if build.isBDD() || build.runStats?size gte 3>
            <div class="col-4">
              <div class="card card-custom" style="height: 175px">
                <div class="card-header">
                  ${title3}
                </div>
                <div class="card-body d-flex justify-content-center" style="height: 90px;">
                  <div class="chart-view center" style="margin-left: -1rem;">
                    <canvas id="stats3"></canvas>
                  </div>
                </div>
                <div class="card-footer small">
                  <span id="sp3">${build.runStats[2].passed}</span> Passed,
                  <span id="sf3">${build.runStats[2].failed}</span> Failed,
                  <span id="ss3">${build.runStats[2].skipped}</span> Skipped
                </div>
              </div>
            </div>
            <#else>
              <div class="col-4">
                <div class="card card-custom" style="height: 175px">
                  <div class="card-header mb-2">
                    Summary
                  </div>
                  <div class="card-body">
                    <div class="row">
                      <div class="col text-center">
                        <label class="fs-10">Started</label>
                        <p class="fw-semibold mb-0">
                          ${build.startedAt?number_to_datetime?string(config['datetimeFormat'])}
                        </p>
                      </div>
                      <div class="col border-start text-center">
                        <label class="fs-10">Ended</label>
                        <p class="fw-semibold mb-0">${build.endedAt?number_to_datetime?string(config['datetimeFormat'])}
                        </p>
                      </div>
                    </div>
                  </div>
                  <div class="card-footer">
                    <#if build.runStats?? && build.runStats?size gte 2 && build.runStats[1].total gt 0>
                      <#assign passRate=(build.runStats[1].passed/build.runStats[1].total)*100>
                        <div class="progress" role="progressbar" aria-label="Success example"
                          aria-valuenow="${passRate}" aria-valuemin="0" aria-valuemax="100">
                          <div class="progress-bar bg-success" style="width: ${passRate}%">${passRate}%</div>
                        </div>
                    <#elseif build.runStats?? && build.runStats?size gte 1 && build.runStats[0].total gt 0>
                      <#assign passRate=(build.runStats[0].passed/build.runStats[0].total)*100>
                        <div class="progress" role="progressbar" aria-label="Success example"
                          aria-valuenow="${passRate}" aria-valuemin="0" aria-valuemax="100">
                          <div class="progress-bar bg-success" style="width: ${passRate}%">${passRate}%</div>
                        </div>
                    </#if>
                  </div>
                </div>
              </div>
          </#if>
        </div>
      </div>
    </div>
    </#if>
    <!-- /dashboard section -->

    <!-- tag section -->
    <#if build.tags?has_content>
      <div id="tags" class="container pb-5">
        <div class="card card-custom">
          <div class="card-header">
            Tags
          </div>
          <div class="card-body">
            <table id="tag-summary" class="table">
              <thead>
                <tr>
                  <th scope="col" style="width:65%"></th>
                  <th scope="col">Total</th>
                  <th scope="col">Passed</th>
                  <th scope="col">Failed</th>
                  <th scope="col">Time</th>
                </tr>
              </thead>
              <tbody>
                <#list build.tagStats as tag>
                  <#if tag.depth == statLevel>
                  <tr>
                    <td><a href="#" class="secondary tag">${tag.name}</a></td>
                    <td>${tag.total}</td>
                    <td>${tag.passed}</td>
                    <td>${tag.failed}</td>
                    <td>${tag.durationPretty}</td>
                  </tr>
                  </#if>
                </#list>
              </tbody>
            </table>
          </div>
        </div>
      </div>
      <div class="border-bottom"></div>
    </#if>
    <!-- /tag section -->
  </div>

  <div class="container-fluid">
    <div class="py-3 mb-5">
      <div class="container d-flex justify-content-between">
        <div class="input-group">
          <input type="text" id="q" class="form-control border" placeholder="Search..." aria-label="Search...">
          <button class="btn border btn-sm text-success status-filter" id="passed" type="button">Passed</button>
          <button class="btn border btn-sm text-warning status-filter" id="skipped" type="button">Skipped</button>
          <button class="btn border btn-sm text-danger status-filter" id="failed" type="button">Failed</button>
          <button id="clear-filters" class="btn border btn-sm"><i class="bi bi-x-lg me-1"></i>Clear all filters</button>
          <button type="button" id="summary-toggle" class="btn border btn-sm"><i class="bi bi-toggle-on me-1"></i> Toggle Summary Section</button>
        </div>
      </div>
    </div>
    <#list tests as test>
      <div class="test-container ${test.result?lower_case}">
        <div class="container">
          <div class="row">
            <div class="col-4">
              <h6 class="mb-3 testname fs-6 ${test.result?lower_case}">${test.name}</h6>
              <div class="small">
                <span class="badge badge-outline"><i class="bi bi-hourglass me-1"></i> ${test.durationPretty}</span>
                <span class="badge text-bg-info"><i class="bi bi-clock me-1"></i>
                  ${test.startedAt?number_to_datetime?string(config['datetimeFormat'])}</span>
                <span class="badge text-bg-warning"><i class="bi bi-clock me-1"></i>
                  ${test.endedAt?number_to_datetime?string(config['datetimeFormat'])}</span>
              </div>
              <#if test.tags?has_content>
                <div class="my-2">
                  <#list test.tags as tag>
                    <span class="badge rounded-pill text-bg-secondary">${tag.name}</span>
                  </#list>
                </div>
              </#if>
              <#if test.description??><code class="mt-2">${test.description}</code></#if>
            </div>
            <div class="col-8">
              <#list test.children as child>
                <div class="card mb-2 result ${child.result?lower_case}">
                  <div class="card-body">
                    <div class="d-flex justify-content-between">
                      <div>
                        <#if child.result=='PASSED'>
                          <i class="bi bi-check-circle-fill text-success"></i>
                          <#else>
                            <i class="bi bi-exclamation-octagon-fill text-danger"></i>
                        </#if>
                        <span class="fs-6 ms-2">${child.name}</span>
                      </div>
                      <div>
                        <#if child.tags?has_content>
                          <span class="tag-list">
                            <#list child.tags as tag>
                              <span class="badge rounded-pill text-bg-secondary">${tag.name}</span>
                            </#list>
                          </span>
                          <span class="mx-1">&middot;</span>
                        </#if>
                        <span class="badge text-dark bg-light">${child.durationPretty}</span>
                      </div>
                    </div>
                    <#if child.description??><code class="ms-4 small">${child.description}</code></#if>
                    <#if child.error??>
                      <pre class="mt-2">${child.error}</pre>
                    </#if>
                    <#if build.isBDD()>
                      <#include "bdd.ftl" />
                    <#else>
                      <#include "standard.ftl" />
                    </#if>
                    <#if child.logs?has_content>
                      <div class="mt-3">
                        <pre class="pb-0">
                          <#list child.logs as log>
${log}
                          </#list>
                        </pre>
                      </div>
                    </#if>
                    <#if child.embeds?has_content>
                      <div class="row mt-3">
                        <#list child.embeds as embed>
                          <div class="embed col-2 mb-1">
                            <img src="resources/${embed.name}" />
                          </div>
                        </#list>
                      </div>
                    </#if>
                  </div>
                </div>
              </#list>
            </div>
          </div>
        </div>
        <#if test?is_last>
          <div class="border-bottom py-5"></div>
          <#else>
            <div class="border-bottom my-5"></div>
        </#if>
      </div>
    </#list>
  </div>

  <#if build.runStats?? && build.runStats?size != 0>
  <script>
    const stats1Annotation = {'total': ${ build.runStats[0].total }, 'passed': ${ build.runStats[0].passed }};
    const stats1 = [
      { result: 'Passed', count: ${ build.runStats[0].passed }, bg: 'rgb(140, 197, 83)' },
      { result: 'Failed', count: ${ build.runStats[0].failed }, bg: 'rgb(233,80,113)' },
      { result: 'Skipped', count: ${ build.runStats[0].skipped }, bg: 'rgb(221, 91, 96)' }
    ];
    <#if build.runStats?size gte 2>
      const stats2Annotation = {'total': ${ build.runStats[1].total }, 'passed': ${ build.runStats[1].passed }};
      const stats2 = [
        { result: 'Passed', count: ${ build.runStats[1].passed }, bg: 'rgb(140, 197, 83)' },
        { result: 'Failed', count: ${ build.runStats[1].failed }, bg: 'rgb(233,80,113)' },
        { result: 'Skipped', count: ${ build.runStats[1].skipped }, bg: 'rgb(221, 91, 96)' }
      ];
    <#else>
      const stats2 = null;
    </#if>
    <#if build.runStats?size gte 3>
      const stats3Annotation = {'total': ${ build.runStats[2].total }, 'passed': ${ build.runStats[2].passed }};
      const stats3 = [
        { result: 'Passed', count: ${ build.runStats[2].passed }, bg: 'rgb(140, 197, 83)' },
        { result: 'Failed', count: ${ build.runStats[2].failed }, bg: 'rgb(233,80,113)' },
        { result: 'Skipped', count: ${ build.runStats[2].skipped }, bg: 'rgb(221, 91, 96)' }
      ];
    <#else>
      const stats3 = null;
    </#if>
    <#if config['js'] ??> ${ config['js'] }</#if >
  </script>
  <#else>
    const stats1 = null;
  </#if>
  <#if config['offline']>
    <script src="resources/chart.umd.js"></script>
    <script src="resources/template.js"></script>
  <#else>
    <script src="https://cdn.jsdelivr.net/gh/anshooarora/chaintest@44756c2b27c4da9d1141ae2de0fc3dda852f17a1/cdn/simple/version/chaintest-pkg.js"></script>
  </#if>

  <#if build.systemInfo?? && build.systemInfo?has_content>
    <div id="sys-info-modal" class="modal" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h6 class="modal-title">System</h6>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"
              onclick="toggleSysInfo(false)"></button>
          </div>
          <div class="modal-body">
            <table class="table">
              <tbody>
                <#list build.systemInfo as s>
                  <tr>
                    <td>${s.name}</td>
                    <td>${s.val}</td>
                  </tr>
                </#list>
              </tbody>
            </table>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal"
              onclick="toggleSysInfo(false)">Close</button>
          </div>
        </div>
      </div>
    </div>
  </#if>

  <div id="info-modal" class="modal" tabindex="-1">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h6 class="modal-title">ChainTest Simple Generator - Shortcuts</h6>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"
            onclick="toggleInfoModal(false)"></button>
        </div>
        <div class="modal-body">
          <table class="table">
            <tbody>
              <#list shortcuts?keys as k>
                <tr>
                  <td>${k}</td>
                  <td>${shortcuts[k]}</td>
                </tr>
              </#list>
            </tbody>
          </table>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal"
            onclick="toggleInfoModal(false)">Close</button>
        </div>
      </div>
    </div>
  </div>

  <div id="attachment-modal" class="modal" tabindex="-1">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h6 class="modal-title">Attachment</h6>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"
            onclick="toggleAttachmentModal(false)"></button>
        </div>
        <div class="modal-body">
          <img src="" />
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal"
            onclick="toggleAttachmentModal(false)">Close</button>
        </div>
      </div>
    </div>
  </div>

</body>

</html>