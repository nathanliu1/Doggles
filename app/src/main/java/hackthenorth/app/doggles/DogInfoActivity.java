package hackthenorth.app.doggles;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zero_ on 9/16/2017.
 */

public class DogInfoActivity extends AppCompatActivity {


    Uri mPhotoUri;

    @BindView(R.id.image)
    ImageView toolBarImage;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_info);
        ButterKnife.bind(this);

        mPhotoUri = getIntent().getParcelableExtra("imageUri");


        collapsingToolbarLayout.setTitle("DOOGIE");
        collapsingToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.white));
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, R.color.white));


        Picasso.with(getApplicationContext()).load(mPhotoUri).rotate(90).fit().into(toolBarImage);
    }
}
