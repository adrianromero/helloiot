//    This file is part of HelloIot.
//
//    HelloIot is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    HelloIot is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with HelloIot.  If not, see <http://www.gnu.org/licenses/>.

package com.adr.helloiot.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;
import javafx.application.Platform;

/**
 *
 * @author adrian
 */
public class CompletableAsync<T> {
    
    private final static Logger logger = Logger.getLogger(CompletableAsync.class.getName());
    private final static ScheduledExecutorService exec = Executors.newScheduledThreadPool(5); 
    private final CompletableFuture<T> future;
    
    private CompletableAsync(CompletableFuture<T> future) {
        this.future = future;
    }
    
    public static ScheduledFuture<?> scheduleTask(long millis, Runnable r) {
        return exec.schedule(r, millis, TimeUnit.MILLISECONDS);
    }
    
    public static ScheduledFuture<?> scheduleTask(long millis, long period, Runnable r) {
        return exec.scheduleAtFixedRate(r, millis, period, TimeUnit.MILLISECONDS);
    }
    
    public static <U> CompletableAsync<U> supplyAsync(Supplier<U> s) {
        return new CompletableAsync<>(CompletableFuture.supplyAsync(s, exec));
    }
    
    public static CompletableAsync<Void> runAsync(Runnable runnable) {
        return new CompletableAsync<>(CompletableFuture.runAsync(runnable, exec));
    }
    
    public CompletableAsync<Void> thenAccept(Consumer<? super T> action) {
        return new CompletableAsync<>(future.thenAccept(action));
    }
    
    public CompletableAsync<Void> thenAcceptFX(Consumer<? super T> action) {
        
        CompletableFuture<Void> cf = new CompletableFuture<>();
        
        future.thenAccept((T t) -> {  
            Platform.runLater(() -> {
                try {
                    action.accept(t);
                    cf.complete(null);
                } catch (Exception ex) {
                    cf.completeExceptionally(ex);
                }
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
               cf.completeExceptionally(ex); 
            });
            return null;
        });        
                
        return new CompletableAsync<>(cf);
    }
    
    public CompletableAsync<T> exceptionally(Function<Throwable,? extends T> fn) {
        return new CompletableAsync<>(future.exceptionally(fn));
    } 
    
    public CompletableAsync<T> exceptionallyFX(Function<Throwable,? extends T> fn) {  
        
        CompletableFuture<T> cf = new CompletableFuture<>();
        
        future.exceptionally((Throwable t) -> {
            Platform.runLater(() -> {
                try {
                    cf.complete(fn.apply(t));
                } catch (Exception ex) {
                    cf.completeExceptionally(ex);
                }
            }); 
            return null;
        });
                
        return new CompletableAsync<>(cf);                
    }
    
    public static void shutdown() {
        exec.shutdown();
        try {
          if (!exec.awaitTermination(60, TimeUnit.SECONDS)) {
            exec.shutdownNow();
            if (!exec.awaitTermination(60, TimeUnit.SECONDS)) {
                logger.severe("Cannot terminate Task Executor service");
            }
          }
        } catch (InterruptedException ie) {
          exec.shutdownNow();
          Thread.currentThread().interrupt();
        }
    }    
}
