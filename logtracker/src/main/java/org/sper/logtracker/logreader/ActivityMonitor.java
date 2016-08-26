package org.sper.logtracker.logreader;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

/**
 * Führt Buch darüber, welche Reader im Moment aktiv sind oder nicht und markiert den Zustand auf dem User-Interface.
 * @author sper
 */
public class ActivityMonitor {

  enum ActivityLevel {INACTIVE("Inactive", Color.RED), SLEEPING("Sleeping", Color.BLUE), ACTIVE("Active", new Color(0x00a000));
  
    public final String description;
    public Color color;
  
    private ActivityLevel(String description, Color color) {
      this.description = description;
      this.color = color;
    }
  
  }

  private List<KeepAliveLogReader> readerList = new ArrayList<>();
  private JLabel activityLabel = new JLabel(ActivityMonitor.ActivityLevel.INACTIVE.description);
  private ActivityMonitor.ActivityLevel activeLevel;
  
  public ActivityMonitor() {
    activityLabel.setForeground(ActivityLevel.INACTIVE.color);
  }
  
  void registerReader(KeepAliveLogReader reader) {
    readerList.add(reader);
  }
  
  /**
   * Markiert, dass sich der Status mindestens eines Readers verändert hat.
   */
  synchronized void notifyStatusChange() {
    ActivityMonitor.ActivityLevel consolidatedLevel = 
        readerList.stream().map(r -> r.getActivityLevel()).max(Enum::compareTo).orElse(ActivityMonitor.ActivityLevel.INACTIVE);
    if (consolidatedLevel != activeLevel) {
      activityLabel.setText(consolidatedLevel.description);
      activityLabel.setForeground(consolidatedLevel.color);
      activeLevel = consolidatedLevel;
    }
  }

  public JLabel getActivityLabel() {
    return activityLabel;
  }

  public synchronized void removeReader(KeepAliveLogReader keepAliveLogReader) {
    readerList.remove(keepAliveLogReader);
  }
  
}
