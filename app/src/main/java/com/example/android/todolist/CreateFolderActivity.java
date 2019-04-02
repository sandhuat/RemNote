package com.example.android.todolist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateFolderActivity extends BaseDemoActivity
{
    DriveId id;
    boolean isFound;String title,content;
    MetadataChangeSet changeSet=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i=getIntent();
        title=i.getStringExtra("title");
        Log.e("TAG","RUN 1");
        content=i.getStringExtra("content");

    }

    @Override
    public void onConnected(Bundle connectionHint)
    {
        super.onConnected(connectionHint);
        Query query = new Query.Builder()
                .addFilter(Filters.and(Filters.eq(
                                SearchableField.TITLE, "NoteIT folder"),
                        Filters.eq(SearchableField.TRASHED, false)))
                .build();
        Log.e("TAG","RUN 2");

        //  DriveApi.MetadataBufferResult result = Drive.DriveApi.query(getGoogleApiClient(), query).await();
        Drive.DriveApi.query(getGoogleApiClient(), query).setResultCallback(metadataCallback);


    }
    final private ResultCallback<DriveApi.MetadataBufferResult> metadataCallback = new
            ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Problem while retrieving results");
                        Log.e("TAG","RUN 2.5");

                        return;
                    }
                    else
                    {
                        Log.e("TAG","RUN 3");

                        if (!result.getStatus().isSuccess()) {
                            showMessage("Cannot create folder in the root.");
                        } else {
                            isFound = false;
                            for (Metadata m : result.getMetadataBuffer()) {
                                if (m.getTitle().equals("NoteIT folder")) {
                                    id=m.getDriveId();
                                    Log.e("TAG","RUN 4");

                                    //   showMessage("NoteIT Folder exists in the Drive");
                                    isFound = true;
                                    break;
                                }
                            }

                            if (!isFound)
                            {
                              //  showMessage("Folder not found; creating it.");
                                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                        .setTitle("NoteIT folder")
                                        .build();
                                Log.e("TAG","RUN 5");

                                Drive.DriveApi.getRootFolder(getGoogleApiClient()).createFolder(
                                        getGoogleApiClient(), changeSet).setResultCallback(folderCreatedCallback);

                                //   Drive.DriveApi.getRootFolder(getGoogleApiClient())
                                //         .createFolder(getGoogleApiClient(), changeSet).await();

                                if (!result.getStatus().isSuccess()) {
                                    showMessage("Error while trying to create the folder");
                                } else {
                                    Log.e("TAG","RUN 5.5");

                                    showMessage("Created the folder - NoteIT");
                                }


                            }
                            else
                            {
                                Log.e("TAG","RUN 6");

                                Drive.DriveApi.newDriveContents(getGoogleApiClient()).setResultCallback(driveContentsCallback);

                                // If folder is found

                            }

                        }
                    }

                }
            };

    ResultCallback<DriveFolder.DriveFolderResult> folderCreatedCallback = new
            ResultCallback<DriveFolder.DriveFolderResult>() {
                @Override
                public void onResult(DriveFolder.DriveFolderResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create the folder");
                        return;
                    }
                    id=result.getDriveFolder().getDriveId();
                   // showMessage("Created a folder: " + result.getDriveFolder().getDriveId());
                    showMessage("Created the folder - NoteIT");
                    Log.e("TAG","RUN 7");

                    Drive.DriveApi.newDriveContents(getGoogleApiClient()).setResultCallback(driveContentsCallback);
                }
            };
    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result)
                {
                    if (!result.getStatus().isSuccess())
                    {
                        showMessage("Error while trying to create new file contents");
                        return;
                    }
                    DriveFolder folder = id.asDriveFolder();
                    Date date1=new Date();
                    String cx1;
                    SimpleDateFormat format1 = new SimpleDateFormat("dd MMM yyyy");
                    cx1 = format1.format(date1);
                    Log.e("TAG","RUN 8");

                    if(content.equals("MUx76cc^g.h"))
                    {
                        changeSet = new MetadataChangeSet.Builder()
                                .setTitle(title + " - " + cx1+".3gp")
                                .setMimeType("audio/3gpp")
                                .setStarred(true).build();
                      //  folder.createFile(getGoogleApiClient(), changeSet, result.getDriveContents())
                        //        .setResultCallback(audiofileCallback);
                        saveAudioToDrive();
                    }
                    else {
                        Log.e("TAG","RUN 9");

                        changeSet = new MetadataChangeSet.Builder()
                                .setTitle(title + " - " + cx1+".txt")
                                .setMimeType("text/plain")
                                .setStarred(true).build();
                        folder.createFile(getGoogleApiClient(), changeSet, result.getDriveContents())
                                .setResultCallback(fileCallback);
                    }


                }
            };

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback =
            new ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create the file");
                        return;
                    }
                    id=result.getDriveFile().getDriveId();
                    DriveFile file = id.asDriveFile();

                    new EditContentsAsyncTask(CreateFolderActivity.this).execute(file);

                    Log.e("TAG","RUN 10");


                  //  showMessage("Created a file: " + result.getDriveFile().getDriveId());
                    showMessage("Created the file - "+title+ " in the Drive");
                }
            };

    public void saveAudioToDrive() {
        Drive.DriveApi.newDriveContents(getGoogleApiClient()).setResultCallback(
                new ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        if (!result.getStatus().isSuccess()) {
                            showMessage("Error while trying to create the file");
                            return;
                        }


                        File file_to_transfer = new File(Environment.getExternalStorageDirectory() + "/Todo/" + title + ".3gp");
                        OutputStream outputStream = result.getDriveContents().getOutputStream();
                        FileInputStream fis;
                        try {
                            fis = new FileInputStream(file_to_transfer.getPath());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            byte[] buf = new byte[1024];
                            int n;
                            while (-1 != (n = fis.read(buf)))
                                baos.write(buf, 0, n);
                            byte[] photoBytes = baos.toByteArray();
                            outputStream.write(photoBytes);

                            outputStream.close();
                            outputStream = null;
                            fis.close();
                            fis = null;

                        } catch (FileNotFoundException e)
                        {
                            Log.e("sfss", "FileNotFoundException: " + e.getMessage());
                        }
                        catch (IOException e1)
                        {
                            Log.e("sfss", "Unable to write file contents." + e1.getMessage());
                        }


                        Drive.DriveApi.getFolder(getGoogleApiClient(), id).createFile(getGoogleApiClient(),changeSet, result.getDriveContents());

                        //  showMessage("Created a file: " + result.getDriveFile().getDriveId());
                        showMessage("Created the file - " + title + " in the Drive");
                        finish();
                    }
                });
    }
    public class EditContentsAsyncTask extends ApiClientAsyncTask<DriveFile, Void, Boolean> {

        public EditContentsAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected Boolean doInBackgroundConnected(DriveFile... args) {
            DriveFile file = args[0];
            try {
                DriveApi.DriveContentsResult driveContentsResult = file.open(getGoogleApiClient(), DriveFile.MODE_WRITE_ONLY, null).await();
                if (!driveContentsResult.getStatus().isSuccess())
                {
                    return false;
                }
                DriveContents driveContents = driveContentsResult.getDriveContents();
                OutputStream outputStream = driveContents.getOutputStream();
                outputStream.write(content.getBytes());
                com.google.android.gms.common.api.Status status =
                        driveContents.commit(getGoogleApiClient(), null).await();
                return status.getStatus().isSuccess();
            } catch (IOException e)
            {
                //  Log.e(TAG, "IOException while appending to the output stream", e);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                showMessage("Error while editing contents");
                return;
            }
            Log.e("TAG","RUN 11");

            showMessage("Note Successfully backed up");
            finish();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        setVisible(true);
    }
}
