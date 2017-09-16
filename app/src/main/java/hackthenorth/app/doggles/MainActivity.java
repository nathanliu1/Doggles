package hackthenorth.app.doggles;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final int TAKE_PICTURE = 1;

    private String mCurrentPhotoPath;
    private Uri mPhotoURI;

    @BindView(R.id.dog_image)
    ImageView mainphoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = photoFile.getAbsolutePath();
        } catch (IOException ex) {
            Log.d("NO IMAGE FILE CREATED", ex.getMessage());
        }
        if (photoFile != null) {
            mPhotoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".doggles.app.provider", photoFile);

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoURI);
            startActivityForResult(takePictureIntent, TAKE_PICTURE);
        }
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File storageDir = Environment.getExternalStorageDirectory();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir    /* directory */
        );
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                mainphoto.setImageResource(R.drawable.loading_image);

                // Get the background, which has been compiled to an AnimationDrawable object.
                AnimationDrawable frameAnimation = (AnimationDrawable) mainphoto.getDrawable();

                // Start the animation (looped playback by default).
                frameAnimation.start();
            }
        }
    }
}
