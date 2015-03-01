package com.github.gfranks.checkable.view.sample;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.gfranks.checkable.view.CheckableGroup;
import com.github.gfranks.checkable.view.CheckableView;

public class MainActivity extends ActionBarActivity implements CheckableView.OnCheckedChangeListener, CheckableGroup.OnCheckedChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.activity_toolbar));
        ((CheckableView) findViewById(R.id.checkable_view)).setOnCheckedChangeListener(this);
        ((CheckableView) findViewById(R.id.checkable_view_2)).setOnCheckedChangeListener(this);

        ((CheckableGroup) findViewById(R.id.checkable_group)).setOnCheckedChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CheckableView checkableView, boolean isChecked) {
        Log.v(checkableView.getClass().getName(), "CheckableView at (" + checkableView.getId() + ") is checked: " + isChecked);
    }

    @Override
    public void onCheckedChanged(CheckableGroup checkableGroup, CheckableView checkableView, boolean isChecked) {
        Log.v(checkableView.getClass().getName(), "CheckableView at (" + checkableView.getId() + ") in group (" +
                checkableGroup.getId() + ") is checked: " + isChecked);
    }
}
