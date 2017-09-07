package com.developers.mytodoapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set the default home fragment
        setTitle("Home");
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment,new MainFragment()).commit();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    // To navigate through fragments
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (id == R.id.nav_home) {
            setTitle("Home");

            fragmentManager.beginTransaction().replace(R.id.fragment,new MainFragment()).commit();

        } else if (id == R.id.nav_completedTask) {
            setTitle("Completed Task List");
            fragmentManager.beginTransaction().replace(R.id.fragment,new CompTaskFragment()).commit();

        } else if (id == R.id.nav_insights) {
            setTitle("Insights");
            fragmentManager.beginTransaction().replace(R.id.fragment,new InsightFragment()).commit();

        } else if (id == R.id.nav_log) {
            setTitle("Log");
            fragmentManager.beginTransaction().replace(R.id.fragment,new LogFragment()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onSelect(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        if (checked) {
            SqLiteTaskHelper taskHelper = new SqLiteTaskHelper(getBaseContext());
            String TaskName = ((CheckBox) view).getText().toString();
            SQLiteDatabase db = taskHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("TASK_STATUS", "1");
            db.update("TASK_LIST", values, "TASK_NAME='"+TaskName+"'", null);
        }
    }
}
