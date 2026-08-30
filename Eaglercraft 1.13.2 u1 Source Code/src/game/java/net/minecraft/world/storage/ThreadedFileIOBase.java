package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadedFileIOBase implements Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ThreadedFileIOBase INSTANCE = new ThreadedFileIOBase();
   private final List<IThreadedFileIO> threadedIOQueue = Lists.newArrayList();

   private ThreadedFileIOBase() {
   }

   public static ThreadedFileIOBase getThreadedIOInstance() {
      return INSTANCE;
   }

   public void run() {
   }

   public void queueIO(IThreadedFileIO fileIo) {
      while(fileIo.writeNextIO()) {
      }
   }

   public void waitForFinish() throws InterruptedException {
   }
}
