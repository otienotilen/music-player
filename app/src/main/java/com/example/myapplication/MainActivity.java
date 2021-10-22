package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
//import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
     ListView listView;
     String[] items;
     private BottomNavigationView bottomNavigationView;
     SearchView searchView;
     ArrayAdapter stringArrayAdapter;

    private DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         drawerLayout = findViewById(R.id.nav_view);
        Toolbar toolbar =findViewById(R.id.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_draw_open, R.string.navigation_draw_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
       // getSupportActionBar().hide();
        listView = findViewById(R.id.listViewSong);
        runtimePermission();


      searchView = findViewById(R.id.search_bar);

        bottomNavigationView = findViewById(R.id.navigationbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.songslist){
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    return true;}
                else
                if (id == R.id.nowplaying) {

                   // Intent intent = new Intent(MainActivity.this, PlayerActivity.class);

                   // startActivity(intent);
                    return true;
                }
                else
                if (id == R.id.stream){
                    Intent intent = new Intent(MainActivity.this, Stream.class);
                    startActivity(intent);
                    return true;
                }

             return true;

            }

        });
        //Search view set filter

      searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
          @Override
         public boolean onQueryTextSubmit(String query) {

              //stringArrayAdapter.getFilter().filter(query);
              //MainActivity.this.listView.getTextFilter();
              return false;
          }

          @Override
           public boolean onQueryTextChange(String newText) {
               //stringArrayAdapter.getFilter().filter(newText);

             //MainActivity.this.listView.getTextFilter();

               return false;
          }
       });

    }

    @Override
    public void onBackPressed() {
      if (drawerLayout.isDrawerOpen(GravityCompat.START))
      {
          drawerLayout.closeDrawer(GravityCompat.START);
      }
      else
      {
          super.onBackPressed();
      }
    }

    public void  runtimePermission()
    {
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        displaySongs();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();

                    }
                }).check();
    }
    public ArrayList<File> findSong (File file)
    {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();
        for (File singlefile: files)
        {
            if(singlefile.isDirectory() && !singlefile.isHidden())
            {
                arrayList.addAll(findSong(singlefile));
            }
            else
            {
                if(singlefile.getName().endsWith(".mp3") || singlefile.getName().endsWith(".wav") )
                {
                    arrayList.add(singlefile);
                }
            }
        }
        return arrayList;
    }

    void displaySongs()
    {
        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());
        items = new String[mySongs.size()];
        for (int i = 0; i<mySongs.size();i++)
        {
            items[i] = mySongs.get(i).getName().toString().replace(".mp3","").replace(".wav","");
        }
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(myAdapter);
        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                String songName = (String) listView.getItemAtPosition(i);
                startActivity(new Intent(getApplicationContext(), PlayerActivity.class)
                .putExtra("songs", mySongs)
                .putExtra("songname", songName)
                .putExtra("pos", i));
            }
        });
    }
    class CustomAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            View myView = getLayoutInflater().inflate(R.layout.list_item, null);
            TextView textsong = myView.findViewById(R.id.txtsongname);
            textsong.setSelected(true);
            textsong.setText(items[i]);
            return myView;
        }
    }
}