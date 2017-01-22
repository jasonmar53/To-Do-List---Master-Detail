package edu.jkmar.masterdetail;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ItemListActivity}.
 */

public class ItemDetailActivity extends AppCompatActivity {

    static int pos;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secactivity_main);
        Intent mIntent = getIntent();
        pos = mIntent.getIntExtra("position", 0);

        final ArrayList<ListItem> item = ToDoListManager.getmList();

        final EditText mEdit = (EditText) findViewById(R.id.editText); //user input

        final EditText detail = (EditText) findViewById(R.id.detail);
        CheckBox cb = (CheckBox) findViewById(R.id.checkbox);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        mEdit.setText(item.get(pos).name);

        if (item.get(pos).detail != null) {
            detail.setText(item.get(pos).detail);
        }
        cb.setChecked(item.get(pos).checked);
        Button submit = (Button) findViewById(R.id.submit);
        Button image = (Button) findViewById(R.id.addp);
        Button remove = (Button) findViewById(R.id.remp);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mEdit.getText().toString();
                String d = detail.getText().toString();
                if (s.length() > 0) {
                    item.get(pos).name = s;
                }
                item.get(pos).detail = d;
                ItemListActivity.update(pos, item.get(pos).name, item.get(pos).checked, item.get(pos).code, item.get(pos).detail);
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.get(pos).code = null;
                ImageView img=(ImageView)findViewById(R.id.imag);
                img.setImageResource(R.drawable.puppy);
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i, 31);
            }
        });

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.get(pos).checked = isChecked;
            }
        });


    }


    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menudel, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        if (item.getItemId() == R.id.delete) {
                ItemListActivity.remove(pos);
                onBackPressed();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (resultCode == RESULT_OK) {
            // Make sure the request was successful
            if (requestCode == 31) {
                Uri uri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                saveToInternalStorage(bitmap);
            }
        }
    }
    private void saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        Intent mIntent = getIntent();
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(53458378);
        final int pos = mIntent.getIntExtra("position", 0);
        File directory = cw.getDir("imageDir" + pos + randomInt, Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //add the directory to 3rd string variable

        final ArrayList <ListItem> item = ToDoListManager.getmList();
        item.get(pos).code = directory.getAbsolutePath();

    }

    private void loadImageFromStorage(String path)
    {
        try {
            File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img=(ImageView)findViewById(R.id.imag);
            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent mIntent = getIntent();
        final int pos = mIntent.getIntExtra("position", 0);
        final ArrayList <ListItem> item = ToDoListManager.getmList();
        loadImageFromStorage(item.get(pos).code);
        // pass in 3rd string uri from entry list);
    }

    protected void onResume() {
        super.onResume();
        Intent mIntent = getIntent();
        final int pos = mIntent.getIntExtra("position", 0);
        final ArrayList <ListItem> item = ToDoListManager.getmList();
        loadImageFromStorage(item.get(pos).code);
        // pass in 3rd string uri));
    }
}
