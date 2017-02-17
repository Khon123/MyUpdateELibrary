package android.camant.com.elibraryandroid.activities;

import android.app.SearchManager;
import android.camant.com.elibraryandroid.Constants;
import android.camant.com.elibraryandroid.R;
import android.camant.com.elibraryandroid.adapters.BookAdapter;
import android.camant.com.elibraryandroid.adapters.BookCursorAdapter;
import android.camant.com.elibraryandroid.adapters.BookListAdapter;
import android.camant.com.elibraryandroid.adapters.OnItemClickListener;
import android.camant.com.elibraryandroid.db.BaseDbHelper;
import android.camant.com.elibraryandroid.db.BookDbHelper;
import android.camant.com.elibraryandroid.db.CategoryDbHelper;
import android.camant.com.elibraryandroid.fragments.BooksFragment;
import android.camant.com.elibraryandroid.models.BaseModel;
import android.camant.com.elibraryandroid.models.Book;
import android.camant.com.elibraryandroid.models.Category;
import android.camant.com.elibraryandroid.srv.DownloadAsyncTask;
import android.camant.com.elibraryandroid.srv.ELibraryScheduleReceiver;
import android.camant.com.elibraryandroid.utils.MainUtil;
import android.camant.com.elibraryandroid.views.FancyListView;
import android.camant.com.elibraryandroid.views.FancyRecyclerView;
import android.camant.com.elibraryandroid.views.SpacesItemDecoration;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.BaseColumns;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TabHost;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BooksListActivity extends BaseActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private FancyRecyclerView recyclerView;
    private ArrayList<Book> books = new ArrayList<>();
    private ArrayList<Book> searchBooks = new ArrayList<>();
    private BookAdapter bookAdapter;
    private Category category;
    private BookCursorAdapter bookCursorAdapter;
    private BookDbHelper bookDbHelper;
    private PopupWindow popupWindow;
    private BookListAdapter bookListAdapter;
    private SearchView searchView;
    private String searchKey;
    private MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
    private IntentFilter intentFilter;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            final Book book = (Book) msg.obj;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(BooksListActivity.this, ""+book.getId(), Toast.LENGTH_SHORT).show();
                    startReadingBookActivity(book);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_list);
        intentFilter = new IntentFilter(Constants.ACTION_DOWNLOAD_UPDATE);
        intentFilter.addAction(Constants.ACTION_DOWNLOAD_COMPLETE);
        if(getIntent() != null && getIntent().hasExtra("category_id")) {
            CategoryDbHelper categoryDbHelper = new CategoryDbHelper(this);
            category = categoryDbHelper.getById(getIntent().getIntExtra("category_id", 0));
            if(category != null && activityTitle != null){
                activityTitle.setText(category.getName());
            }else{
                activityTitle.setText(getString(R.string.downloaded));
            }
        }
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //activityBack.setText(getString(R.string.library));
        }
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendBroadcast(new Intent(ELibraryScheduleReceiver.ACTION_SYNC_NOW));
                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadBooks(true);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        recyclerView = (FancyRecyclerView) findViewById(R.id.recyclerView);
        bookAdapter = new BookAdapter(books);
        bookAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final BaseModel book, int position) {
                Message message = handler.obtainMessage();
                message.obj = book;
                handler.sendMessage(message);
            }
        });
        GridLayoutManager layout = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(bookAdapter);
        recyclerView.setOnScrollBottomReachedListener(new FancyRecyclerView.OnScrollBottomReachedListener() {
            @Override
            public void onScrollBottomReached() {
                loadBooks(false);
            }
        });
        recyclerView.addItemDecoration(new SpacesItemDecoration(getResources().getDimensionPixelSize(R.dimen.activity_default_h_margin)));

        if(bookDbHelper == null) bookDbHelper = new BookDbHelper(this);

        bookListAdapter = new BookListAdapter(this, searchBooks);
        View v = getLayoutInflater().inflate(R.layout.suggestions_layout, null);
        popupWindow = new PopupWindow(v, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.RED));
        popupWindow.setOutsideTouchable(true); popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int h = 0;
                if(getSupportActionBar()!= null) h = popupWindow.getContentView().getMeasuredHeight();
                if(event.getY() > h) {
                    popupWindow.dismiss();
                    return true;
                }else{
                    popupWindow.setFocusable(true);
                    popupWindow.update();
                }
                return false;
            }
        });
        FancyListView listView = (FancyListView)v.findViewById(R.id.lstBooks);
        listView.setAdapter(bookListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                startReadingBookActivity(searchBooks.get(position));
                popupWindow.dismiss();
            }
        });
        listView.setOnScrollBottomReachedListener(new FancyListView.OnScrollBottomReachedListener() {
            @Override
            public void onScrollBottomReached() {
                suggestBooks(searchKey,false);
            }
        });
        loadBooks(true);
        //buildTabs();
    }

    /*private void buildTabs() {
        TabHost tabHost = (TabHost)findViewById(R.id.tabHost);

        TabHost.TabSpec tab1 = tabHost.newTabSpec("First Tab");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Second Tab");
        TabHost.TabSpec tab3 = tabHost.newTabSpec("Third tab");
        tab1.setIndicator("Tab1");
        tab1.setContent(new Intent(this, BooksFragment.class));

        tab2.setIndicator("Tab2");
        tab2.setContent(new Intent(this, BooksFragment.class));

        tab3.setIndicator("Tab3");
        tab3.setContent(new Intent(this, BooksFragment.class));

        *//** Add the tabs  to the TabHost to display. *//*
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);
    }*/

    private void startReadingBookActivity(final Book book) {
        if(bookDbHelper == null) bookDbHelper = new BookDbHelper(this);
        if(book.getBook_file_local() == null || MainUtil.getBookFileLocalFullPath(book.getBook_file_local()) == null){
            if(book.getDownload_id() > 0){
                //book is being download
                Toast.makeText(this, getString(R.string.downloading_book_file), Toast.LENGTH_SHORT).show();
                return;
            }
            //download book file
            if(MainUtil.isConnected(this)) {
                DownloadAsyncTask downloadAsyncTask = MainUtil.asyncDownload(this, book, null);
                if(downloadAsyncTask == null){
                    Toast.makeText(this, getString(R.string.download_failed), Toast.LENGTH_LONG).show();
                    return;
                }
                if(downloadAsyncTask.getProgress() == 100){
                    //file is previously downloaded but have in db
                    //simply record it in db
                    book.setProgress(100);
                    book.setBook_file_local(downloadAsyncTask.getDestinationFile());
                    book.setDownload_id(downloadAsyncTask.getDownloadManagerId());
                    bookDbHelper.insertOrUpdate(book);
                }else {
                    //Log.d("BooksList", ""+b);
                    //Log.d("BooksList", ""+downloadAsyncTask);
                    book.setDownload_id(downloadAsyncTask.getDownloadManagerId());
                    bookDbHelper.insertOrUpdate(book);
                    Toast.makeText(this, getString(R.string.downloading_book_file), Toast.LENGTH_SHORT).show();
                    return;
                }
            }else{
                Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                return ;
            }
        }

        Intent intent = new Intent(BooksListActivity.this, MuPDFActivity.class);
        intent.putExtra("book_id", book.getId());
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra("title", book.getTitle());
        intent.setData(Uri.parse(BaseDbHelper.BOOK_FILES_PATH+book.getBook_file_local()));
        startActivity(intent);
    }

    private void loadBooks(boolean isCleared){
        if(isCleared) books.clear();
        BookDbHelper bookDbHelper = new BookDbHelper(this);
        List<Book> list;
        if(category != null) {
            list = bookDbHelper.listBooks(category.getId(), books.size(), 20);
        }else {
            list = bookDbHelper.listBooks(null, books.size(), 20);
        }
        for(Book b:list) {
            books.add(b);
        }
        Log.d("Books", ""+books.size());
        bookAdapter.notifyDataSetChanged();
    }
    private void suggestBooks(String key, boolean isCleared){
        popupWindow.setFocusable(false);
        if(isCleared) searchBooks.clear();
        searchBooks.addAll(bookDbHelper.filterBooks(category.getId(), key, searchBooks.size()));
        bookListAdapter.notifyDataSetChanged();
/*searchView.postDelayed(new Runnable() {
    @Override
    public void run() {
        popupWindow.setFocusable(true);
        popupWindow.update();
    }
}, 500);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_books_list, menu);
        /*SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Tells your app's SearchView to use this activity's searchable configuration
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);*/
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) menuItem.getActionView();
        bookDbHelper = new BookDbHelper(this);
        bookCursorAdapter = new BookCursorAdapter(this, bookDbHelper.listAllBooks(category.getId(), null,0));
        Log.d(getClass().getSimpleName(), ""+searchView);
        if(searchView == null) return true;
        AutoCompleteTextView actv = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        if(actv != null){
            actv.setThreshold(1);
        }
        /*searchView.setSuggestionsAdapter(bookCursorAdapter);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {
                // Your code here
                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                // Your code here
                return true;
            }
        });*/
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchKey = s;
                suggestBooks(s, true);
                if(!popupWindow.isShowing()){
                    int h;
                    Rect rectangle = new Rect();
                    Window window = getWindow();
                    window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
                    h = rectangle.top + 2;
                    if(getSupportActionBar() != null){
                        h += getSupportActionBar().getHeight();
                    }
                    popupWindow.setWidth(rectangle.width());
                    popupWindow.showAtLocation(searchView, Gravity.TOP, 0, h);
                    //popupWindow.showAsDropDown(searchView);
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateAdapter(String query) {
        bookCursorAdapter.changeCursor(bookDbHelper.listAllBooks(category.getId(), query,0));
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myBroadcastReceiver);
    }
    public Book findBookInBooksListBy(int bookId){
        for (Book b:books){
            if(b.getId() == bookId) return b;
        }
        return null;
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            long downloadId = intent.getLongExtra("downloadManagerId", 0L);
            int bookId = intent.getIntExtra("bookId", 0);
            Book b = findBookInBooksListBy(bookId);

            if(intent.getAction().equals(Constants.ACTION_DOWNLOAD_UPDATE)){
                int progress = intent.getIntExtra("progress", 0);
                b.setDownload_id(downloadId);
                b.setProgress(progress);
                bookDbHelper.insertOrUpdate(b);
            }else if(intent.getAction().equals(Constants.ACTION_DOWNLOAD_COMPLETE)){
                boolean progress = intent.getBooleanExtra("progress", false);
                if(progress) {
                    b.setProgress(100);
                    b.setDownload_id(downloadId);
                    b.setBook_file_local(intent.getStringExtra("book_file_local"));
                    bookDbHelper.insertOrUpdate(b);
                    startReadingBookActivity(b);
                }else{
                    b.setDownload_id(0);
                }
            }
            bookAdapter.notifyDataSetChanged();
        }
    }
}
