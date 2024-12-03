// init::statusFilters
const statusFilters = [];
document.querySelectorAll('#status-filter > button').forEach((e) => {
  statusFilters.push(e);
});

// init::tests
const tests = [];
document.querySelectorAll('.test-result').forEach((card) => {
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
    card.style.display = card.className.indexOf(status) == -1 ? 'none' : 'block';
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
    tests.forEach((card) => {
      card.style.display = toggleResult && card.className.indexOf(status) == -1 ? 'none' : 'block';
    })
  })
});

// filter tests on click a tag in tags table
const onTagClick = (tag) => {
  tests.forEach((card) => {
    let display = 'none';
    if ([...card.querySelectorAll('.tag-list > *')]
      .map(badge => badge.innerText)
      .filter(text => text === tag).length) {
        display = 'block';
      }
      card.style.display = display;
  })
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
