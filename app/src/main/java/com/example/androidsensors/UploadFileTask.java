package com.example.androidsensors;

import android.os.AsyncTask;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.io.File;
import java.io.FileInputStream;

import static com.example.androidsensors.DataActivity.storageConnectionString;
import static com.example.androidsensors.DataActivity.storageContainer;

public class UploadFileTask extends AsyncTask<String, Integer, Long> {

    @Override
    protected Long doInBackground(String... strings) {
        storeImageInBlobStorage(strings[0], strings[1]);
        return null;
    }

    protected void onPostExecute(Long result) {
        System.out.println("File uploaded");
    }

    public void storeImageInBlobStorage(String filePath, String uniqueFileName){
        try
        {
            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

            // Create the blob client.
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

            // Retrieve reference to a previously created container.
            CloudBlobContainer container = blobClient.getContainerReference(storageContainer);

            // Create or overwrite the blob (with the name "example.jpeg") with contents from a local file.
            CloudBlockBlob blob = container.getBlockBlobReference(uniqueFileName);
            File source = new File(filePath);
            blob.upload(new FileInputStream(source), source.length());


        }
        catch (Exception e)
        {
            // Output the stack trace.
            e.printStackTrace();
        }
    }
}
