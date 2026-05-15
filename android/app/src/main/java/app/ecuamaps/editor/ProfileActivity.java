package app.ecuamaps.editor;

import androidx.fragment.app.Fragment;
import app.ecuamaps.base.BaseMwmFragmentActivity;

public class ProfileActivity extends BaseMwmFragmentActivity
{
  public static final String EXTRA_REDIRECT_TO_PROFILE = "redirectToProfile";

  @Override
  protected Class<? extends Fragment> getFragmentClass()
  {
    return ProfileFragment.class;
  }
}
