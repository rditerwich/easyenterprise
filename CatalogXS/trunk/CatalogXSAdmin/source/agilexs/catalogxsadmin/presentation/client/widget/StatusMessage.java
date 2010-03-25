package agilexs.catalogxsadmin.presentation.client.widget;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;

/**
 * Displays a status message at the top of the page.
 * 
 * Use:
 *     StatusMessage.get().show("message");
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
    status.add(message);
    status.setPopupPositionAndShow(new PositionCallback() {
      @Override
      public void setPosition(int offsetWidth, int offsetHeight) {
        int left = (Window.getClientWidth() - offsetWidth) >> 1;
        status.setPopupPosition(left, 0);
      }
    });
  }

  public void show(String message) {
    show(message, DEFAULT_TIMEOUT);
  }

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
