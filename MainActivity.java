package com.example.eyebrowser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ProgressBar eyeProgressBar;
    ImageView eyeImageView;
    WebView eyeWebview;
    LinearLayout eyeLinearLayout;
    SwipeRefreshLayout eyeSwipeLayout;
    String myCurrentUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        eyeSwipeLayout = findViewById(R.id.mySwipeLayout);
        eyeLinearLayout = findViewById(R.id.myLinearLayout);
        eyeProgressBar = findViewById(R.id.myProgressBar);
        eyeImageView = findViewById(R.id.myImageView);
        eyeWebview = findViewById(R.id.myWebView);

        eyeProgressBar.setMax(100);

        eyeWebview.loadUrl("https://www.google.com");
        eyeWebview.getSettings().setJavaScriptEnabled(true);
        eyeWebview.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                eyeLinearLayout.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                eyeLinearLayout.setVisibility(View.GONE);
                eyeSwipeLayout.setRefreshing(false);
                super.onPageFinished(view, url);
                myCurrentUrl = url;
            }
        });
        eyeWebview.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                eyeProgressBar.setProgress(newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                getSupportActionBar().setTitle(title);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                eyeImageView.setImageBitmap(icon);
            }
        });

        eyeWebview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String s, String s1, String s2, String s3, long l) {
                DownloadManager.Request myRequest = new DownloadManager.Request(Uri.parse(s));
                myRequest.allowScanningByMediaScanner();
                myRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                DownloadManager myManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                myManager.enqueue(myRequest);

                Toast.makeText(MainActivity.this, "your file is downloading...", Toast.LENGTH_SHORT).show();

            }
        });

        eyeSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                eyeWebview.reload();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.eye_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected( @NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.menu_back:
                onBackPressed();
                break;

            case R.id.menu_forward:
                onForwardPressed();
                break;

            case R.id.menu_home:
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                eyeWebview.loadUrl("https://google.com");

                return true;

            case R.id.menu_refresh:
                eyeWebview.reload();
                break;
            case R.id.menu_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,myCurrentUrl);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Copied URL");
                startActivity(Intent.createChooser(shareIntent, "Share URL Using..."));

        }
        return super.onOptionsItemSelected(item);
    }

    private void onForwardPressed(){
        if (eyeWebview.canGoForward()){
            eyeWebview.goForward();
        } else {
            Toast.makeText(this, "can't go further", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        if (eyeWebview.canGoBack()){
            eyeWebview.goBack();
        }
        else {
            finish();
        }
    }
}