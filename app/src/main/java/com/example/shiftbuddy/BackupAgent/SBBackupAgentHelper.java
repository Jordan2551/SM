package com.example.shiftbuddy.BackupAgent;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FileBackupHelper;
import android.content.Context;

import com.example.shiftbuddy.Database.Connector.DBConnector;
import com.example.shiftbuddy.Database.DataSource;
import com.example.shiftbuddy.MainActivity;

import android.app.backup.BackupManager;
import android.os.ParcelFileDescriptor;

import java.io.IOException;

import static android.app.backup.BackupManager.dataChanged;


/**
 * Created by jorda_000 on 8/26/2016.
 */
public class SBBackupAgentHelper extends BackupAgentHelper {

    // Allocate a helper and add it to the backup agent
    @Override
    public void onCreate() {

        FileBackupHelper helper = new FileBackupHelper(this,
                "../databases/" + DBConnector.DB_NAME);
        addHelper("shifts", helper);
    }
/*
    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data,
                         ParcelFileDescriptor newState) throws IOException {
        // Hold the lock while the FileBackupHelper performs backup
        synchronized (DBConnector.sDataLock) {
            super.onBackup(oldState, data, newState);
        }
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode,
                          ParcelFileDescriptor newState) throws IOException {
        // Hold the lock while the FileBackupHelper restores the file
        synchronized (DBConnector.sDataLock) {
            super.onRestore(data, appVersionCode, newState);
        }
    }
*/
}




