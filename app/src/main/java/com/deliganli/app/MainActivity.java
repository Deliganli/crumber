package com.deliganli.app;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.deliganli.crumber.CrumbView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private CrumbView kek;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private EditText dotIntervalText;
    private EditText dotRadiusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        kek = (CrumbView) findViewById(R.id.view_kek);
        kek.post(new Runnable() {
            @Override
            public void run() {
                generatePath();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generatePath();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        dotIntervalText = (EditText) navigationView.findViewById(R.id.text_dot_interval);
        dotIntervalText.setText(String.valueOf(kek.getDotInterval()));
        dotRadiusText = (EditText) navigationView.findViewById(R.id.text_dot_radius);
        dotRadiusText.setText(String.valueOf(kek.getDotRadius()));

        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                kek.setDotInterval(Integer.valueOf(dotIntervalText.getText().toString()));
                kek.setDotRadius(Integer.valueOf(dotRadiusText.getText().toString()));
                kek.invalidate();
            }
        });
    }

    private void generatePath() {
        final Random rnd = new Random();
        int nodeCount = Math.abs(rnd.nextInt()) % 10 + 2;
        ArrayList<PointF> points = new ArrayList<>(nodeCount);
        for (int i = 0; i < nodeCount; i++) {
            points.add(new PointF(rnd.nextFloat(), rnd.nextFloat()));
        }
        kek.setPath(points);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        drawer.removeDrawerListener(toggle);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            startActivity(new Intent(this, InfoActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
