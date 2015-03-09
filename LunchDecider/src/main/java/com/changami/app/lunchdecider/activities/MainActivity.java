package com.changami.app.lunchdecider.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import com.changami.app.lunchdecider.PointListActivity;
import com.changami.app.lunchdecider.R;
import com.changami.app.lunchdecider.fragment.AddFragment;
import com.changami.app.lunchdecider.fragment.SuggestFragment;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        // SuggestFragmentを初期表示とする
        transaction.replace(R.id.fragments, SuggestFragment.newInstance());
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            transaction.replace(R.id.fragments, AddFragment.newInstance());
            transaction.addToBackStack(null);
            transaction.commit();

            return true;
        }
        if (id == R.id.action_list) {
            startActivity(new Intent(this, PointListActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

}
