// charts
const labels = ['Passed', 'Failed', 'Skipped'];
const getOptions = (data) => {
  const label = (Math.floor(data.passed / data.total * 100)) + '%';
  const options = {
    cutout: '65%',
    plugins: {
      responsive: true,
      legend: {
        display: false
      },
      annotation: {
        annotations: {
          dLabel: {
            type: 'doughnutLabel',
            content: () => [label],
            color: ['#999']
          }
        }
      }
    }
  };
  return options;
}

const emptyStat = [
  { result: 'Passed', count: 0, bg: 'rgb(140, 197, 83)' },
  { result: 'Failed', count: 0, bg: 'rgb(233,80,113)' },
  { result: 'Skipped', count: 0, bg: 'rgb(221, 91, 96)' }
];

var chart1, chart2, chart3;

// chart: Features or Classes
function depth1Chart() {
  if (stats1) {
    chart1 = new Chart(
      document.getElementById('stats1'),
      {
        type: 'doughnut',
        data: {
          labels: labels,
          datasets: [
            {
              data: stats1.map(row => row.count),
              backgroundColor: stats1.map(row => row.bg),
              borderColor: 'transparent'
            }
          ]
        },
        options: getOptions(stats1Annotation)
      }
    );
  }
}

// chart: Scenarios or Methods
function depth2Chart() {
  if (stats2) {
    chart2 = new Chart(
      document.getElementById('stats2'),
      {
        type: 'doughnut',
        data: {
          labels: labels,
          datasets: [
            {
              data: stats2.map(row => row.count),
              backgroundColor: stats2.map(row => row.bg),
              borderColor: 'transparent'
            }
          ]
        },
        options: getOptions(stats2Annotation)
      }
    );
  }
}

// chart: Scenarios or Methods
function depth3Chart() {
  if (stats3) {
    const el = document.getElementById('stats3');
    if (el) {
      chart3 = new Chart(
        el,
        {
          type: 'doughnut',
          data: {
            labels: labels,
            datasets: [
            {
              data: stats3.map(row => row.count),
              backgroundColor: stats3.map(row => row.bg),
              borderColor: 'transparent'
            }
          ]},
          options: getOptions(stats3Annotation)
        }
      );
    }
  }
}

// init::charts
depth1Chart();
depth2Chart();
depth3Chart();

// init::statusFilters
const statusFilters = [];
document.querySelectorAll('#status-filter > button').forEach((e) => {
  statusFilters.push(e);
});

// init::test-container
const testContainers = [];
document.querySelectorAll('.test-container').forEach((card) => {
  testContainers.push(card);
});

// init::tests
const tests = [];
document.querySelectorAll('.result').forEach((card) => {
  tests.push(card);
});
const leafs = [];
document.querySelectorAll('.leaf').forEach((l) => {
  leafs.push(l);
});

// init::tags
const tags = [];
document.querySelectorAll('#tag-summary .tag').forEach((e) => {
  tags.push(e);
});

// clear-filters btn event
const clearFiltersBtn = document.querySelector('#clear-filters');
clearFiltersBtn.addEventListener('click', el => {
  filterTests('');
});

// helper to filter tests by their status
const results = ['passed', 'failed', 'skipped'];
const filterTests = (_status) => {
  statusFilters.forEach((e) => {
    e.classList.remove('active');
  });
  const status = _status.trim().toLowerCase();
  if (results.includes(status)) {
    results.forEach(x => x != status && document.querySelector('#' + status).classList.remove('active'));
    document.querySelector(`#${status}`).classList.toggle('active');
  }
  tests.forEach((card) => {
    card.className.indexOf(status) == -1 ? card.classList.add('d-none') : card.classList.remove('d-none');
  });
  testContainers.forEach((container) => {
    container.querySelectorAll('.result').length == container.querySelectorAll('.result.d-none').length
      ? container.classList.add('d-none') : container.classList.remove('d-none');
  });
}

// filter all tests based on their status
document.querySelectorAll('#status-filter > button').forEach((e) => {
  e.addEventListener('click', el => {
    // remove any other buttons with .active class
    statusFilters.forEach(x => el.target.id != x.id && x.classList.remove('active'));
    // gets the toggle result: true if toggled
    const toggleResult = el.target.classList.toggle('active');
    const status = el.target.innerText.toLowerCase();
    if (status.indexOf('clear') > -1) {
      filterTests('');
      return;
    }
    filterTests(status);
  })
});

