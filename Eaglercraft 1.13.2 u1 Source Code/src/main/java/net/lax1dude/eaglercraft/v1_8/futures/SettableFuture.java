/*
 * Copyright (c) 2022-2023 lax1dude, ayunami2000. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.v1_8.futures;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class SettableFuture<V> implements ListenableFuture<V> {

	private final List<Runnable> listeners = new ArrayList<>();
	private V value;
	private Throwable failure;
	private boolean done;
	private boolean cancelled;

	public static <V> SettableFuture<V> create() {
		return new SettableFuture<>();
	}

	public boolean set(V value) {
		return complete(value, null, false);
	}

	public boolean setException(Throwable throwable) {
		return complete(null, throwable, false);
	}

	private boolean complete(V value, Throwable throwable, boolean cancelled) {
		List<Runnable> pending;
		synchronized(this) {
			if(done) {
				return false;
			}
			this.value = value;
			this.failure = throwable;
			this.cancelled = cancelled;
			this.done = true;
			pending = new ArrayList<>(listeners);
			listeners.clear();
		}
		for(int i = 0, l = pending.size(); i < l; ++i) {
			try {
				pending.get(i).run();
			}catch(Throwable t) {
				ListenableFuture.futureExceptionLogger.error("Exception caught running future listener!");
				ListenableFuture.futureExceptionLogger.error(t);
			}
		}
		return true;
	}

	@Override
	public void addListener(final Runnable listener, final Executor executor) {
		Runnable wrapped = new Runnable() {
			@Override
			public void run() {
				executor.execute(listener);
			}
		};
		boolean runNow;
		synchronized(this) {
			runNow = done;
			if(!runNow) {
				listeners.add(wrapped);
			}
		}
		if(runNow) {
			wrapped.run();
		}
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return complete(null, new CancellationException("Task was cancelled."), true);
	}

	@Override
	public synchronized boolean isCancelled() {
		return cancelled;
	}

	@Override
	public synchronized boolean isDone() {
		return done;
	}

	@Override
	public synchronized V get() throws InterruptedException, ExecutionException {
		if(!done) {
			throw new IllegalStateException("Blocking SettableFuture.get() is not available in the browser runtime");
		}
		if(cancelled) {
			throw new CancellationException("Task was cancelled.", failure);
		}
		if(failure != null) {
			throw new ExecutionException(failure);
		}
		return value;
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException {
		return get();
	}

}
