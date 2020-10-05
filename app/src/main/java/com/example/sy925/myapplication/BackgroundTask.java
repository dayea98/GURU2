package com.example.sy925.myapplication;

import android.app.Dialog;
import android.os.AsyncTask;

public class BackgroundTask extends AsyncTask<Void,Void,Void> {


    private Dialog dialog;


    public BackgroundTask(Dialog dialog){
        this.dialog=dialog;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.show(); //다이얼로그 표시
    }
    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
