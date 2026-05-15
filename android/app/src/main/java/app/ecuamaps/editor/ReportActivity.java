package app.ecuamaps.editor;

import androidx.fragment.app.Fragment;
import app.ecuamaps.base.BaseMwmFragmentActivity;

public class ReportActivity extends BaseMwmFragmentActivity
{
  @Override
  protected Class<? extends Fragment> getFragmentClass()
  {
    return ReportFragment.class;
  }
}
