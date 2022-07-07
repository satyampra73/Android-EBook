package com.example.androidebookapp.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.androidebookapp.R;
import com.example.androidebookapp.database.DatabaseHandler;
import com.example.androidebookapp.util.BannerAds;
import com.example.androidebookapp.util.Method;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;
import java.io.IOException;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import io.github.lizhangqu.coreprogress.ProgressHelper;
import io.github.lizhangqu.coreprogress.ProgressUIListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class PDFShow extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener, OnPageErrorListener {

    private Method method;
    public MaterialToolbar toolbar;
    private PDFView pdfView;
    private DatabaseHandler db;
    private String id, uri, type;
    private OkHttpClient client;
    private Dialog dialog;
    private MaterialTextView textViewCancel;
    private static final String CANCEL_TAG = "c_tag_pdf";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview);

        method = new Method(PDFShow.this);
        method.forceRTLIfSupported();

        db = new DatabaseHandler(PDFShow.this);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        uri = intent.getStringExtra("link");
        String toolbarTitle = intent.getStringExtra("toolbarTitle");
        type = intent.getStringExtra("type");

        dialog = new Dialog(PDFShow.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progressbar_custom);
        dialog.setCancelable(false);

        if (method.isRtl()) {
            dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        dialog.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        textViewCancel = dialog.findViewById(R.id.textView_cancel_progressBar_custom);
        dialog.show();

        toolbar = findViewById(R.id.toolbar_pdfView);
        toolbar.setTitle(toolbarTitle);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        LinearLayout linearLayout = findViewById(R.id.linearLayout_pdfView);
        BannerAds.showBannerAds(PDFShow.this,linearLayout);

        pdfView = findViewById(R.id.pdfView_activity);
        pdfView.setBackgroundColor(Color.LTGRAY);

        if (type.equals("link")) {
            if (!db.checkIdPdfBook(id)) {
                pdfFile(uri, id, db.getPdf(id));
            } else {
                pdfFile(uri, id, 0);
                db.addPdf(id, 0);
            }
        } else {
            File file = new File(uri);
            if (!db.checkIdPdfBook(id)) {
                displayFromFile(file, db.getPdf(id));
            } else {
                displayFromFile(file, 0);
                db.addPdf(id, 0);
            }
        }

    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        db.updatePdf(id, page);
    }

    @Override
    public void loadComplete(int nbPages) {
        dialog.dismiss();
    }

    @Override
    public void onPageError(int page, Throwable t) {
        dialog.dismiss();
        method.alertBox(getResources().getString(R.string.wrong));
    }

    public void pdfFile(String path, String bookId, int pageNumber) {

        try {

            String filePath = getExternalCacheDir().getAbsolutePath();
            String fileName = "file" + bookId + ".pdf";
            File file = new File(filePath, fileName);

            textViewCancel.setOnClickListener(v -> {
                file.delete();
                dialog.dismiss();
                if (client != null) {
                    for (Call call1 : client.dispatcher().queuedCalls()) {
                        if (call1.request().tag().equals(CANCEL_TAG))
                            call1.cancel();
                    }
                    for (Call call1 : client.dispatcher().runningCalls()) {
                        if (call1.request().tag().equals(CANCEL_TAG))
                            call1.cancel();
                    }
                }
                finish();
            });


            if (file.exists()) {
                displayFromFile(file, pageNumber);
            } else {
                client = new OkHttpClient();
                Request.Builder builder = new Request.Builder()
                        .url(path)
                        .addHeader("Accept-Encoding", "identity")
                        .get()
                        .tag(CANCEL_TAG);

                Call call = client.newCall(builder.build());
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e("TAG", "=============onFailure===============");
                        e.printStackTrace();
                        Log.d("error_downloading", e.toString());
                        dialog.dismiss();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        Log.e("TAG", "=============onResponse===============");
                        Log.e("TAG", "request headers:" + response.request().headers());
                        Log.e("TAG", "response headers:" + response.headers());
                        assert response.body() != null;
                        ResponseBody responseBody = ProgressHelper.withProgress(response.body(), new ProgressUIListener() {

                            //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
                            @Override
                            public void onUIProgressStart(long totalBytes) {
                                super.onUIProgressStart(totalBytes);
                                Log.e("TAG", "onUIProgressStart:" + totalBytes);
                            }

                            @Override
                            public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                                Log.e("TAG", "=============start===============");
                                Log.e("TAG", "numBytes:" + numBytes);
                                Log.e("TAG", "totalBytes:" + totalBytes);
                                Log.e("TAG", "percent:" + percent);
                                Log.e("TAG", "speed:" + speed);
                                Log.e("TAG", "============= end ===============");
                            }

                            //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
                            @Override
                            public void onUIProgressFinish() {
                                super.onUIProgressFinish();
                                Log.e("TAG", "onUIProgressFinish:");
                                dialog.dismiss();
                                displayFromFile(file, pageNumber);
                            }
                        });

                        try {

                            BufferedSource source = responseBody.source();
                            File outFile = new File(filePath + "/" + fileName);
                            BufferedSink sink = Okio.buffer(Okio.sink(outFile));
                            source.readAll(sink);
                            sink.flush();
                            source.close();

                        } catch (Exception e) {
                            Log.d("show_data", e.toString());
                        }
                    }
                });

            }

        } catch (Exception e) {
            if (dialog != null) {
                dialog.dismiss();
            }
            Log.d("exception_error", e.toString());
            method.alertBox(getResources().getString(R.string.failed_try_again));
        }

    }

    public void displayFromFile(File file, int pagePosition) {
        pdfView.fromFile(file)
                .defaultPage(pagePosition)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError(this)
                .load();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
