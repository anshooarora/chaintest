<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="UTF-8">
  <meta name='viewport' content='width=device-width, initial-scale=1.0'>
  <title>chaintest</title>
  <link id="style" href="bootstrap.min.css" rel="stylesheet">
  <link id="icons" href="bootstrap-icons.min.css" rel="stylesheet">
  <link
    href="https://fonts.googleapis.com/css2?family=Inter:ital,opsz,wght@0,14..32,100..900;1,14..32,100..900&display=swap"
    rel="stylesheet">
  <link rel="stylesheet" href="template.css">
</head>

<body>
  <!-- page wrapper -->
  <div class="page">

    <!-- navbar -->
    <nav class="navbar navbar-expand-md">
      <div class="container-fluid">
        <a class="navbar-brand primary" href="#">ChainTest</a>
        <div class="collapse navbar-collapse" id="navbarCollapse">
          <ul class="navbar-nav me-auto mb-0">
            <li class="nav-item ms-5 me-2">
              <span class="nav-link">chaintest-core</span>
            </li>
            <li class="nav-item mx-2">
              <a class="nav-link" href="#">Tags</a>
            </li>
            <li class="nav-item mx-2">
              <a class="nav-link" href="#">Tests</a>
            </li>
          </ul>
          <form class="d-flex" role="search">
            <span class="badge bg-outline-light">2024-11-08 11:27 AM</span>
          </form>
        </div>
      </div>
    </nav>
    <!-- /navbar -->

    <!-- main-content -->
    <div class="main-content app-content">
      <div class="border-bottom"></div>

      <!-- dashboard section -->
      <div id="dashboard" class="mt-4 mb-3">
        <div class="container-fluid">
          <div class="row">
            <div class="col-6">
              <div class="card card-custom" style="height: 200px">
                <div class="card-header">
                  Tests
                </div>
                <div class="card-body d-flex justify-content-center" style="height: 120px;">
                  <div class="chart-view center" style="margin-left: -1rem;">
                    <canvas id="acquisitions"></canvas>
                  </div>
                </div>
                <div class="card-header small">
                  <#if build.runStats?? && build.runStats?size != 0>
                  ${build.runStats[0].passed} Passed,
                  ${build.runStats[0].failed} Failed,
                  ${build.runStats[0].skipped} Skipped,
                  </#if>
                </div>
              </div>
            </div>
            <div class="col-6">
              <div class="card card-custom" style="height: 200px">
                <div class="card-header mb-2">
                  Summary
                </div>
                <div class="card-body">
                  <div class="row pb-2">
                    <div class="col text-center">
                      <label class="fs-10">Started</label>
                      <p class="fw-bold">${build.startedAt?number_to_datetime}</p>
                    </div>
                    <div class="col border-start text-center">
                      <label class="fs-10">Ended</label>
                      <p class="fw-bold">${build.endedAt?number_to_datetime}</p>
                    </div>
                  </div>
                  <#if build.runStats?? && build.runStats?size != 0 && build.runStats[0].total != 0>
                  <#assign
                    passRate=(build.runStats[0].passed/build.runStats[0].total)*100
                  >
                  <div class="progress mt-4" role="progressbar" aria-label="Success example" aria-valuenow="${passRate}" aria-valuemin="0" aria-valuemax="100">
                    <div class="progress-bar bg-success" style="width: ${passRate}%">${passRate}%</div>
                  </div>
                  </#if>
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- /dashboard section -->

        <div class="container-fluid tags mt-4 mb-4">

          <!-- tag section -->
          <div class="row">
            <div class="col-12">
              <div class="card card-custom">
                <div class="card-header">
                  Tags
                </div>
                <div class="card-body">
                  <table id="tag-summary" class="table">
                    <thead>
                      <tr>
                        <th scope="col" style="width: 40%">Name</th>
                        <th scope="col">Total</th>
                        <th scope="col">Failed</th>
                        <th scope="col">Passed</th>
                        <th scope="col">Time</th>
                      </tr>
                    </thead>
                    <tbody>
                      <#list build.tagStats as tag>
                      <tr>
                        <td class="tag"><a href="#" class="secondary">${tag.name}</a></td>
                        <td>${tag.total}</td>
                        <td>${tag.passed}</td>
                        <td>${tag.skipped}</td>
                        <td>${tag.durationMs}</td>
                      </tr>
                      </#list>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- /tag section -->

        <!-- test section -->
        <div class="container-fluid tests">
          <div class="mt-3 mb-3 d-flex justify-content-between">
            <div id="status-filter" class="btn-group" role="group" aria-label="Filter tests with status">
              <button type="button" id="passed" class="btn btn-success">Passed</button>
              <button type="button" id="skipped" class="btn btn-warning">Skipped</button>
              <button type="button" id="failed" class="btn btn-danger">Failed</button>
            </div>
            <button id="clear-filters" type="button" class="btn btn-outline-light btn-sm">
              <i class="bi bi-x-lg me-1"></i>Clear all filters
            </button>
          </div>

          <div class="row">
            <#list tests as test>
            <div class="col-12">
              <div class="card card-custom test-result ${test.result?lower_case}">
                <div class="card-body">
                  <div class="ms-1 mb-2 d-flex justify-content-between">
                    <a href="#" class="secondary">
                      <span class="h6">${test.name}</span>
                    </a>
                    <div class="">
                      <div class="pb-2 ms-1 mb-1 small">
                        <i class="bi bi-clock"></i> <span class="ms-1">${test.startedAt}</span>
                        <i class="bi bi-hourglass ms-2"></i> <span class="ms-1">${test.durationMs}</span>
                        <span class="ms-2 badge badge-danger badge-md"><i class="bi bi-record-fill me-1"></i>
                          ${test.result}</span>
                      </div>
                    </div>
                  </div>
                  <#if test.tag??>
                  <div class="ms-1 mt-2 tag-list">
                    <#list test.tags as tag>
                    <span class="badge bg-outline-light">${tag.name}</span>
                    </#list>
                  </div>
                  </#if>
                </div>
              </div>
              </#list>
              <div class="card card-custom test-result passed">
                <div class="card-body">
                  <div class="ms-1 mb-2 d-flex justify-content-between">
                    <a href="#" class="secondary">
                      <span class="h6">#9 - junit-jupiter</span>
                    </a>
                    <div class="">
                      <div class="pb-2 ms-1 mb-1 small">
                        <i class="bi bi-clock"></i> <span class="ms-1">2024-10-17 4:32:22 PM</span>
                        <i class="bi bi-hourglass ms-2"></i> <span class="ms-1">Took 5m 20s</span>
                        <span class="ms-2 badge badge-success badge-md"><i class="bi bi-flag me-1"></i>Passed</span>
                      </div>
                    </div>
                  </div>
                  <div class="ms-1 mt-2 tag-list">
                    <span class="badge bg-outline-light">Smoke</span>
                    <span class="badge bg-outline-light">Feature 24.1</span>
                    <span class="badge bg-outline-light">Feature 24.2</span>
                    <span class="badge bg-outline-light">Feature 24.2</span>
                    <span class="badge bg-outline-light">Unstable</span>
                    <span class="badge bg-outline-light">@regression</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- /test section-->

      </div>
      <!-- /main-content -->

    </div>
    <!-- /page wrapper -->

    <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.1/chart.umd.js"></script>
    <script src="template.js"></script>
</body>

</html>
