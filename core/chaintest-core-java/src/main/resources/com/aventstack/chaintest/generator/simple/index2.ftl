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
      <link
        href="https://fonts.googleapis.com/css2?family=Inter:ital,opsz,wght@0,14..32,100..900;1,14..32,100..900&display=swap"
        rel="stylesheet">
  </#if>
  <#if config['css']??>
    <style>
      ${config['css']}
    </style>
  </#if>
</head>

<body>

  <nav class="navbar border-bottom">
    <div class="container d-flex justify-content-between">
      <a class="navbar-brand" href="#">ChainTest</a>
      <div>
        <span
          class="badge badge-outline text-lg">${build.endedAt?number_to_datetime?string(config['datetimeFormat'])}</span>
        <#if build.systemInfo?? && build.systemInfo?has_content>
          <span id="sys-info" class="badge badge-outline text-lg"><i class="bi bi-pc-display-horizontal"></i></span>
        </#if>
      </div>
    </div>
  </nav>

  <div class="container-fluid bg-body-tertiary">
    <!-- dashboard section -->
    <div id="dashboard" class="pt-5">
      <div class="container">
        <div class="row">
          <div class="col-4">
            <div class="card card-custom" style="height: 175px">
              <div class="card-header">
                ${build.isBDD()?then('Features', 'Classes')}
              </div>
              <div class="card-body d-flex justify-content-center" style="height: 90px;">
                <div class="chart-view center" style="margin-left: -1rem;">
                  <canvas id="stats1"></canvas>
                </div>
              </div>
              <div class="card-footer small">
                <#if build.runStats?? && build.runStats?size !=0>
                  ${build.runStats[0].passed} Passed,
                  ${build.runStats[0].failed} Failed,
                  ${build.runStats[0].skipped} Skipped
                </#if>
              </div>
            </div>
          </div>
          <div class="col-4">
            <div class="card card-custom" style="height: 175px">
              <div class="card-header">
                ${build.isBDD()?then('Scenarios', 'Methods')}
              </div>
              <div class="card-body d-flex justify-content-center" style="height: 90px;">
                <div class="chart-view center" style="margin-left: -1rem;">
                  <canvas id="stats2"></canvas>
                </div>
              </div>
              <div class="card-footer small">
                <#if build.runStats?? && build.runStats?size !=0>
                  ${build.runStats[1].passed} Passed,
                  ${build.runStats[1].failed} Failed,
                  ${build.runStats[1].skipped} Skipped
                </#if>
              </div>
            </div>
          </div>
          <#if build.isBDD() || build.runStats?size==3>
            <div class="col-4">
              <div class="card card-custom" style="height: 175px">
                <div class="card-header">
                  Steps
                </div>
                <div class="card-body d-flex justify-content-center" style="height: 90px;">
                  <div class="chart-view center" style="margin-left: -1rem;">
                    <canvas id="stats3"></canvas>
                  </div>
                </div>
                <div class="card-footer small">
                  <#if build.runStats?? && build.runStats?size==3>
                    ${build.runStats[2].passed} Passed,
                    ${build.runStats[2].failed} Failed,
                    ${build.runStats[2].skipped} Skipped
                  </#if>
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
                    <#if build.runStats?? && build.runStats?size !=0 && build.runStats[0].total !=0>
                      <#assign passRate=(build.runStats[1].passed/build.runStats[1].total)*100>
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
    <!-- /dashboard section -->

    <!-- tag section -->
    <#if build.tags?has_content>
      <div id="tags" class="container my-5">
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
                  <tr>
                    <td class="tag"><a href="#" class="secondary">${tag.name}</a></td>
                    <td>${tag.total}</td>
                    <td>${tag.passed}</td>
                    <td>${tag.failed}</td>
                    <td>${tag.durationPretty}</td>
                  </tr>
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
    <div class="border-bottom py-3 mb-5">
      <div class="container">
        <div id="status-filter" aria-label="Filter tests with status">
          <button type="button" id="passed" class="btn btn-outline-success btn-sm">Passed</button>
          <button type="button" id="skipped" class="btn btn-outline-warning btn-sm">Skipped</button>
          <button type="button" id="failed" class="btn btn-outline-danger btn-sm">Failed</button>
          <button id="clear-filters" class="btn btn-outline-secondary btn-sm">
            <i class="bi bi-x-lg me-1"></i>Clear all filters
            </span>
        </div>
      </div>
    </div>
    <#list tests as test>
      <div class="test-container">
        <div class="container">
          <div class="row">
            <div class="col-4">
              <h6 class="mb-3 testname ${test.result?lower_case}">${test.name}</h6>
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
            </div>
            <div class="col-8">
              <#list test.children as child>
                <div class="card mb-1 result ${child.result?lower_case}">
                  <div class="card-body">
                    <div class="d-flex justify-content-between">
                      <div>
                        <#if child.result=='PASSED'>
                          <i class="bi bi-check-circle-fill text-success"></i>
                          <#else>
                            <i class="bi bi-exclamation-octagon-fill text-danger"></i>
                        </#if>
                        <span class="ms-2">${child.name}</span>
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
                    <#if child.error??>
                      <pre class="mt-2">${child.error}</pre>
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

  <#if config['offline']>
    <script src="resources/chart.umd.js"></script>
    <#else>
      <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.1/chart.umd.js"></script>
  </#if>
  <script>
    <#if build.runStats?? && build.runStats ? size != 0 >
    const stats1 = [
      { result: 'Passed', count: ${ build.runStats[0].passed }, bg: 'rgb(140, 197, 83)' },
    { result: 'Failed', count: ${ build.runStats[0].failed }, bg: 'rgb(233,80,113)' },
    { result: 'Skipped', count: ${ build.runStats[0].skipped }, bg: 'rgb(221, 91, 96)' }
    ];
    const stats2 = [
      { result: 'Passed', count: ${ build.runStats[1].passed }, bg: 'rgb(140, 197, 83)' },
    { result: 'Failed', count: ${ build.runStats[1].failed }, bg: 'rgb(233,80,113)' },
    { result: 'Skipped', count: ${ build.runStats[1].skipped }, bg: 'rgb(221, 91, 96)' }
    ];
    <#if build.runStats?size == 3 >
      const stats3 = [
      { result: 'Passed', count: ${ build.runStats[2].passed }, bg: 'rgb(140, 197, 83)' },
    { result: 'Failed', count: ${ build.runStats[2].failed }, bg: 'rgb(233,80,113)' },
    { result: 'Skipped', count: ${ build.runStats[2].skipped }, bg: 'rgb(221, 91, 96)' }
      ];
    </#if >
      <#if config['js'] ??> ${ config['js'] }</#if >
  </script>
  </#if>
  <script src="resources/template.js"></script>

  <#if build.systemInfo?? && build.systemInfo?has_content>
    <div id="sys-info-modal" class="modal" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">SystemInfo</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"
              onclick="toggleSysInfo(false)"></button>
          </div>
          <div class="modal-body">class
            <table class="table">
              <tbody>
                <#list build.systemInfo as s>
                  <tr>
                    <td>${s.name}</td>
                    <td>${s.value}</td>
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
</body>

</html>