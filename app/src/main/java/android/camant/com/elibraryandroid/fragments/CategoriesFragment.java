package android.camant.com.elibraryandroid.fragments;

import android.camant.com.elibraryandroid.R;
import android.camant.com.elibraryandroid.activities.BaseActivity;
import android.camant.com.elibraryandroid.activities.BooksListActivity;
import android.camant.com.elibraryandroid.adapters.CategoryAdapter;
import android.camant.com.elibraryandroid.adapters.OnItemClickListener;
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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends BaseFragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private FancyRecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categories = new ArrayList<>();
    private SearchView searchView;
    private String searchKey;
    private boolean synced = false;
    private boolean downloaded = false;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_categories, container, false);
        if(getArguments() != null && getArguments().containsKey("downloaded")){
            downloaded = getArguments().getBoolean("downloaded", false);
        }
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
                BooksListFragment booksListFragment = new BooksListFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("category_id", category.getId());
                if(downloaded){
                    bundle.putBoolean("downloaded", downloaded);
                    bundle.putInt("title", R.string.downloaded);
                }else {
                    bundle.putInt("title", R.string.library);
                }
                booksListFragment.setArguments(bundle);
                startFragment(booksListFragment, "BooksList");
                //getChildFragmentManager().beginTransaction().addToBackStack("BooksListFragment").replace(R.id.container, booksListFragment).commit();
                /*Intent intent = new Intent(getContext(), BooksListActivity.class);
                intent.putExtra("category_id", category.getId());
                startActivity(intent);*/
            }
        });
        recyclerView.setLayoutManager( new LinearLayoutManager(getContext()));
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
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(activityTitle != null) {
            activityTitle.setPadding(MainUtil.dp2px(getContext(), 30), 0, 0, 0);
            if(getArguments() != null && getArguments().containsKey("title")){
                activityTitle.setText(getString(getArguments().getInt("title")));
            }
        }
    }

    private void loadCategories(boolean isCleared){
        if(isCleared) categories.clear();
        CategoryDbHelper categoryDbHelper = new CategoryDbHelper(getContext());
        if(!downloaded) {
            categories.addAll(categoryDbHelper.listCategories(searchKey, categories.size(), 20));
        }else{
            categories.addAll(categoryDbHelper.listDownloadedCategories(searchKey, categories.size(), 20));
        }
        categoryAdapter.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_books_list, menu);
        return true;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) menuItem.getActionView();
        if(searchView == null) return ;
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
        return ;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(ELibraryScheduleReceiver.ACTION_SYNC_COMPLETE));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}
