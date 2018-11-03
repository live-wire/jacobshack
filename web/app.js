var search = instantsearch({
  // Replace with your own values
  appId: '4MXHEI8AM6',
  apiKey: '52a1d82dd957c7603916dc3cab946c9e', // search only API key, no ADMIN key
  indexName: 'whodat',
  urlSync: true,
  searchParameters: {
    hitsPerPage: 10
  }
});

// Add this after the previous JavaScript code
search.addWidget(
  instantsearch.widgets.searchBox({
    container: '#search-input'
  })
);

// Add this after the previous JavaScript code
search.addWidget(
  instantsearch.widgets.hits({
    container: '#hits',
    templates: {
      item: document.getElementById('hit-template').innerHTML,
      empty: "We didn't find any results for the search <em>\"{{query}}\"</em>"
    }
  })
);


// Add this after the other search.addWidget() calls
search.addWidget(
  instantsearch.widgets.pagination({
    container: '#pagination'
  })
);


// Add this after all the search.addWidget() calls
search.start();