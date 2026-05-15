package app.ecuamaps.help;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import app.ecuamaps.R;
import app.ecuamaps.WebContainerDelegate;
import app.ecuamaps.base.BaseMwmFragment;
import app.ecuamaps.sdk.util.Constants;
import app.ecuamaps.util.WindowInsetUtils;

public class CopyrightFragment extends BaseMwmFragment
{
  private WebContainerDelegate mDelegate;

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
  {
    View root = inflater.inflate(R.layout.fragment_web_view_with_progress, container, false);

    ViewCompat.setOnApplyWindowInsetsListener(root, WindowInsetUtils.PaddingInsetsListener.excludeTop());

    mDelegate = new WebContainerDelegate(root, Constants.Url.COPYRIGHT) {
      @Override
      protected void doStartActivity(Intent intent)
      {
        startActivity(intent);
      }
    };

    return root;
  }

  @Override
  public boolean onBackPressed()
  {
    if (!mDelegate.onBackPressed())
    {
      ((HelpActivity) requireActivity()).stackFragment(HelpFragment.class, getString(R.string.help), null);
    }

    return true;
  }
}
