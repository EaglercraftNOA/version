/*
	* Copyright (C) 2003-2024 Sebastiano Vigna
	*
	* Licensed under the Apache License, Version 2.0 (the "License");
	* you may not use this file except in compliance with the License.
	* You may obtain a copy of the License at
	*
	*     http://www.apache.org/licenses/LICENSE-2.0
	*
	* Unless required by applicable law or agreed to in writing, software
	* distributed under the License is distributed on an "AS IS" BASIS,
	* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	* See the License for the specific language governing permissions and
	* limitations under the License.
	*/
package it.unimi.dsi.fastutil.doubles;

/**
 * A class providing static methods and objects that do useful things with type-specific priority
 * queues.
 *
 * @see it.unimi.dsi.fastutil.PriorityQueue
 */
public final class DoublePriorityQueues {
	private DoublePriorityQueues() {
	}

	/** A synchronized wrapper class for priority queues. */
	public static class SynchronizedPriorityQueue implements DoublePriorityQueue {
		protected final DoublePriorityQueue q;
		protected final Object sync;

		protected SynchronizedPriorityQueue(final DoublePriorityQueue q, final Object sync) {
			this.q = q;
			this.sync = sync;
		}

		protected SynchronizedPriorityQueue(final DoublePriorityQueue q) {
			this.q = q;
			this.sync = this;
		}

		@Override
		public void enqueue(double x) {
			synchronized (sync) {
				q.enqueue(x);
			}
		}

		@Override
		public double dequeueDouble() {
			synchronized (sync) {
				return q.dequeueDouble();
			}
		}

		@Override
		public double firstDouble() {
			synchronized (sync) {
				return q.firstDouble();
			}
		}

		@Override
		public double lastDouble() {
			synchronized (sync) {
				return q.lastDouble();
			}
		}

		@Override
		public boolean isEmpty() {
			synchronized (sync) {
				return q.isEmpty();
			}
		}

		@Override
		public int size() {
			synchronized (sync) {
				return q.size();
			}
		}

		@Override
		public void clear() {
			synchronized (sync) {
				q.clear();
			}
		}

		@Override
		public void changed() {
			synchronized (sync) {
				q.changed();
			}
		}

		@Override
		public DoubleComparator comparator() {
			synchronized (sync) {
				return q.comparator();
			}
		}

		@Deprecated
		@Override
		public void enqueue(Double x) {
			synchronized (sync) {
				q.enqueue(x);
			}
		}

		@Deprecated
		@Override
		public Double dequeue() {
			synchronized (sync) {
				return q.dequeue();
			}
		}

		@Deprecated
		@Override
		public Double first() {
			synchronized (sync) {
				return q.first();
			}
		}

		@Deprecated
		@Override
		public Double last() {
			synchronized (sync) {
				return q.last();
			}
		}

		@Override
		public int hashCode() {
			synchronized (sync) {
				return q.hashCode();
			}
		}

		@Override
		public boolean equals(final Object o) {
			if (o == this) return true;
			synchronized (sync) {
				return q.equals(o);
			}
		}

		private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
			synchronized (sync) {
				s.defaultWriteObject();
			}
		}
	}

	/**
	 * Returns a synchronized type-specific priority queue backed by the specified type-specific
	 * priority queue.
	 *
	 * @param q the priority queue to be wrapped in a synchronized priority queue.
	 * @return a synchronized view of the specified priority queue.
	 */
	public static DoublePriorityQueue synchronize(final DoublePriorityQueue q) {
		return new SynchronizedPriorityQueue(q);
	}

	/**
	 * Returns a synchronized type-specific priority queue backed by the specified type-specific
	 * priority queue, using an assigned object to synchronize.
	 *
	 * @param q the priority queue to be wrapped in a synchronized priority queue.
	 * @param sync an object that will be used to synchronize the access to the priority queue.
	 * @return a synchronized view of the specified priority queue.
	 */
	public static DoublePriorityQueue synchronize(final DoublePriorityQueue q, final Object sync) {
		return new SynchronizedPriorityQueue(q, sync);
	}
}
