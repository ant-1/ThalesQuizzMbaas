package com.thales.thalesquizzmbaas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.OnItemSelected;
import com.thales.thalesquizzmbaas.mbass.Mbaas;
import com.thales.thalesquizzmbaas.model.Quizz;
import com.thales.thalesquizzmbaas.ui.adapters.ArrayQuizzAdapter;
import com.thales.thalesquizzmbaas.utils.ThalesQuizzMbaasConstants;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import java.util.List;


public class MainMenuActivity extends MainActivity {

    @InjectView(R.id.mQuizzesListView)
    ListView mQuizzesListView;

    @InjectView(R.id.mSwipeRefreshLayout)
    PullToRefreshLayout mSwipeRefreshLayout;

    ArrayQuizzAdapter mQuizzesAdapter;

    public static final String DEMO_HOST_USERNAME = "Antoine";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        injectViews();
        ActionBarPullToRefresh.from(this).allChildrenArePullable()
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        showLoader();
                        Mbaas.getInstance().getQuizzes();
                    }
                })
                .setup(mSwipeRefreshLayout);

        mQuizzesAdapter = new ArrayQuizzAdapter(this, android.R.layout.simple_list_item_1);
        TextView emptyText = (TextView) getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
        emptyText.setText(getString(R.string.no_results));
        mQuizzesListView.setEmptyView(emptyText);
        mQuizzesListView.setAdapter(mQuizzesAdapter);
        mQuizzesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainMenuActivity.this.onItemClick(position);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showLoader();
        Mbaas.getInstance().getQuizzes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        if (Mbaas.getInstance().getUserName()!= null && !Mbaas.getInstance().getUserName().equals(DEMO_HOST_USERNAME)) {
            menu.findItem(R.id.action_start_demo).setVisible(false);
            menu.findItem(R.id.action_stop_demo).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_disconnect) {
            getSharedPreferencesEditor().putString(ThalesQuizzMbaasConstants.KEY_LOGIN, "").commit();
            getSharedPreferencesEditor().putString(ThalesQuizzMbaasConstants.KEY_PASSWORD, "").commit();
            finish();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }
//        else if (id == R.id.action_add) {
//            Intent intent = new Intent(this, CreateQuizzActivity.class);
//            startActivity(intent);
//            return true;
//        }
         else if (id == R.id.action_refresh) {
            showLoader();
            Mbaas.getInstance().getQuizzes();
            return true;
        } else if (id == R.id.action_start_demo) {
            showLoader();
            Mbaas.getInstance().startDemo();
        } else if (id == R.id.action_stop_demo) {
            showLoader();
            Mbaas.getInstance().stopDemo();
        }
        return super.onOptionsItemSelected(item);
    }


    public void onEventMainThread(Mbaas.GetQuizzesEvent event) {
        hideLoader();
        mSwipeRefreshLayout.setRefreshing(false);
        if (!event.isSuccessfull()) {
            Toast.makeText(this, event.exception.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            mQuizzesAdapter.clear();
            List<Quizz> quizzes = (List<Quizz>) event.result;
            mQuizzesAdapter.addAll(quizzes);
        }
    }

    @OnItemSelected(R.id.mQuizzesListView)
    void onItemClick(int position) {
        Quizz quizz = mQuizzesAdapter.getItem(position);
        if (quizz.getCreatorId() != null && quizz.getCreatorId().equals(Mbaas.getInstance().getUserId())) {
            //user is the admin of this quiz
            Intent intent = new Intent(this, AdminQuizzActivity.class);
            Parcelable[] quizzTab = new Parcelable[1];
            quizzTab[0] = quizz;
            intent.putExtra(ThalesQuizzMbaasConstants.KEY_QUIZZ, quizzTab);
            startActivity(intent);
        } else if (quizz.getStatus() != null && quizz.getStatus().equals(ThalesQuizzMbaasConstants.STATUS_STARTED)) {
            //user participates in the quizz
            Intent intent = new Intent(this, PlayQuizzActivity.class);
            Parcelable[] quizzTab = new Parcelable[1];
            quizzTab[0] = quizz;
            intent.putExtra(ThalesQuizzMbaasConstants.KEY_QUIZZ, quizzTab);
            startActivity(intent);
        }
    }

    public void onEventMainThread(Mbaas.StartDemoEvent event) {
        hideLoader();
        if (!event.isSuccessfull()) {
            Toast.makeText(this, event.exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void onEventMainThread(Mbaas.StopDemoEvent event) {
        hideLoader();
        if (!event.isSuccessfull()) {
            Toast.makeText(this, event.exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
