package hackthenorth.app.doggles;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    // TODO: Move this elsewhere + add real image file.
    private class ImageRecognitionService extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
            service.setApiKey("f3225891c60c0abe5a2424102fdf772d1d9955d4");
            ClassifyImagesOptions options = new ClassifyImagesOptions.Builder()
                    .images(new File(mCurrentPhotoPath))
                    .build();
            VisualClassification result = service.classify(options).execute();
            parseResponse(result.toString());
            //System.out.println(result.toString());
            return null;
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

                new ImageRecognitionService().execute();
            }
        }
    }

    public void parseResponse(String response) {
        try {
            JSONObject resultJson = new JSONObject(response);
            JSONArray images = resultJson.getJSONArray("images");
            for (int k = 0; k < images.length(); k++) {
                JSONObject image = images.getJSONObject(k);
                JSONArray classifiers = image.getJSONArray("classifiers");
                for (int i = 0; i < classifiers.length(); i++) {
                    JSONObject classifiersJSONObject = classifiers.getJSONObject(i);
                    JSONArray classes = classifiersJSONObject.getJSONArray("classes");
                    for (int j = 0; j < classes.length(); j++) {
                        JSONObject classObject = classes.getJSONObject(j);
                        if (classObject.getString("class").contains("dog")) {
                            Log.d("RESULT: ", classObject.getString("class"));
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
