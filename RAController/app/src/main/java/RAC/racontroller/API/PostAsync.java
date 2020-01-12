package RAC.racontroller.API;

import android.os.AsyncTask;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class PostAsync extends AsyncTask<String, Void, Boolean> {

    protected Boolean doInBackground(String... data) {
        OkHttpClient client = APIClient.getInstance().getClient();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, data[0]);
        Request request = new Request.Builder()
                .url("http://" + APIClient.getInstance().getIP() + "/api")
                .post(body)
                .addHeader("content-type", "application/json; charset=utf-8")
                .build();

        try {
            Response response = client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}

