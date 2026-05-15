package app.ecuamaps.background;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import app.ecuamaps.MwmApplication;
import app.ecuamaps.sdk.editor.Editor;
import app.ecuamaps.sdk.editor.OsmOAuth;
import app.ecuamaps.sdk.util.log.Logger;

public class OsmUploadWork extends Worker
{
  private static final String TAG = OsmUploadWork.class.getSimpleName();
  private final Context mContext;
  private final WorkerParameters mWorkerParameters;

  public OsmUploadWork(@NonNull Context context, @NonNull WorkerParameters workerParams)
  {
    super(context, workerParams);
    this.mContext = context;
    this.mWorkerParameters = workerParams;
  }

  /**
   * Starts this worker to upload map edits to osm servers.
   */
  public static void startActionUploadOsmChanges(@NonNull Context context)
  {
    if (Editor.nativeHasSomethingToUpload() && OsmOAuth.isAuthorized())
    {
      final Constraints c = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
      final OneTimeWorkRequest wr = new OneTimeWorkRequest.Builder(OsmUploadWork.class).setConstraints(c).build();
      WorkManager.getInstance(context).beginUniqueWork("UploadOsmChanges", ExistingWorkPolicy.KEEP, wr).enqueue();
    }
  }

  @NonNull
  @Override
  public Result doWork()
  {
    if (!MwmApplication.from(mContext).getecuamaps().arePlatformAndCoreInitialized())
    {
      Logger.w(TAG, "Application is not initialized, ignoring " + mWorkerParameters);
      return Result.failure();
    }
    int result = Editor.uploadChanges();
    if (result == Editor.UPLOAD_RESULT_ERROR)
    {
      if (getRunAttemptCount() >= 5)
        return Result.failure();
      return Result.retry();
    }
    return Result.success();
  }
}
