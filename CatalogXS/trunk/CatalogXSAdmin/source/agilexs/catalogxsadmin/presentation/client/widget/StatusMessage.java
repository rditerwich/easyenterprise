package agilexs.catalogxsadmin.presentation.client.widget;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;

/**
 * Displays a status message at the top of the page.
 * 
 * Use with default timeout of 15 seconds:
 *     StatusMessage.get().show("message");
 * Or with custom timeout (in seconds):
 *     StatusMessage.get().show("message", 20);
 */
public class StatusMessage {

  private static final int DEFAULT_TIMEOUT = 15;

  private static StatusMessage instance = new StatusMessage();
  public static StatusMessage get() {
    return instance;
  }

  private DecoratedPopupPanel status = new DecoratedPopupPanel(false, false);
  private HTML message = new HTML();

  private StatusMessage() {
    status.setStyleName("statusmessage");
    //Method NOT publicly visible: status.setAnimationType(AnimationType.ROLL_DOWN);
    status.add(message);
    status.setPopupPositionAndShow(new PositionCallback() {
      @Override
      public void setPosition(int offsetWidth, int offsetHeight) {
        int left = (Window.getClientWidth() - offsetWidth) >> 1;
        status.setPopupPosition(left, 0);
      }
    });
   status.hide();
   //set animation after initial show+hide
   status.setAnimationEnabled(true);
  }

  /**
   * Displays message for 15 seconds.
   *
   * @param message
   */
  public void show(String message) {
    show(message, DEFAULT_TIMEOUT);
  }

  /**
   * Displays message for the duration of given seconds.
   *
   * @param message
   * @param durationSeconds
   */
  public void show(String message, int durationSeconds) {
    new Timer() {
      @Override
      public void run() {
        status.hide();
      }
    }.schedule(durationSeconds * 1000);
    this.message.setHTML(message);
    status.show();
  }
}
