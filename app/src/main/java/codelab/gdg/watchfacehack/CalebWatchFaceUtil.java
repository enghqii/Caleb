package codelab.gdg.watchfacehack;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by hanter on 2015. 2. 15..
 */
public final class CalebWatchFaceUtil {
    public static final String PATH_WITH_FEATURE = "/watch_face_config/caleb";

    public static final String KEY_EXERCISE_GOAL_TYPE = "EXER_GOAL_TYPE";
    public static final String KEY_EXERCISE_STATE = "EXER_STATE";

    public interface FetchConfigDataMapCallback {
        void onConfigDataMapFetched(DataMap config);
    }

    private CalebWatchFaceUtil() { }
}
