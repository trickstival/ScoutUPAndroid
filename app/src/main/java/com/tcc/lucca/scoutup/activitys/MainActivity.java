package com.tcc.lucca.scoutup.activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.tcc.lucca.scoutup.R;
import com.tcc.lucca.scoutup.gerenciar.LoginClass;
import com.tcc.lucca.scoutup.gerenciar.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private static final int TAG_CODE_PERMISSION_LOCATION = 1;
    private LoginClass loginClass = new LoginClass(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
        } else {


            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, TAG_CODE_PERMISSION_LOCATION);
        }



        if (loginClass.getFirebaseUser() == null) {

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();

        } else {


            ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

            adapter.addFragment(new PerfilFrag(), "");
            adapter.addFragment(new AgendaFrag(), "");
            viewPager.setAdapter(adapter);


            final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.setAnimation(null);
            tabLayout.getTabAt(0).setIcon(R.drawable.maoverde);
            tabLayout.getTabAt(1).setIcon(R.drawable.fogomarrom);


            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                    if (tabLayout.getSelectedTabPosition() == 0) {

                        tabLayout.getTabAt(0).setIcon(R.drawable.maoverde);
                        tabLayout.getTabAt(1).setIcon(R.drawable.fogomarrom);
                    }

                    if (tabLayout.getSelectedTabPosition() == 1) {
                        tabLayout.getTabAt(0).setIcon(R.drawable.maomarrom);
                        tabLayout.getTabAt(1).setIcon(R.drawable.fogoverde);

                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                    tabLayout.getTabAt(0).setIcon(R.drawable.maomarrom);
                    tabLayout.getTabAt(1).setIcon(R.drawable.fogomarrom);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });


        }

    }


    public void logout(View view) {


        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        finish();

    }


}

