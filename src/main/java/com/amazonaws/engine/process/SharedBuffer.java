package com.amazonaws.engine.process;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SharedBuffer<T> {
   BlockingQueue<T> buffer;

   public SharedBuffer(){
       this.buffer = new LinkedBlockingQueue<>();
   }

   public void put(T order) throws InterruptedException {
      buffer.put(order);
   }

   public T take() throws InterruptedException {
      return buffer.take();
   }
}
