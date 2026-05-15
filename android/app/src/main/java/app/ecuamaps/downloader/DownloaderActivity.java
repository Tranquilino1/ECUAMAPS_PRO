package app.ecuamaps.downloader;

import androidx.fragment.app.Fragment;
import app.ecuamaps.base.BaseMwmFragmentActivity;

public class DownloaderActivity extends BaseMwmFragmentActivity
{
  public static final String EXTRA_OPEN_DOWNLOADED = "open downloaded";

  @Override
  protected Class<? extends Fragment> getFragmentClass()
  {
    return DownloaderFragment.class;
  }
}
