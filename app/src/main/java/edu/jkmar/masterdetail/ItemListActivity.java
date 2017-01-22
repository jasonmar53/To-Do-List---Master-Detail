package edu.jkmar.masterdetail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;


import edu.jkmar.masterdetail.dummy.DummyContent;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {
/*    if (findViewById(R.id.item_detail_container) != null) {
        // The detail container view will be present only in the
        // large-screen layouts (res/values-w900dp).
        // If this view is present, then the
        // activity should be in two-pane mode.
        mTwoPane = true;
    }
*/
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */

    EditText mEdit;
    static View prev;
    static ArrayList <ListItem> mList;
    static MyAdapter adapter;
    final Context context = this;
    ItemDetailFragment fragment;
    DatabaseHandler db = new DatabaseHandler(this);
    private boolean mTwoPane;
    static int selected;

    //  private ListView lv;
    //  int rempos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);

        assert rv != null;
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        mList = db.getList();
        adapter = new MyAdapter(mList);
        rv.setAdapter(adapter);


        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                ListItemViewHolder avh = (ListItemViewHolder) viewHolder;
                if (direction == ItemTouchHelper.RIGHT) {

                    int index = viewHolder.getAdapterPosition();
                    if (index == selected && fragment != null) {
                        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    }
                    mList.remove(index);
                    adapter.notifyItemRemoved(index);
                }

            }
        };
        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(rv);

        //submit text
        mEdit = (EditText) findViewById(R.id.editText); //user input
        //submit button
        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mEdit.getText().toString();
                if (s.length() > 0) {
                    addListEntry(s, adapter);
                    mEdit.getText().clear();
                }
            }
        });
        mEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String s = mEdit.getText().toString();
                    if (s.length() > 0 && !s.trim().isEmpty()) {
                        addListEntry(s, adapter);
                        mEdit.getText().clear();
                    }
                }
                return false;
            }
        });

    }

    @Override
    protected void onStart(){
        adapter.notifyDataSetChanged();
        super.onStart();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if(mTwoPane) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu, menu);
            return true;
        }
        else {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menushare, menu);
            return true;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.share) {
            Log.i("Tag", "sharee");
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, ToDoListManager.sending());
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
            return true;
        }
        else if (item.getItemId() == R.id.delete) {
            if(fragment != null) {

                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                prev.setBackgroundColor(Color.TRANSPARENT);
                mList.remove(selected);
                fragment = null;
                adapter.notifyDataSetChanged();
                selected = -1;
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.addListItems();
    }

    @Override
    public void onStop() {
        super.onStop();
        db.addListItems();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return ToDoListManager.getmList();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("key", ToDoListManager.getmList());
        super.onSaveInstanceState(savedInstanceState);
    }



    public class MyAdapter extends RecyclerView.Adapter<ListItemViewHolder> {
        private ArrayList<ListItem> mList;

        public MyAdapter(ArrayList<ListItem> items) {
            this.mList = items;
        }

        public int getItemViewType(int position) {
            return R.layout.list_item;
        }

        public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ListItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
        }

        public void onBindViewHolder(ListItemViewHolder holder, int position) {
            holder.bind(mList.get(position));
        }

        public int getItemCount() {
            return mList.size();
        }

    }

    public class ListItemViewHolder extends RecyclerView.ViewHolder {

        private TextView mTv;
        private CheckBox cb;
        private ListItem li;
        private Context context;

        public ListItemViewHolder(final View itemView) {
            super(itemView);
            context = itemView.getContext();
            mTv = (TextView) itemView.findViewById(R.id.tv);
            cb = (CheckBox) itemView.findViewById(R.id.checkbox);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        if (prev != null) {
                            prev.setBackgroundColor(Color.TRANSPARENT);
                            prev = itemView;
                            itemView.setBackgroundColor(0xFF00FF00);
                        }

                        else {
                            prev = itemView;
                            itemView.setBackgroundColor(0xFF00FF00);
                        }

                        Bundle arguments = new Bundle();
                        arguments.putInt(ItemDetailFragment.ARG_ITEM_ID, getAdapterPosition());
                        fragment = new ItemDetailFragment();
                        fragment.setArguments(arguments);
                        selected = getAdapterPosition();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    }
                    else {
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        intent.putExtra("position", getAdapterPosition());
                        context.startActivity(intent);
                        Log.i("TAG", "onClick: " + getAdapterPosition());
                    }
                }
            });

        }

        public void bind(ListItem a) {
            this.li = a;
            mTv.setText(li.name);
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    li.checked = isChecked;
                    buttonView.setChecked(isChecked);
                    if(mTwoPane) {
                        Log.i("TAG", "onCheckedChanged: ");
                        ItemDetailFragment.checked(isChecked, getAdapterPosition());
                    }
                }
            }
            );
            cb.setChecked(li.checked);

        }
    }


    public void addListEntry(String s, MyAdapter adapter) {
        ListItem entry = new ListItem(s, false, null, null);
        ToDoListManager.addItem(entry);
        adapter.notifyDataSetChanged();

    }

    public static void update(int pos, String name, boolean check, String uri, String detail) {
        mList.get(pos).name = name;
        mList.get(pos).checked = check;
        mList.get(pos).code = uri;
        mList.get(pos).detail = detail;
        adapter.notifyDataSetChanged();
    }

    public static void remove(int pos) {
        mList.remove(pos);
        adapter.notifyDataSetChanged();
    }


}