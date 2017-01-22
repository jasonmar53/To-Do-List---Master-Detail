package edu.jkmar.masterdetail;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import edu.jkmar.masterdetail.dummy.DummyContent;

import static android.app.Activity.RESULT_OK;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static ScrollView rootView;
    static ArrayList <ListItem> item = ToDoListManager.getmList();
    static int pos;
    static String uri;
    static CheckBox cb;

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            pos = getArguments().getInt(ARG_ITEM_ID);


            Activity activity = this.getActivity();
           // CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            //if (appBarLayout != null) {
                //appBarLayout.setTitle(mItem.content);
            //}
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ScrollView) inflater.inflate(R.layout.item_detail, container, false);
        final LinearLayout linear =  (LinearLayout) rootView.findViewById(R.id.frag_linear);
        // Show the dummy content as text in a TextView.
        //((TextView) rootView.findViewById(R.id.item_detail)).setText(mItem.details);
        final ArrayList<ListItem> item = ToDoListManager.getmList();

        final EditText mEdit = (EditText) linear.findViewById(R.id.editText); //user input
        final EditText detail = (EditText) linear.findViewById(R.id.detail);
        cb = (CheckBox) linear.findViewById(R.id.checkbox);
        if(item.size() > 0) {
            uri = item.get(pos).code;
            mEdit.setText(item.get(pos).name);
            cb.setChecked(item.get(pos).checked);
            if (item.get(pos).detail != null) {
                detail.setText(item.get(pos).detail);
            }
        }

        Button submit = (Button) linear.findViewById(R.id.submit);
        Button image = (Button) linear.findViewById(R.id.addp);
        Button remove = (Button) linear.findViewById(R.id.remp);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mEdit.getText().toString();
                String d = detail.getText().toString();
                if (s.length() > 0) {
                    item.get(pos).name = s;
                }
                item.get(pos).detail = d;
                ItemListActivity.update(pos, item.get(pos).name, item.get(pos).checked, uri, item.get(pos).detail);
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uri = null;
                ImageView img=(ImageView) linear.findViewById(R.id.imag);
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

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (resultCode == RESULT_OK) {
            // Make sure the request was successful
            if (requestCode == 31) {
                Uri uri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                saveToInternalStorage(bitmap);
            }
        }
    }
    private void saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        Intent mIntent = getActivity().getIntent();
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
        //item.get(pos).code = directory.getAbsolutePath();
        //uri = item.get(pos).code;
        uri = directory.getAbsolutePath();

    }

    private void loadImageFromStorage(String path)
    {
        try {
            File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img=(ImageView) rootView.findViewById(R.id.imag);
            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
       // Intent mIntent = getActivity().getIntent();
       // final int pos = mIntent.getIntExtra("position", 0);
        //final ArrayList <ListItem> item = ToDoListManager.getmList();
        loadImageFromStorage(uri);
        // pass in 3rd string uri from entry list);
    }

    public void onResume() {
        super.onResume();
       // Intent mIntent = getActivity().getIntent();
       // final int pos = mIntent.getIntExtra("position", 0);
        //final ArrayList <ListItem> item = ToDoListManager.getmList();
        loadImageFromStorage(uri);
        // pass in 3rd string uri));
    }

    public static void checked(boolean check, int position){
        if(position == pos) {
            Log.i("TAG", "onCheckedChanged:  part 2" + position);
            item.get(pos).checked = check;
            cb.setChecked(item.get(pos).checked);
        }
    }
}
