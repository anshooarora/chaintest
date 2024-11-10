(async function() {
  const data = [
    { year: 2010, count: 10 },
    { year: 2011, count: 20 },
    { year: 2012, count: 15 },
    { year: 2013, count: 25 },
    { year: 2014, count: 22 },
    { year: 2015, count: 30 },
    { year: 2016, count: 28 },
  ];

  const donut = {
    labels: [
      'Red',
      'Blue',
      'Yellow'
    ],
    datasets: [{
      label: 'My First Dataset',
      data: [300, 50, 100],
      backgroundColor: [
        'rgb(255, 99, 132)',
        'rgb(54, 162, 235)',
        'rgb(255, 205, 86)'
      ],
      hoverOffset: 4
    }]
  };

  new Chart(
    document.getElementById('acquisitions'),
    {
      type: 'doughnut',
      data: {
        labels: donut.labels,
        datasets: [
          {
            label: 'Acquisitions by year',
            data: data.map(row => row.count)
          }
        ]
      },
      options: {
        plugins: {
          legend: {
            display: false
          }
        }
      }
    }
  );
})();


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

// on key down events (shortcuts)
window.onkeydown = evt => {
  if (evt.metaKey) {
    return;
  }
  const k = evt.key.toLowerCase();
  console.log(k)
  k === 'p' && filterTests('passed');
  k === 'f' && filterTests('failed');
  k === 's' && filterTests('skipped');
  k === 'escape' && filterTests('');
}