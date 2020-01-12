package RAC.racontroller.API;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class GetAsync extends AsyncTask<JSONObject, Void, Boolean> {

    protected Boolean doInBackground(JSONObject... data) {
        OkHttpClient client = APIClient.getInstance().getClient();
        return true;
    }

}
