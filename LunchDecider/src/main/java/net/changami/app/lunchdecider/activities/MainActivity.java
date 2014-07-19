package net.changami.app.lunchdecider.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import net.changami.app.lunchdecider.AddActivity;
import net.changami.app.lunchdecider.PointListActivity;
import net.changami.app.lunchdecider.R;
import net.changami.app.lunchdecider.fragment.SuggestFragment;

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
            startActivity(new Intent(this, AddActivity.class));
            return true;
        }
        if (id == R.id.action_list) {
            startActivity(new Intent(this, PointListActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

}
