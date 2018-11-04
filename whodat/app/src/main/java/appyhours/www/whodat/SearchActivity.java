package appyhours.www.whodat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ToggleButton;

import com.algolia.instantsearch.helpers.InstantSearch;
import com.algolia.instantsearch.helpers.Searcher;

public class SearchActivity extends AppCompatActivity {

    private Searcher searcher;
    private static final String ALGOLIA_APP_ID = "4MXHEI8AM6";
    public static final String ALGOLIA_INDEX_NAME = "whodat";
    private static final String ALGOLIA_API_KEY = "52a1d82dd957c7603916dc3cab946c9e";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searcher = Searcher.create(ALGOLIA_APP_ID, ALGOLIA_API_KEY, ALGOLIA_INDEX_NAME);
        InstantSearch helper = new InstantSearch(this, searcher);
        helper.search();

    }

    @Override
    protected void onDestroy() {
        searcher.destroy();
        super.onDestroy();
    }

}
