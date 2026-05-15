package app.ecuamaps.help;

import androidx.fragment.app.Fragment;
import app.ecuamaps.base.BaseToolbarActivity;

public class HelpActivity extends BaseToolbarActivity
{
  @Override
  protected Class<? extends Fragment> getFragmentClass()
  {
    return HelpFragment.class;
  }
}
