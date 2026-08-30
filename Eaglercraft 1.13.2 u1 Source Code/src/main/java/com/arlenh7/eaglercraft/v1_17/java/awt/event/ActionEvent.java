package com.arlenh7.eaglercraft.v1_17.java.awt.event;

import java.util.EventObject;

public class ActionEvent extends EventObject {
   private static final long serialVersionUID = 1L;
   public static final int ACTION_PERFORMED = 1001;
   public static final int SHIFT_MASK = 1;
   public static final int CTRL_MASK = 2;
   public static final int META_MASK = 4;
   public static final int ALT_MASK = 8;

   private final int id;
   private final String actionCommand;
   private final long when;
   private final int modifiers;

   public ActionEvent(Object source, int id, String actionCommand) {
      this(source, id, actionCommand, 0L, 0);
   }

   public ActionEvent(Object source, int id, String actionCommand, int modifiers) {
      this(source, id, actionCommand, 0L, modifiers);
   }

   public ActionEvent(Object source, int id, String actionCommand, long when, int modifiers) {
      super(source);
      this.id = id;
      this.actionCommand = actionCommand;
      this.when = when;
      this.modifiers = modifiers;
   }

   public int getID() {
      return this.id;
   }

   public String getActionCommand() {
      return this.actionCommand;
   }

   public long getWhen() {
      return this.when;
   }

   public int getModifiers() {
      return this.modifiers;
   }
}
