package android.camant.com.elibraryandroid.activities;

import android.camant.com.elibraryandroid.Constants;
import android.camant.com.elibraryandroid.R;
import android.camant.com.elibraryandroid.adapters.BookCursorAdapter;
import android.camant.com.elibraryandroid.adapters.CategoryAdapter;
import android.camant.com.elibraryandroid.adapters.OnItemClickListener;
import android.camant.com.elibraryandroid.db.BookDbHelper;
import android.camant.com.elibraryandroid.db.CategoryDbHelper;
import android.camant.com.elibraryandroid.models.BaseModel;
import android.camant.com.elibraryandroid.models.Category;
import android.camant.com.elibraryandroid.srv.ELibraryScheduleReceiver;
import android.camant.com.elibraryandroid.utils.MainUtil;
import android.camant.com.elibraryandroid.views.FancyRecyclerView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends BaseActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private FancyRecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categories = new ArrayList<>();
    private SearchView searchView;
    private String searchKey;
    private boolean synced = false;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ELibraryScheduleReceiver.ACTION_SYNC_COMPLETE)){
                loadCategories(true);
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        activityTitle.setPadding(MainUtil.dp2px(this, 30), 0, 0, 0);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendBroadcast(new Intent(ELibraryScheduleReceiver.ACTION_SYNC_NOW));
                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadCategories(true);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        swipeRefreshLayout.setRefreshing(true);
        recyclerView = (FancyRecyclerView) findViewById(R.id.recyclerView);
        categoryAdapter = new CategoryAdapter(categories);
        categoryAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseModel category, int position) {
                Intent intent = new Intent(CategoriesActivity.this, BooksListActivity.class);
                intent.putExtra("category_id", category.getId());
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager( new LinearLayoutManager(this));
        recyclerView.setAdapter(categoryAdapter);
        recyclerView.setOnScrollBottomReachedListener(new FancyRecyclerView.OnScrollBottomReachedListener() {
            @Override
            public void onScrollBottomReached() {
                loadCategories(false);
            }
        });

        loadCategories(true);
        if(categories.size()>0){
            swipeRefreshLayout.setRefreshing(false);
            categoryAdapter.notifyDataSetChanged();
        }
        sendBroadcast(new Intent(ELibraryScheduleReceiver.ACTION_SYNC_NOW));
    }
    private void loadCategories(boolean isCleared){
        if(isCleared) categories.clear();
        CategoryDbHelper categoryDbHelper = new CategoryDbHelper(this);
        categories.addAll(categoryDbHelper.listCategories(searchKey, categories.size(), 20));
        categoryAdapter.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_books_list, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) menuItem.getActionView();
        if(searchView == null) return true;
        AutoCompleteTextView actv = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        if(actv != null){
            actv.setThreshold(1);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchKey = s;
                loadCategories(true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchKey = s;
                loadCategories(true);
                return true;
            }
        });
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(ELibraryScheduleReceiver.ACTION_SYNC_COMPLETE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}
