package app.ecuamaps.widget.recycler;

/**
 * Interface for ViewHolders to specify their divider drawing behavior.
 */
public interface DividerBehavior
{
  /** Use full-width divider adjacent to this item (no start margin). */
  boolean useFullWidthDivider();

  /** Skip drawing divider below this item. */
  default boolean skipDivider()
  {
    return false;
  }
}
