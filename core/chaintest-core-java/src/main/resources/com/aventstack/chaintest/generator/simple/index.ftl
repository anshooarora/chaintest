<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="UTF-8">
  <meta name='viewport' content='width=device-width, initial-scale=1.0'>
  <title>chaintest</title>
  <#if config[offline]>
  <link id="style" href="bootstrap.min.css" rel="stylesheet">
  <link id="icons" href="bootstrap-icons.min.css" rel="stylesheet">
  <link rel="stylesheet" href="template.css">
  <#else>
  <link href="https://fonts.googleapis.com/css2?family=Inter:ital,opsz,wght@0,14..32,100..900;1,14..32,100..900&display=swap" rel="stylesheet">
  </#if>
</head>

<body>
  <!-- page wrapper -->
  <div class="page ${build.isBDD()?then('bdd', '')}">

    <!-- navbar -->
    <nav class="navbar navbar-expand-md">
      <div class="container-fluid">
        <a class="navbar-brand primary" href="#">ChainTest</a>
        <div class="collapse navbar-collapse" id="navbarCollapse">
          <ul class="navbar-nav me-auto mb-0">
            <li class="nav-item ms-5 me-2">
              <span class="nav-link">chaintest-core</span>
            </li>
            <#if build.tags?? && build.tags?has_content>
            <li class="nav-item mx-2">
              <a class="nav-link" href="#tags">Tags</a>
            </li>
            </#if>
            <li class="nav-item mx-2">
              <a class="nav-link" href="#tests">Tests</a>
            </li>
          </ul>
          <form class="d-flex" role="search">
            <span class="badge bg-outline-light me-1">${build.startedAt?number_to_datetime}</span>
            <span class="badge bg-outline-light">${build.durationPretty}</span>
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
            <div class="col-${build.isBDD()?then('3', '6')}">
              <div class="card card-custom" style="height: 175px">
                <div class="card-header">
                  ${build.isBDD()?then('Features', 'Tests')}
                </div>
                <div class="card-body d-flex justify-content-center" style="height: 90px;">
                  <div class="chart-view center" style="margin-left: -1rem;">
                    <canvas id="stats"></canvas>
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
            <#if build.isBDD()>
            <div class="col-3">
              <div class="card card-custom" style="height: 175px">
                <div class="card-header">
                  Scenarios
                </div>
                <div class="card-body d-flex justify-content-center" style="height: 90px;">
                  <div class="chart-view center" style="margin-left: -1rem;">
                    <canvas id="stats2"></canvas>
                  </div>
                </div>
                <div class="card-header small">
                  <#if build.runStats?? && build.runStats?size != 0>
                  ${build.runStats[1].passed} Passed,
                  ${build.runStats[1].failed} Failed,
                  ${build.runStats[1].skipped} Skipped,
                  </#if>
                </div>
              </div>
            </div>
            </#if>
            <div class="col-6">
              <div class="card card-custom" style="height: 175px">
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
      </div>
      <!-- /dashboard section -->

      <div class="container-fluid tags mt-4 mb-4">

        <!-- tag section -->
        <div id="tags" class="row">
          <div class="col-12">
            <div class="card card-custom">
              <div class="card-header">
                Tags
              </div>
              <div class="card-body">
                <table id="tag-summary" class="table">
                  <thead>
                    <tr>
                      <th scope="col" style="width: 60%">Name</th>
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
                      <td>${tag.durationPretty}</td>
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
      <div id="tests" class="container-fluid tests mb-5">
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
          <#if build.isBDD()>
            <#include "bdd.ftl">
          <#else>
            <#include "standard.ftl">
          </#if>
        </div>
      </div>
      <!-- /test section-->

    </div>
    <!-- /main-content -->

  </div>
  <!-- /page wrapper -->

  <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.1/chart.umd.js"></script>
  <script src="template.js"></script>
  <#if build.runStats?? && build.runStats?size != 0>
  <script>
    (async function() {
      const data = [
        { result: 'Passed', count: ${build.runStats[0].passed}, bg: 'rgb(43,189,86)' },
        { result: 'Failed', count: ${build.runStats[0].failed}, bg: 'rgb(233,80,113)' },
        { result: 'Skipped', count: ${build.runStats[0].skipped}, bg: 'rgb(227,203,94),' }
      ];

      const donut = {
        labels: [
          'Passed',
          'Failed',
          'Skipped'
        ]
      };

      new Chart(
        document.getElementById('stats'),
        {
          type: 'doughnut',
          data: {
            labels: donut.labels,
            datasets: [
              {
                data: data.map(row => row.count),
                backgroundColor: data.map(row => row.bg)
              }
            ]
          },
          options: {
            plugins: {
              responsive: true,
              legend: {
                display: false
              }
            }
          }
        }
      );
    })();
    <#if build.isBDD()>
    (async function() {
      const data = [
        { result: 'Passed', count: ${build.runStats[1].passed}, bg: 'rgb(43,189,86)' },
        { result: 'Failed', count: ${build.runStats[1].failed}, bg: 'rgb(233,80,113)' },
        { result: 'Skipped', count: ${build.runStats[1].skipped}, bg: 'rgb(227,203,94),' }
      ];

      const donut = {
        labels: [
          'Passed',
          'Failed',
          'Skipped'
        ]
      };

      new Chart(
        document.getElementById('stats2'),
        {
          type: 'doughnut',
          data: {
            labels: donut.labels,
            datasets: [
              {
                data: data.map(row => row.count),
                backgroundColor: data.map(row => row.bg)
              }
            ]
          },
          options: {
            plugins: {
              responsive: true,
              legend: {
                display: false
              }
            }
          }
        }
      );
    })();
    </#if>
  </script>
  </#if>
</body>

</html>