// redraw charts on tag click
function redrawChart(stat, annotation, containers, idx) {
  stat[0].count = containers.filter(x => x.classList.contains('passed') && !x.classList.contains('d-none')).length;
  stat[1].count = containers.filter(x => x.classList.contains('failed') && !x.classList.contains('d-none')).length;
  annotation.total = stat[0].count + stat[1].count;
  annotation.passed = stat[0].count;
  document.querySelector('#sp' + idx).innerText = stat[0].count;
  document.querySelector('#sf' + idx).innerText = stat[1].count;
}

// filter tests on click a tag in tags table
const onTagClick = (tag) => {
  tests.forEach((card) => {
    if ([...card.querySelectorAll('.tag-list > *')]
      .map(badge => badge.innerText)
      .filter(text => text === tag).length) {
        card.classList.remove('d-none');
      } else {
        card.classList.add('d-none');
      }
  });
  leafs.forEach((l) => {
    if ([...l.querySelectorAll('.tag-list > *')]
      .map(badge => badge.innerText)
      .filter(text => text === tag).length) {
        l.classList.remove('d-none');
      } else {
        l.classList.add('d-none');
      }
  });
  testContainers.forEach((container) => {
    container.querySelectorAll('.result').length == container.querySelectorAll('.result.d-none').length
      ? container.classList.add('d-none') : container.classList.remove('d-none');
  });

  // redraw depth1 chart
  if (stats1) {
    redrawChart(stats1, stats1Annotation, testContainers, 1);
    chart1.destroy();
    depth1Chart();
  }

  // redraw depth2 chart
  if (stats2) {
    redrawChart(stats2, stats2Annotation, tests, 2);
    chart2.destroy();
    depth2Chart();
  }

  // redraw depth3 chart
  if (stats3) {
    redrawChart(stats3, stats3Annotation, leafs, 3);
    chart3.destroy();
    depth3Chart();
  }
}
tags.forEach((e) => {
  e.addEventListener('click', el => {
    onTagClick(el.target.innerText);
  })
});

// resets state: clear all filters and removes modals
const resetState = () => {
  setTimeout(function(){
    window.location.reload(true);
  });
}

// on key down events (shortcuts)
window.onkeydown = evt => {
  if (evt.metaKey) {
    return;
  }
  const k = evt.key.toLowerCase();
  k === 'p' && filterTests('passed');
  k === 'f' && filterTests('failed');
  k === 's' && filterTests('skipped');
  k === 'escape' && resetState();
  k === 'l' && toggleLights();
}

// filter only failed tests on load
const failures = document.querySelectorAll('.test-result.failed').length;
if (failures > 0) {
    filterTests('failed');
}

// system-info modal
const toggleSysInfo = (show) => {
  let display = 'none';
  if (show) {
    display = 'block';
  }
  document.querySelector("#sys-info-modal").style.display = display;
}
document.querySelector("#sys-info").addEventListener('click', el => {
    toggleSysInfo(true);
});

// lights
const toggleLights = () => {
  const dark = document.querySelector('body').getAttribute('data-bs-theme') === 'dark';
  document.querySelector('body').setAttribute('data-bs-theme', dark ? '' : 'dark');
}

// attachments
const toggleAttachmentModal = (show) => {
  let display = 'none';
  if (show) {
    display = 'block';
  }
  document.querySelector("#attachment-modal").style.display = display;
}
document.querySelectorAll('.embed img').forEach((e) => {
  e.addEventListener('click', el => {
    const src = el.target.getAttribute('src');
    const modal = document.querySelector('#attachment-modal');
    modal.querySelector('img').setAttribute('src', src);
    toggleAttachmentModal(true);
  });
})

// info modal
const toggleInfoModal = (show) => {
  let display = 'none';
  if (show) {
    display = 'block';
  }
  document.querySelector("#info-modal").style.display = display;
}
document.querySelector("#shortcuts").addEventListener('click', el => {
    toggleInfoModal(true);
});

// handle modal close
const modalClose = () => {
  toggleSysInfo(false);
  toggleAttachmentModal(false);
  toggleInfoModal(false);
}
document.querySelector('body').addEventListener('click', el => {
    el.target.className == 'modal' && modalClose();
})

// toggle summary section
document.querySelector('#summary-toggle').addEventListener('click', el => {
  el.target.classList.toggle('active');
  const summary = document.querySelector('#summary');
  summary.classList.toggle('d-none');
})
