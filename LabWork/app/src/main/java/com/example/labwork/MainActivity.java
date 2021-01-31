package com.example.labwork;

import MyException.*;
import Publication.PublicationClass;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayout;


public class MainActivity extends AppCompatActivity {
    private ImageView _imageView;
    private TextView _textView;
    private TabLayout _tabLayout;
    private List<PublicationClass> _listViewedPublications = new ArrayList<PublicationClass>();
    private int _indexCurrentPublications = -1;
    private CircularProgressDrawable _circularProgressDrawable = null;
    private String _startUrl = "https://developerslife.ru/";
    private String _category = "latest/";
    private boolean _isEmpty = true;
    private boolean _isFirstLoad = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (_isFirstLoad) {
            _circularProgressDrawable = initCircularProgressDrawable(50f, 100f);
            _imageView = findViewById(R.id.imageView);
            _textView = findViewById(R.id.textView);
            _tabLayout = findViewById(R.id.tablayout);
            String urlAdress = _startUrl + _category;

            try {
                if (!hasConnection(this)) {
                    throw new ErrorNetworkException("Error connection");
                }
                drawPublication(urlAdress);
            } catch (ErrorNetworkException e) {
                showErrorPublication(R.drawable.ic_error_network, "Отсутствует интернет соединение");
                _isEmpty = true;
            }
            _isFirstLoad = false;
        }
    }

    private void showErrorPublication(int icon, String msgDescriptoion) {
        Glide
                .with(this)
                .load(icon)
                .into(_imageView);
        _textView.setText(msgDescriptoion);
    }

    private CircularProgressDrawable initCircularProgressDrawable(float width, float redius) {
        CircularProgressDrawable circularProgress = new CircularProgressDrawable(this);
        circularProgress.setStrokeWidth(width);
        circularProgress.setCenterRadius(redius);
        circularProgress.start();
        return circularProgress;
    }

    private void drawPublication(String urlAdress) {
        PublicationClass publication = new PublicationClass(urlAdress);
        try {
            publication.loadPublication();
            if (publication.getUrl().equals("")) {
                throw new EmptyCategoryException("Empty category");
            }
            _listViewedPublications.add(publication);
            _indexCurrentPublications++;
            showPublication(publication);
        } catch (EmptyCategoryException e) {
            showErrorPublication(R.drawable.ic_empty_category, "Категория еще пуста");
            _isEmpty = true;
        }
    }

    private void showPublication(PublicationClass publication) {
        Glide
                .with(this)
                .load(publication.getUrl())
                .apply(new RequestOptions()
                        .placeholder(_circularProgressDrawable)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                )
                .into(_imageView);
        _textView.setText(publication.getDescription());
    }


    public void btRefresh(View view) throws ErrorCategoryException {
        _isEmpty = false;
        if (_indexCurrentPublications == _listViewedPublications.size() - 1) {
            drawNewPublication();
        } else {
            //Загрузка поста из кэша
            drawPublicationFromCache();
            _indexCurrentPublications++;
        }
        if (_listViewedPublications.size() != 1) {
            setButtonEnabled(R.id.button2, true);
        }
    }

    private void drawPublicationFromCache() {
        PublicationClass currentPublication = _listViewedPublications.get((_indexCurrentPublications + 1));
        showPublication(currentPublication);
    }

    private void drawNewPublication() throws ErrorCategoryException {
        _category = getCategory(_tabLayout);
        String urlAdress = _startUrl + _category;
        try {
            if (!hasConnection(this)) {
                throw new ErrorNetworkException("Error connection");
            }
            drawPublication(urlAdress);
        } catch (ErrorNetworkException e) {
            showErrorPublication(R.drawable.ic_error_network, "Отсутствует интернет соединение");
            _isEmpty = true;
        }
    }

    private String getCategory(TabLayout tabLayout) throws ErrorCategoryException {
        switch (tabLayout.getSelectedTabPosition()) {
            case 0:
                return "latest/";
            case 1:
                return "top/";
            case 2:
                return "hot/";
            default:
                throw new ErrorCategoryException("Could not find student with ID " + tabLayout.getSelectedTabPosition());
        }
    }

    public void btBack(View view) {
        if (!_isEmpty) {
            _indexCurrentPublications--;
        }
        PublicationClass currentPublication = _listViewedPublications.get((_indexCurrentPublications));
        _isEmpty = false;
        showPublication(currentPublication);
        if (_indexCurrentPublications == 0) {
            setButtonEnabled(R.id.button2, false);
        }
    }

    private void setButtonEnabled(int idButton, boolean state) {
        Button b = (Button) findViewById(idButton);
        b.setEnabled(state);
    }

    private static boolean hasConnection(final Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
