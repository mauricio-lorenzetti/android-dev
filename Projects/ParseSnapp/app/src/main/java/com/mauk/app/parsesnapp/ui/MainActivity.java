package com.mauk.app.parsesnapp.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.mauk.app.parsesnapp.utils.ParseConstants;
import com.mauk.app.parsesnapp.R;
import com.mauk.app.parsesnapp.adapters.SectionsPagerAdapter;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TAKE_PHOTO_REQUEST = 0;
    private static final int TAKE_VIDEO_REQUEST = 1;
    private static final int PICK_PHOTO_REQUEST = 2;
    private static final int PICK_VIDEO_REQUEST = 3;

    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;
    private static final int FILE_SIZE_LIMIT = 1024 * 1024 * 10;

    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    protected Uri mediaUri;
    private DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0: //take pic
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                    if (mediaUri == null) {
                        Log.e(TAG, getString(R.string.error_external_storage));
                    } else {
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);
                        startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
                    }
                    break;
                case 1: //take vid
                    Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    mediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                    if (mediaUri == null) {
                        Log.e(TAG, getString(R.string.error_external_storage));
                    } else {
                        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);
                        videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                        videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0); //lowest quality
                        startActivityForResult(videoIntent, TAKE_VIDEO_REQUEST);
                    }
                    break;
                case 2: //choose pic
                    Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    choosePhotoIntent.setType("image/*");
                    startActivityForResult(choosePhotoIntent, PICK_PHOTO_REQUEST);
                    break;
                case 3: //choose vid
                    Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    chooseVideoIntent.setType("video/*");
                    Toast.makeText(MainActivity.this, R.string.warning_video_size, Toast.LENGTH_SHORT).show();
                    startActivityForResult(chooseVideoIntent, PICK_VIDEO_REQUEST);
                    break;
            }
        }

        private Uri getOutputMediaFileUri(int mediaType) {

            if (isExternalStorageAvailable()) {
                //get URI
                //get ext str dir
                File mediaStorageDir = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        MainActivity.this.getString(R.string.app_name));

                //create subdir
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        Log.e(TAG, getString(R.string.error_creating_directory));
                        return null;
                    }
                }

                //create filename
                //create file
                File mediaFile;
                String timestamp = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.US).format(new Date());

                String path = mediaStorageDir.getPath() + File.separator;

                if (mediaType == MEDIA_TYPE_IMAGE) {
                    mediaFile = new File(path + "IMG" + timestamp + ".jpg");
                } else if (mediaType == MEDIA_TYPE_VIDEO) {
                    mediaFile = new File(path + "VID" + timestamp + ".mp4");
                } else {
                    return null;
                }

                Log.d(TAG, "File: " + Uri.fromFile(mediaFile));

                //return file's uri
                return Uri.fromFile(mediaFile);
            } else {
                return null;
            }
        }

        private boolean isExternalStorageAvailable() {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progress_spinner);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.toolbar_focus_pressesd), PorterDuff.Mode.MULTIPLY);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.toolbar_icon);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) {
            Log.i(TAG, currentUser.getUsername());
        } else {
            navigateToLogin();
        }

        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_PHOTO_REQUEST || requestCode == PICK_VIDEO_REQUEST) {
                if (data == null) {
                    Toast.makeText(this, R.string.error_general, Toast.LENGTH_SHORT).show();
                } else {
                    mediaUri = data.getData();
                }
                Log.i(TAG, "Media URI: " + mediaUri);
                if (requestCode == PICK_VIDEO_REQUEST) {
                    int fileSize = 0;
                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream(mediaUri);
                        fileSize = inputStream.available();
                    } catch (FileNotFoundException e) {
                        Toast.makeText(this, R.string.error_file_open, Toast.LENGTH_SHORT).show();
                        return;
                    } catch (IOException e) {
                        Toast.makeText(this, R.string.error_file_open, Toast.LENGTH_SHORT).show();
                        return;
                    } finally {
                        try {
                            inputStream.close();
                        } catch (IOException e) {}
                    }
                    if (fileSize >= FILE_SIZE_LIMIT) {
                        Toast.makeText(this, R.string.error_file_size, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

            } else {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mediaUri);
                sendBroadcast(mediaScanIntent);
            }

            Intent recipientsIntent = new Intent(this, RecipientsActivity.class);
            recipientsIntent.setData(mediaUri);
            String fileType;
            if (requestCode == PICK_PHOTO_REQUEST || requestCode == TAKE_PHOTO_REQUEST) {
                fileType = ParseConstants.TYPE_IMAGE;
            } else {
                fileType = ParseConstants.TYPE_VIDEO;
            }
            recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE, fileType);
            startActivity(recipientsIntent);

        } else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, R.string.error_general, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_log_out) {
            ParseUser.logOut();
            navigateToLogin();
            return true;
        } else if (itemId == R.id.action_edit_friends) {
            Intent intent = new Intent(this, EditFriendsActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.action_exit) {
            System.exit(1);
        } else if (itemId == R.id.action_camera) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setItems(R.array.camera_choices, dialogListener);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

}
