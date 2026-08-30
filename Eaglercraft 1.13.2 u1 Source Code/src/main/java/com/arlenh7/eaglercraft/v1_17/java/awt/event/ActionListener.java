package com.arlenh7.eaglercraft.v1_17.java.awt.event;

import java.util.EventListener;

@FunctionalInterface
public interface ActionListener extends EventListener {
   void actionPerformed(ActionEvent e);
}
