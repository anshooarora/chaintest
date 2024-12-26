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

// chart: Features or Classes
(async function() {
  new Chart(
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
})();

// chart: Scenarios or Methods
if (stats2) {
(async function() {
  new Chart(
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
})();
}

// chart: Scenarios or Methods
if (stats3) {
(async function() {
  const el = document.getElementById('stats3');
  if (el) {
    new Chart(
      document.getElementById('stats3'),
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
})();
}

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
  })
  testContainers.forEach((container) => {
    container.querySelectorAll('.result').length == container.querySelectorAll('.result.d-none').length
      ? container.classList.add('d-none') : container.classList.remove('d-none');
  });
}
tags.forEach((e) => {
  e.addEventListener('click', el => {
    onTagClick(el.target.innerText);
  })
});

// resets state: clear all filters and removes modals
const resetState = () => {
  filterTests('');
  toggleSysInfo(false);
  toggleAttachmentModal(false);
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
