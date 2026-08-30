/*
	* Copyright (C) 2005-2024 Sebastiano Vigna
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
package it.unimi.dsi.fastutil.io;
import static it.unimi.dsi.fastutil.BigArrays.SEGMENT_MASK;
import static it.unimi.dsi.fastutil.BigArrays.length;
import static it.unimi.dsi.fastutil.BigArrays.start;
import static it.unimi.dsi.fastutil.BigArrays.segment;
import static it.unimi.dsi.fastutil.BigArrays.displacement;
import static it.unimi.dsi.fastutil.BigArrays.ensureOffsetLength;
import static it.unimi.dsi.fastutil.Arrays.ensureOffsetLength;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import it.unimi.dsi.fastutil.bytes.*;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.doubles.*;
import it.unimi.dsi.fastutil.booleans.*;
import it.unimi.dsi.fastutil.chars.*;
import it.unimi.dsi.fastutil.shorts.*;
import it.unimi.dsi.fastutil.floats.*;
/** Provides static methods to perform easily binary I/O.
	*
	* <p>This class fills some gaps in the Java API. First of all, you have two
	* buffered, easy-to-use methods to {@linkplain #storeObject(Object,CharSequence) store an object to a file}
	* or {@linkplain #loadObject(CharSequence) load an object from a file},
	* and two
	* buffered, easy-to-use methods to {@linkplain #storeObject(Object,OutputStream) store an object to an output stream}
	* or to {@linkplain #loadObject(InputStream) load an object from an input stream}.
	*
	* <p>Second, a natural operation on sequences of primitive elements is to load or
	* store them in binary form using the {@link DataInput} conventions.  This
	* method is much more flexible than storing arrays as objects, as it allows
	* for partial load, partial store, and makes it easy to read the
	* resulting files from other languages.
	*
	* <p>For each primitive type, this class provides methods that read elements
	* from a {@link DataInput}, an {@link InputStream} or from a file into an array
	* or a {@linkplain it.unimi.dsi.fastutil.BigArrays big array}, or expose those elements as a type-specific {@link Iterator iterator}.
	* There are also methods that let you choose a {@linkplain ByteOrder byte order} and
	* that work with {@link ReadableByteChannel byte channels}.
	* Analogously, there are
	* methods that store the content of a (big) array (fragment) or the elements
	* returned by an iterator to a {@link DataOutput}, to an {@link OutputStream}, 
	* to a {@link WritableByteChannel}, or to a given file. Files
	* are buffered using {@link FastBufferedInputStream} and {@link FastBufferedOutputStream},
	* or, when possible, with a {@linkplain ByteBuffer byte buffer} allocated with
	* {@linkplain ByteBuffer#allocateDirect(int)}.
	*
	* <p>Since bytes can be read from or written to any stream, additional methods
	* makes it possible to {@linkplain #loadBytes(InputStream,byte[]) load bytes from} and
	* {@linkplain #storeBytes(byte[],OutputStream) store bytes to} a stream. Such methods
	* use the bulk-read methods of {@link InputStream} and {@link OutputStream}, but they
	* also include a workaround for <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6478546">bug #6478546</a>.
	*
	* <p>The store methods with a specified byte order are particularly useful when writing
	* data that is intended to be {@linkplain it.unimi.dsi.fastutil.ints.IntMappedBigList mapped into memory},
	* as using the {@linkplain ByteOrder#nativeOrder() native order} enhances performances significantly.
	*
	* @since 4.4
	*/
public class BinIO {
	/** The size used with {@link ByteBuffer#allocateDirect(int)}. */
	public static int BUFFER_SIZE = 8192;
	private BinIO() {}
	/** Stores an object in a file given by a {@link File} object.
	 *
	 * @param o an object.
	 * @param file a file.
	 * @see #loadObject(File)
	 */
	public static void storeObject(final Object o, final File file) throws IOException {
	 final ObjectOutputStream oos = new ObjectOutputStream(new FastBufferedOutputStream(new FileOutputStream(file)));
	 oos.writeObject(o);
	 oos.close();
	}
	/** Stores an object in a file given by a filename.
	 *
	 * @param o an object.
	 * @param filename a filename.
	 * @see #loadObject(CharSequence)
	 */
	public static void storeObject(final Object o, final CharSequence filename) throws IOException {
	 storeObject(o, new File(filename.toString()));
	}
	/** Loads an object from a file given by a {@link File} object.
	 *
	 * @param file a file.
	 * @return the object stored under the given file.
	 * @see #storeObject(Object, File)
	 */
	public static Object loadObject(final File file) throws IOException, ClassNotFoundException {
	 final ObjectInputStream ois = new ObjectInputStream(new FastBufferedInputStream(new FileInputStream(file)));
	 final Object result = ois.readObject();
	 ois.close();
	 return result;
	}
	/** Loads an object from a file given by a filename.
	 *
	 * @param filename a filename.
	 * @return the object stored under the given filename.
	 * @see #storeObject(Object, CharSequence)
	 */
	public static Object loadObject(final CharSequence filename) throws IOException, ClassNotFoundException {
	 return loadObject(new File(filename.toString()));
	}
	/** Stores an object in a given output stream.
	 *
	 * <p>This method buffers {@code s}, and flushes all wrappers after
	 * calling {@code writeObject()}, but does not close {@code s}.
	 *
	 * @param o an object.
	 * @param s an output stream.
	 * @see #loadObject(InputStream)
	 */
	public static void storeObject(final Object o, final OutputStream s) throws IOException {
	 @SuppressWarnings("resource")
	 final ObjectOutputStream oos = new ObjectOutputStream(new FastBufferedOutputStream(s));
	 oos.writeObject(o);
	 oos.flush();
	}
	/** Loads an object from a given input stream.
	 *
	 * <p><STRONG>Warning</STRONG>: this method buffers the input stream. As a consequence,
	 * subsequent reads from the same stream may not give the desired results, as bytes
	 * may have been read by the internal buffer, but not used by {@code readObject()}.
	 * This is a feature, as this method is targeted at one-shot reading from streams,
	 * e.g., reading exactly one object from {@link System#in}.
	 *
	 * @param s an input stream.
	 * @return the object read from the given input stream.
	 * @see #storeObject(Object, OutputStream)
	 */
	public static Object loadObject(final InputStream s) throws IOException, ClassNotFoundException {
	 @SuppressWarnings("resource")
	 final ObjectInputStream ois = new ObjectInputStream(new FastBufferedInputStream(s));
	 final Object result = ois.readObject();
	 return result;
	}
/* Generic definitions */
/* Assertions (useful to generate conditional code) */
/* Narrowing and widening */
/* Current type and class (and size, if applicable) */
/* Value methods */
/* Interfaces (keys) */
/* Interfaces (values) */
/* Types and methods related to primitive-type support in the JDK */
/* Abstract implementations (keys) */
/* Abstract implementations (values) */
/* Static containers (keys) */
/* Static containers (values) */
/* Implementations */
/* Synchronized wrappers */
/* Unmodifiable wrappers */
/* Other wrappers */
/* Methods (keys) */
/* Methods (values) */
/* Methods (keys/values) */
/* Methods that have special names depending on keys (but the special names depend on values) */
/* Equality */
/* Object/Reference-only definitions (keys) */
/* Primitive-type-only definitions (keys) */
/* Object/Reference-only definitions (values) */
/* START_OF_JAVA_SOURCE */
/*
	* Copyright (C) 2004-2024 Sebastiano Vigna
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
/** Loads elements from a given data input, storing them in a given array fragment.
	*
	* @param dataInput a data input.
	* @param array an array which will be filled with data from {@code dataInput}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code dataInput} (it might be less than {@code length} if {@code dataInput} ends).
	*/
public static int loadBooleans(final DataInput dataInput, final boolean[] array, final int offset, final int length) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	int i = 0;
	try {
	 for(i = 0; i < length; i++) array[i + offset] = dataInput.readBoolean();
	}
	catch(EOFException itsOk) {}
	return i;
}
/** Loads elements from a given data input, storing them in a given array.
	*
	* @param dataInput a data input.
	* @param array an array which will be filled with data from {@code dataInput}.
	* @return the number of elements actually read from {@code dataInput} (it might be less than the array length if {@code dataInput} ends).
	*/
public static int loadBooleans(final DataInput dataInput, final boolean[] array) throws IOException {
	int i = 0;
	try {
	 final int length = array.length;
	 for(i = 0; i < length; i++) array[i] = dataInput.readBoolean();
	}
	catch(EOFException itsOk) {}
	return i;
}
/** Loads elements from a file given by a {@link File} object, storing them in a given array fragment.
	*
	* @param file a file.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadBooleans(final File file, final boolean[] array, final int offset, final int length) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	final DataInputStream dis = new DataInputStream(new FastBufferedInputStream(new FileInputStream(file)));
	int i = 0;
	try {
	 for(i = 0; i < length; i++) array[i + offset] = dis.readBoolean();
	}
	catch(EOFException itsOk) {}
	dis.close();
	return i;
}
/** Loads elements from a file given by a filename, storing them in a given array fragment.
	*
	* @param filename a filename.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadBooleans(final CharSequence filename, final boolean[] array, final int offset, final int length) throws IOException {
	return loadBooleans(new File(filename.toString()), array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, storing them in a given array.
	*
	* @param file a file.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadBooleans(final File file, final boolean[] array) throws IOException {
	return loadBooleans(file, array, 0, array.length);
}
/** Loads elements from a file given by a filename, storing them in a given array.
	*
	* @param filename a filename.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadBooleans(final CharSequence filename, final boolean[] array) throws IOException {
	return loadBooleans(new File(filename.toString()), array);
}
/** Loads elements from a file given by a {@link File} object, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @return an array filled with the content of the specified file.
	*/
public static boolean[] loadBooleans(final File file) throws IOException {
	final FileInputStream fis = new FileInputStream(file);
	final long length = fis.getChannel().size();
	if (length > Integer.MAX_VALUE) {
	 fis.close();
	 throw new IllegalArgumentException("File too long: " + fis.getChannel().size()+ " bytes (" + length + " elements)");
	}
	final boolean[] array = new boolean[(int)length];
	final DataInputStream dis = new DataInputStream(new FastBufferedInputStream(fis));
	for(int i = 0; i < length; i++) array[i] = dis.readBoolean();
	dis.close();
	return array;
}
/** Loads elements from a file given by a filename, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @return an array filled with the content of the specified file.
	*/
public static boolean[] loadBooleans(final CharSequence filename) throws IOException {
	return loadBooleans(new File(filename.toString()));
}
/** Stores an array fragment to a given data output.
	*
	* @param array an array whose elements will be written to {@code dataOutput}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param dataOutput a data output.
	*/
public static void storeBooleans(final boolean array[], final int offset, final int length, final DataOutput dataOutput) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	for(int i = 0; i < length; i++) dataOutput.writeBoolean(array[offset + i]);
}
/** Stores an array to a given data output.
	*
	* @param array an array whose elements will be written to {@code dataOutput}.
	* @param dataOutput a data output.
	*/
public static void storeBooleans(final boolean array[], final DataOutput dataOutput) throws IOException {
	final int length = array.length;
	for(int i = 0; i < length; i++) dataOutput.writeBoolean(array[i]);
}
/** Stores an array fragment to a file given by a {@link File} object.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	*/
public static void storeBooleans(final boolean array[], final int offset, final int length, final File file) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	final DataOutputStream dos = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(file)));
	for(int i = 0; i < length; i++) dos.writeBoolean(array[offset + i]);
	dos.close();
}
/** Stores an array fragment to a file given by a filename.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	*/
public static void storeBooleans(final boolean array[], final int offset, final int length, final CharSequence filename) throws IOException {
	storeBooleans(array, offset, length, new File(filename.toString()));
}
/** Stores an array to a file given by a {@link File} object.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	*/
public static void storeBooleans(final boolean array[], final File file) throws IOException {
	storeBooleans(array, 0, array.length, file);
}
/** Stores an array to a file given by a filename.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	*/
public static void storeBooleans(final boolean array[], final CharSequence filename) throws IOException {
	storeBooleans(array, new File(filename.toString()));
}
/** Loads elements from a given data input, storing them in a given big-array fragment.
	*
	* @param dataInput a data input.
	* @param array a big array which will be filled with data from {@code dataInput}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code dataInput} (it might be less than {@code length} if {@code dataInput} ends).
	*/
public static long loadBooleans(final DataInput dataInput, final boolean[][] array, final long offset, final long length) throws IOException {
	ensureOffsetLength(array, offset, length);
	long c = 0;
	try {
	 for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	  final boolean[] t = array[i];
	  final int l = (int)Math.min(t.length, offset + length - start(i));
	  for(int d = (int)Math.max(0, offset - start(i)); d < l; d++) {
	   t[d] = dataInput.readBoolean();
	   c++;
	  }
	 }
	}
	catch(EOFException itsOk) {}
	return c;
}
/** Loads elements from a given data input, storing them in a given big array.
	*
	* @param dataInput a data input.
	* @param array a big array which will be filled with data from {@code dataInput}.
	* @return the number of elements actually read from {@code dataInput} (it might be less than the array length if {@code dataInput} ends).
	*/
public static long loadBooleans(final DataInput dataInput, final boolean[][] array) throws IOException {
	long c = 0;
	try {
	 for(int i = 0; i < array.length; i++) {
	  final boolean[] t = array[i];
	  final int l = t.length;
	  for(int d = 0; d < l; d++) {
	   t[d] = dataInput.readBoolean();
	   c++;
	  }
	 }
	}
	catch(EOFException itsOk) {}
	return c;
}
/** Loads elements from a file given by a {@link File} object, storing them in a given big-array fragment.
	*
	* @param file a file.
	* @param array a big array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadBooleans(final File file, final boolean[][] array, final long offset, final long length) throws IOException {
	ensureOffsetLength(array, offset, length);
	final FileInputStream fis = new FileInputStream(file);
	final DataInputStream dis = new DataInputStream(new FastBufferedInputStream(fis));
	long c = 0;
	try {
	 for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	  final boolean[] t = array[i];
	  final int l = (int)Math.min(t.length, offset + length - start(i));
	  for(int d = (int)Math.max(0, offset - start(i)); d < l; d++) {
	   t[d] = dis.readBoolean();
	   c++;
	  }
	 }
	}
	catch(EOFException itsOk) {}
	dis.close();
	return c;
}
/** Loads elements from a file given by a filename, storing them in a given big-array fragment.
	*
	* @param filename a filename.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadBooleans(final CharSequence filename, final boolean[][] array, final long offset, final long length) throws IOException {
	return loadBooleans(new File(filename.toString()), array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, storing them in a given big array.
	*
	* @param file a file.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadBooleans(final File file, final boolean[][] array) throws IOException {
	final FileInputStream fis = new FileInputStream(file);
	final DataInputStream dis = new DataInputStream(new FastBufferedInputStream(fis));
	long c = 0;
	try {
	 for(int i = 0; i < array.length; i++) {
	  final boolean[] t = array[i];
	  final int l = t.length;
	  for(int d = 0; d < l; d++) {
	   t[d] = dis.readBoolean();
	   c++;
	  }
	 }
	}
	catch(EOFException itsOk) {}
	dis.close();
	return c;
}
/** Loads elements from a file given by a filename, storing them in a given big array.
	*
	* @param filename a filename.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadBooleans(final CharSequence filename, final boolean[][] array) throws IOException {
	return loadBooleans(new File(filename.toString()), array);
}
/** Loads elements from a file given by a {@link File} object, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @return a big array filled with the content of the specified file.
	*/
public static boolean[][] loadBooleansBig(final File file) throws IOException {
	final FileInputStream fis = new FileInputStream(file);
	final long length = fis.getChannel().size();
	final boolean[][] array = BooleanBigArrays.newBigArray(length);
	final DataInputStream dis = new DataInputStream(new FastBufferedInputStream(fis));
	for(int i = 0; i < array.length; i++) {
	 final boolean[] t = array[i];
	 final int l = t.length;
	 for(int d = 0; d < l; d++) t[d] = dis.readBoolean();
	}
	dis.close();
	return array;
}
/** Loads elements from a file given by a filename, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @return a big array filled with the content of the specified file.
	*/
public static boolean[][] loadBooleansBig(final CharSequence filename) throws IOException {
	return loadBooleansBig(new File(filename.toString()));
}
/** Stores an array fragment to a given data output.
	*
	* @param array an array whose elements will be written to {@code dataOutput}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param dataOutput a data output.
	*/
public static void storeBooleans(final boolean array[][], final long offset, final long length, final DataOutput dataOutput) throws IOException {
	ensureOffsetLength(array, offset, length);
	for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	 final boolean[] t = array[i];
	 final int l = (int)Math.min(t.length, offset + length - start(i));
	 for(int d = (int)Math.max(0, offset - start(i)); d < l; d++) dataOutput.writeBoolean(t[d]);
	}
}
/** Stores a big array to a given data output.
	*
	* @param array a big array whose elements will be written to {@code dataOutput}.
	* @param dataOutput a data output.
	*/
public static void storeBooleans(final boolean array[][], final DataOutput dataOutput) throws IOException {
	for(int i = 0; i < array.length; i++) {
	 final boolean[] t = array[i];
	 final int l = t.length;
	 for(int d = 0; d < l; d++) dataOutput.writeBoolean(t[d]);
	}
}
/** Stores a big-array fragment to a file given by a {@link File} object.
	*
	* @param array a big array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	*/
public static void storeBooleans(final boolean array[][], final long offset, final long length, final File file) throws IOException {
	final DataOutputStream dos = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(file)));
	for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	 final boolean[] t = array[i];
	 final int l = (int)Math.min(t.length, offset + length - start(i));
	 for(int d = (int)Math.max(0, offset - start(i)); d < l; d++) dos.writeBoolean(t[d]);
	}
	dos.close();
}
/** Stores a big-array fragment to a file given by a filename.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	*/
public static void storeBooleans(final boolean array[][], final long offset, final long length, final CharSequence filename) throws IOException {
	storeBooleans(array, offset, length, new File(filename.toString()));
}
/** Stores an array to a file given by a {@link File} object.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	*/
public static void storeBooleans(final boolean array[][], final File file) throws IOException {
	final DataOutputStream dos = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(file)));
	for(int i = 0; i < array.length; i++) {
	 final boolean[] t = array[i];
	 final int l = t.length;
	 for(int d = 0; d < l; d++) dos.writeBoolean(t[d]);
	}
	dos.close();
}
/** Stores a big array to a file given by a filename.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	*/
public static void storeBooleans(final boolean array[][], final CharSequence filename) throws IOException {
	storeBooleans(array, new File(filename.toString()));
}
/** Stores the element returned by an iterator to a given data output.
	*
	* @param i an iterator whose output will be written to {@code dataOutput}.
	* @param dataOutput a filename.
	*/
public static void storeBooleans(final BooleanIterator i, final DataOutput dataOutput) throws IOException {
	while(i.hasNext()) dataOutput.writeBoolean(i.nextBoolean());
}
/** Stores the element returned by an iterator to a file given by a {@link File} object.
	*
	* @param i an iterator whose output will be written to {@code file}.
	* @param file a file.
	*/
public static void storeBooleans(final BooleanIterator i, final File file) throws IOException {
	final DataOutputStream dos = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(file)));
	while(i.hasNext()) dos.writeBoolean(i.nextBoolean());
	dos.close();
}
/** Stores the element returned by an iterator to a file given by a filename.
	*
	* @param i an iterator whose output will be written to the file {@code filename}.
	* @param filename a filename.
	*/
public static void storeBooleans(final BooleanIterator i, final CharSequence filename) throws IOException {
	storeBooleans(i, new File(filename.toString()));
}
/** A wrapper that exhibits the content of a data input stream as a type-specific iterator. */
private static final class BooleanDataInputWrapper implements BooleanIterator {
	private final DataInput dataInput;
	private boolean toAdvance = true;
	private boolean endOfProcess = false;
	private boolean next;
	public BooleanDataInputWrapper(final DataInput dataInput) {
	 this.dataInput = dataInput;
	}
	@Override
	public boolean hasNext() {
	 if (! toAdvance) return ! endOfProcess;
	 toAdvance = false;
	 try { next = dataInput.readBoolean(); }
	 catch(EOFException eof) { endOfProcess = true; }
	 catch(IOException rethrow) { throw new RuntimeException(rethrow); }
	 return ! endOfProcess;
	}
	@Override
	public boolean nextBoolean() {
	 if (! hasNext()) throw new NoSuchElementException();
	 toAdvance = true;
	 return next;
	}
}
/** Wraps the given data input stream into an iterator.
	*
	* @param dataInput a data input.
	*/
public static BooleanIterator asBooleanIterator(final DataInput dataInput) {
	return new BooleanDataInputWrapper(dataInput);
}
/** Wraps a file given by a {@link File} object into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param file a file.
	*/
public static BooleanIterator asBooleanIterator(final File file) throws IOException {
	return new BooleanDataInputWrapper(new DataInputStream(new FastBufferedInputStream(new FileInputStream(file))));
}
/** Wraps a file given by a filename into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param filename a filename.
	*/
public static BooleanIterator asBooleanIterator(final CharSequence filename) throws IOException {
	return asBooleanIterator(new File(filename.toString()));
}
/** Wraps a file given by a {@link File} object into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param file a file.
	*/
public static BooleanIterable asBooleanIterable(final File file) {
	return () -> {
	 try { return asBooleanIterator(file); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
/** Wraps a file given by a filename into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param filename a filename.
	*/
public static BooleanIterable asBooleanIterable(final CharSequence filename) {
	return () -> {
	 try { return asBooleanIterator(filename); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
/* Generic definitions */
/* Assertions (useful to generate conditional code) */
/* Narrowing and widening */
/* Current type and class (and size, if applicable) */
/* Value methods */
/* Interfaces (keys) */
/* Interfaces (values) */
/* Types and methods related to primitive-type support in the JDK */
/* Abstract implementations (keys) */
/* Abstract implementations (values) */
/* Static containers (keys) */
/* Static containers (values) */
/* Implementations */
/* Synchronized wrappers */
/* Unmodifiable wrappers */
/* Other wrappers */
/* Methods (keys) */
/* Methods (values) */
/* Methods (keys/values) */
/* Methods that have special names depending on keys (but the special names depend on values) */
/* Equality */
/* Object/Reference-only definitions (keys) */
/* Primitive-type-only definitions (keys) */
/* Object/Reference-only definitions (values) */
/* START_OF_JAVA_SOURCE */
/*
	* Copyright (C) 2004-2024 Sebastiano Vigna
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
// HORRIBLE kluges to work around bug #6478546
private static final int MAX_IO_LENGTH = 1024 * 1024;
private static int read(final InputStream is, final byte a[], final int offset, final int length) throws IOException {
	if (length == 0) return 0;
	int read = 0, result;
	do {
	 result = is.read(a, offset + read, Math.min(length - read, MAX_IO_LENGTH));
	 if (result < 0) return read;
	 read += result;
	} while(read < length);
	return read;
}
private static void write(final OutputStream outputStream, final byte a[], final int offset, final int length) throws IOException {
	int written = 0;
	while(written < length) {
	 outputStream.write(a, offset + written, Math.min(length - written, MAX_IO_LENGTH));
	 written += Math.min(length - written, MAX_IO_LENGTH);
	}
}
private static void write(final DataOutput dataOutput, final byte a[], final int offset, final int length) throws IOException {
	int written = 0;
	while(written < length) {
	 dataOutput.write(a, offset + written, Math.min(length - written, MAX_IO_LENGTH));
	 written += Math.min(length - written, MAX_IO_LENGTH);
	}
}
// Additional read/write methods to work around the DataInput/DataOutput schizophrenia.
/** Loads bytes from a given input stream, storing them in a given array fragment.
	*
	* <p>Note that this method is going to be significantly faster than {@link #loadBytes(DataInput,byte[],int,int)}
	* as it uses {@link InputStream}'s bulk-read methods.
	*
	* @param inputStream an input stream.
	* @param array an array which will be filled with data from {@code inputStream}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code inputStream} (it might be less than {@code length} if {@code inputStream} ends).
	*/
public static int loadBytes(final InputStream inputStream, final byte[] array, final int offset, final int length) throws IOException {
	return read(inputStream, array, offset, length);
}
/** Loads bytes from a given input stream, storing them in a given array.
	*
	* <p>Note that this method is going to be significantly faster than {@link #loadBytes(DataInput,byte[])}
	* as it uses {@link InputStream}'s bulk-read methods.
	*
	* @param inputStream an input stream.
	* @param array an array which will be filled with data from {@code inputStream}.
	* @return the number of elements actually read from {@code inputStream} (it might be less than the array length if {@code inputStream} ends).
	*/
public static int loadBytes(final InputStream inputStream, final byte[] array) throws IOException {
	return read(inputStream, array, 0, array.length);
}
/** Stores an array fragment to a given writable channel.
	*
	* <p>Note that this method is going to be significantly faster than {@link #storeBytes(byte[],int,int,DataOutput)}
	* as it uses {@link OutputStream}'s bulk-write methods.
	*
	* @param array an array whose elements will be written to {@code outputStream}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param outputStream an output stream.
	*/
public static void storeBytes(final byte array[], final int offset, final int length, final OutputStream outputStream) throws IOException {
	write(outputStream, array, offset, length);
}
/** Stores an array to a given writable channel.
	*
	* <p>Note that this method is going to be significantly faster than {@link #storeBytes(byte[],DataOutput)}
	* as it uses {@link OutputStream}'s bulk-write methods.
	*
	* @param array an array whose elements will be written to {@code outputStream}.
	* @param outputStream an output stream.
	*/
public static void storeBytes(final byte array[], final OutputStream outputStream) throws IOException {
	write(outputStream, array, 0, array.length);
}
private static long read(final InputStream is, final byte a[][], final long offset, final long length) throws IOException {
	if (length == 0) return 0;
	long read = 0;
	int segment = segment(offset);
	int displacement = displacement(offset);
	int result;
	do {
	 result = is.read(a[segment], displacement, (int)Math.min(a[segment].length - displacement, Math.min(length - read, MAX_IO_LENGTH)));
	 if (result < 0) return read;
	 read += result;
	 displacement += result;
	 if (displacement == a[segment].length) {
	  segment++;
	  displacement = 0;
	 }
	} while(read < length);
	return read;
}
private static void write(final OutputStream outputStream, final byte a[][], final long offset, final long length) throws IOException {
	if (length == 0) return;
	long written = 0;
	int toWrite;
	int segment = segment(offset);
	int displacement = displacement(offset);
	do {
	 toWrite = (int)Math.min(a[segment].length - displacement, Math.min(length - written, MAX_IO_LENGTH));
	 outputStream.write(a[segment], displacement, toWrite);
	 written += toWrite;
	 displacement += toWrite;
	 if (displacement == a[segment].length) {
	  segment++;
	  displacement = 0;
	 }
	} while(written < length);
}
private static void write(final DataOutput dataOutput, final byte a[][], final long offset, final long length) throws IOException {
	if (length == 0) return;
	long written = 0;
	int toWrite;
	int segment = segment(offset);
	int displacement = displacement(offset);
	do {
	 toWrite = (int)Math.min(a[segment].length - displacement, Math.min(length - written, MAX_IO_LENGTH));
	 dataOutput.write(a[segment], displacement, toWrite);
	 written += toWrite;
	 displacement += toWrite;
	 if (displacement == a[segment].length) {
	  segment++;
	  displacement = 0;
	 }
	} while(written < length);
}
// Additional read/write methods to work around the DataInput/DataOutput schizophrenia.
/** Loads bytes from a given readable channel, storing them in a given big-array fragment.
	*
	* <p>Note that this method is going to be significantly faster than {@link #loadBytes(DataInput,byte[][],long,long)}
	* as it uses {@link InputStream}'s bulk-read methods.
	*
	* @param inputStream an input stream.
	* @param array a big array which will be filled with data from {@code inputStream}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code inputStream} (it might be less than {@code length} if {@code inputStream} ends).
	*/
public static long loadBytes(final InputStream inputStream, final byte[][] array, final long offset, final long length) throws IOException {
	return read(inputStream, array, offset, length);
}
/** Loads bytes from a given readable channel, storing them in a given big array.
	*
	* <p>Note that this method is going to be significantly faster than {@link #loadBytes(DataInput,byte[][])}
	* as it uses {@link InputStream}'s bulk-read methods.
	*
	* @param inputStream an input stream.
	* @param array a big array which will be filled with data from {@code inputStream}.
	* @return the number of elements actually read from {@code inputStream} (it might be less than the array length if {@code inputStream} ends).
	*/
public static long loadBytes(final InputStream inputStream, final byte[][] array) throws IOException {
	return read(inputStream, array, 0, length(array));
}
/** Stores a big-array fragment to a given writable channel.
	*
	* <p>Note that this method is going to be significantly faster than {@link #storeBytes(byte[][],long,long,DataOutput)}
	* as it uses {@link OutputStream}'s bulk-write methods.
	*
	* @param array a big array whose elements will be written to {@code outputStream}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param outputStream an output stream.
	*/
public static void storeBytes(final byte array[][], final long offset, final long length, final OutputStream outputStream) throws IOException {
	write(outputStream, array, offset, length);
}
/** Stores a big array to a given writable channel.
	*
	* <p>Note that this method is going to be significantly faster than {@link #storeBytes(byte[][],DataOutput)}
	* as it uses {@link OutputStream}'s bulk-write methods.
	*
	* @param array a big array whose elements will be written to {@code outputStream}.
	* @param outputStream an output stream.
	*/
public static void storeBytes(final byte array[][], final OutputStream outputStream) throws IOException {
	write(outputStream, array, 0, length(array));
}
// Methods working with channels.
/** Loads bytes from a given readable channel, storing them in a given array fragment.
	*
	* <p>Note that this method is going to be significantly faster than {@link #loadBytes(DataInput,byte[],int,int)}
	* as it uses {@link ReadableByteChannel}'s bulk-read methods.
	*
	* @param channel a readable channel.
	* @param array an array which will be filled with data from {@code channel}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code channel} (it might be less than {@code length} if {@code channel} ends).
	*/
public static int loadBytes(final ReadableByteChannel channel, final byte[] array, int offset, int length) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
	int read = 0;
	for (;;) {
	 buffer.clear();
	 buffer.limit(Math.min(buffer.capacity(), length));
	 int r = channel.read(buffer);
	 if (r <= 0) return read;
	 read += r;
	 buffer.flip();
	 buffer.get(array, offset, r);
	 offset += r;
	 length -= r;
	}
}
/** Loads bytes from a given readable channel, storing them in a given array.
	*
	* <p>Note that this method is going to be significantly faster than {@link #loadBytes(DataInput,byte[],int,int)}
	* as it uses {@link ReadableByteChannel}'s bulk-read methods.
	*
	* @param channel a readable channel.
	* @param array an array which will be filled with data from {@code channel}.
	* @return the number of elements actually read from {@code channel} (it might be less than the array length if {@code channel} ends).
	*/
public static int loadBytes(final ReadableByteChannel channel, final byte[] array) throws IOException {
	return loadBytes(channel, array, 0, array.length);
}
/** Stores an array fragment to a given writable channel.
	*
	* <p>Note that this method is going to be significantly faster than {@link #loadBytes(DataInput,byte[],int,int)}
	* as it uses {@link WritableByteChannel}'s bulk-write methods.
	*
	* @param array an array whose elements will be written to {@code channel}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param channel a writable channel.
	*/
public static void storeBytes(final byte array[], int offset, int length, final WritableByteChannel channel) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
	while(length != 0) {
	 final int l = Math.min(length, buffer.capacity());
	 buffer.clear();
	 buffer.put(array, offset, l);
	 buffer.flip();
	 channel.write(buffer);
	 offset += l;
	 length -= l;
	}
}
/** Stores an array to a given writable channel.
	*
	* <p>Note that this method is going to be significantly faster than {@link #loadBytes(DataInput,byte[],int,int)}
	* as it uses {@link WritableByteChannel}'s bulk-write methods.
	*
	* @param array an array whose elements will be written to {@code channel}.
	* @param channel a writable channel.
	*/
public static void storeBytes(final byte array[], final WritableByteChannel channel) throws IOException {
	storeBytes(array, 0, array.length, channel);
}
/** Loads bytes from a given readable channel, storing them in a given big-array fragment.
	*
	* <p>Note that this method is going to be significantly faster than {@link #loadBytes(DataInput,byte[],int,int)}
	* as it uses {@link ReadableByteChannel}'s bulk-read methods.
	*
	* @param channel a readable channel.
	* @param array a big array which will be filled with data from {@code channel}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code channel} (it might be less than {@code length} if {@code channel} ends).
	*/
public static long loadBytes(final ReadableByteChannel channel, final byte[][] array, final long offset, final long length) throws IOException {
	ensureOffsetLength(array, offset, length);
	long read = 0;
	for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	 final byte[] t = array[i];
	 final int s = (int)Math.max(0, offset - start(i));
	 final int e = (int)Math.min(t.length, offset + length - start(i));
	 final int r = loadBytes(channel, t, s, e - s);
	 read += r;
	 if (r < e -s) break;
	}
	return read;
}
/** Loads bytes from a given readable channel, storing them in a given big array.
	*
	* <p>Note that this method is going to be significantly faster than {@link #loadBytes(DataInput,byte[],int,int)}
	* as it uses {@link ReadableByteChannel}'s bulk-read methods.
	*
	* @param channel a readable channel.
	* @param array a big array which will be filled with data from {@code channel}.
	* @return the number of elements actually read from {@code channel} (it might be less than the array length if {@code channel} ends).
	*/
public static long loadBytes(final ReadableByteChannel channel, final byte[][] array) throws IOException {
	return loadBytes(channel, array, 0, length(array));
}
/** Stores a big-array fragment to a given writable channel.
	*
	* <p>Note that this method is going to be significantly faster than {@link #loadBytes(DataInput,byte[],int,int)}
	* as it uses {@link WritableByteChannel}'s bulk-write methods.
	*
	* @param array a big array whose elements will be written to {@code channel}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param channel a writable channel.
	*/
public static void storeBytes(final byte array[][], final long offset, final long length, final WritableByteChannel channel) throws IOException {
	for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	 final int s = (int)Math.max(0, offset - start(i));
	 final int l = (int)Math.min(array[i].length, offset + length - start(i));
	 storeBytes(array[i], s, l - s, channel);
	}
}
/** Stores a big array to a given writable channel.
	*
	* <p>Note that this method is going to be significantly faster than {@link #loadBytes(DataInput,byte[],int,int)}
	* as it uses {@link WritableByteChannel}'s bulk-write methods.
	*
	* @param array a big array whose elements will be written to {@code channel}.
	* @param channel a writable channel.
	*/
public static void storeBytes(final byte array[][], final WritableByteChannel channel) throws IOException {
	for(byte[] t: array) storeBytes(t, channel);
}
/** Loads elements from a given data input, storing them in a given array fragment.
	*
	* @param dataInput a data input.
	* @param array an array which will be filled with data from {@code dataInput}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code dataInput} (it might be less than {@code length} if {@code dataInput} ends).
	*/
public static int loadBytes(final DataInput dataInput, final byte[] array, final int offset, final int length) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	int i = 0;
	try {
	 for(i = 0; i < length; i++) array[i + offset] = dataInput.readByte();
	}
	catch(EOFException itsOk) {}
	return i;
}
/** Loads elements from a given data input, storing them in a given array.
	*
	* @param dataInput a data input.
	* @param array an array which will be filled with data from {@code dataInput}.
	* @return the number of elements actually read from {@code dataInput} (it might be less than the array length if {@code dataInput} ends).
	*/
public static int loadBytes(final DataInput dataInput, final byte[] array) throws IOException {
	int i = 0;
	try {
	 final int length = array.length;
	 for(i = 0; i < length; i++) array[i] = dataInput.readByte();
	}
	catch(EOFException itsOk) {}
	return i;
}
/** Loads elements from a file given by a {@link File} object, storing them in a given array fragment.
	*
	* @param file a file.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadBytes(final File file, final byte[] array, final int offset, final int length) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	final int result = loadBytes(channel, array, offset, length);
	channel.close();
	return result;
}
/** Loads elements from a file given by a filename, storing them in a given array fragment.
	*
	* @param filename a filename.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadBytes(final CharSequence filename, final byte[] array, final int offset, final int length) throws IOException {
	return loadBytes(new File(filename.toString()), array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, storing them in a given array.
	*
	* @param file a file.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadBytes(final File file, final byte[] array) throws IOException {
	return loadBytes(file, array, 0, array.length);
}
/** Loads elements from a file given by a filename, storing them in a given array.
	*
	* @param filename a filename.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadBytes(final CharSequence filename, final byte[] array) throws IOException {
	return loadBytes(new File(filename.toString()), array);
}
/** Loads elements from a file given by a {@link File} object, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @return an array filled with the content of the specified file.
	*/
public static byte[] loadBytes(final File file) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	final long length = channel.size();
	if (length > Integer.MAX_VALUE) {
	 channel.close();
	 throw new IllegalArgumentException("File too long: " + channel.size()+ " bytes (" + length + " elements)");
	}
	final byte[] array = new byte[(int)length];
	if (loadBytes(channel, array) < length) throw new EOFException();
	return array;
}
/** Loads elements from a file given by a filename, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @return an array filled with the content of the specified file.
	*/
public static byte[] loadBytes(final CharSequence filename) throws IOException {
	return loadBytes(new File(filename.toString()));
}
/** Stores an array fragment to a given data output.
	*
	* @param array an array whose elements will be written to {@code dataOutput}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param dataOutput a data output.
	*/
public static void storeBytes(final byte array[], final int offset, final int length, final DataOutput dataOutput) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	write(dataOutput, array, offset, length);
}
/** Stores an array to a given data output.
	*
	* @param array an array whose elements will be written to {@code dataOutput}.
	* @param dataOutput a data output.
	*/
public static void storeBytes(final byte array[], final DataOutput dataOutput) throws IOException {
	write(dataOutput, array, 0, array.length);
}
/** Stores an array fragment to a file given by a {@link File} object.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	*/
public static void storeBytes(final byte array[], final int offset, final int length, final File file) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	storeBytes(array, offset, length, channel);
	channel.close();
}
/** Stores an array fragment to a file given by a filename.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	*/
public static void storeBytes(final byte array[], final int offset, final int length, final CharSequence filename) throws IOException {
	storeBytes(array, offset, length, new File(filename.toString()));
}
/** Stores an array to a file given by a {@link File} object.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	*/
public static void storeBytes(final byte array[], final File file) throws IOException {
	storeBytes(array, 0, array.length, file);
}
/** Stores an array to a file given by a filename.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	*/
public static void storeBytes(final byte array[], final CharSequence filename) throws IOException {
	storeBytes(array, new File(filename.toString()));
}
/** Loads elements from a given data input, storing them in a given big-array fragment.
	*
	* @param dataInput a data input.
	* @param array a big array which will be filled with data from {@code dataInput}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code dataInput} (it might be less than {@code length} if {@code dataInput} ends).
	*/
public static long loadBytes(final DataInput dataInput, final byte[][] array, final long offset, final long length) throws IOException {
	ensureOffsetLength(array, offset, length);
	long c = 0;
	try {
	 for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	  final byte[] t = array[i];
	  final int l = (int)Math.min(t.length, offset + length - start(i));
	  for(int d = (int)Math.max(0, offset - start(i)); d < l; d++) {
	   t[d] = dataInput.readByte();
	   c++;
	  }
	 }
	}
	catch(EOFException itsOk) {}
	return c;
}
/** Loads elements from a given data input, storing them in a given big array.
	*
	* @param dataInput a data input.
	* @param array a big array which will be filled with data from {@code dataInput}.
	* @return the number of elements actually read from {@code dataInput} (it might be less than the array length if {@code dataInput} ends).
	*/
public static long loadBytes(final DataInput dataInput, final byte[][] array) throws IOException {
	long c = 0;
	try {
	 for(int i = 0; i < array.length; i++) {
	  final byte[] t = array[i];
	  final int l = t.length;
	  for(int d = 0; d < l; d++) {
	   t[d] = dataInput.readByte();
	   c++;
	  }
	 }
	}
	catch(EOFException itsOk) {}
	return c;
}
/** Loads elements from a file given by a {@link File} object, storing them in a given big-array fragment.
	*
	* @param file a file.
	* @param array a big array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadBytes(final File file, final byte[][] array, final long offset, final long length) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	final long read = loadBytes(channel, array, offset, length);
	return read;
}
/** Loads elements from a file given by a filename, storing them in a given big-array fragment.
	*
	* @param filename a filename.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadBytes(final CharSequence filename, final byte[][] array, final long offset, final long length) throws IOException {
	return loadBytes(new File(filename.toString()), array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, storing them in a given big array.
	*
	* @param file a file.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadBytes(final File file, final byte[][] array) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	final long read = loadBytes(channel, array);
	return read;
}
/** Loads elements from a file given by a filename, storing them in a given big array.
	*
	* @param filename a filename.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadBytes(final CharSequence filename, final byte[][] array) throws IOException {
	return loadBytes(new File(filename.toString()), array);
}
/** Loads elements from a file given by a {@link File} object, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @return a big array filled with the content of the specified file.
	*/
public static byte[][] loadBytesBig(final File file) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	final long length = channel.size();
	final byte[][] array = ByteBigArrays.newBigArray(length);
	loadBytes(channel, array);
	channel.close();
	return array;
}
/** Loads elements from a file given by a filename, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @return a big array filled with the content of the specified file.
	*/
public static byte[][] loadBytesBig(final CharSequence filename) throws IOException {
	return loadBytesBig(new File(filename.toString()));
}
/** Stores an array fragment to a given data output.
	*
	* @param array an array whose elements will be written to {@code dataOutput}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param dataOutput a data output.
	*/
public static void storeBytes(final byte array[][], final long offset, final long length, final DataOutput dataOutput) throws IOException {
	ensureOffsetLength(array, offset, length);
	write(dataOutput, array, offset, length);
}
/** Stores a big array to a given data output.
	*
	* @param array a big array whose elements will be written to {@code dataOutput}.
	* @param dataOutput a data output.
	*/
public static void storeBytes(final byte array[][], final DataOutput dataOutput) throws IOException {
	write(dataOutput, array, 0, length(array));
}
/** Stores a big-array fragment to a file given by a {@link File} object.
	*
	* @param array a big array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	*/
public static void storeBytes(final byte array[][], final long offset, final long length, final File file) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	storeBytes(array, offset, length, channel);
	channel.close();
}
/** Stores a big-array fragment to a file given by a filename.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	*/
public static void storeBytes(final byte array[][], final long offset, final long length, final CharSequence filename) throws IOException {
	storeBytes(array, offset, length, new File(filename.toString()));
}
/** Stores an array to a file given by a {@link File} object.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	*/
public static void storeBytes(final byte array[][], final File file) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	storeBytes(array, channel);
	channel.close();
}
/** Stores a big array to a file given by a filename.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	*/
public static void storeBytes(final byte array[][], final CharSequence filename) throws IOException {
	storeBytes(array, new File(filename.toString()));
}
/** Stores the element returned by an iterator to a given data output.
	*
	* @param i an iterator whose output will be written to {@code dataOutput}.
	* @param dataOutput a filename.
	*/
public static void storeBytes(final ByteIterator i, final DataOutput dataOutput) throws IOException {
	while(i.hasNext()) dataOutput.writeByte(i.nextByte());
}
/** Stores the element returned by an iterator to a file given by a {@link File} object.
	*
	* @param i an iterator whose output will be written to {@code file}.
	* @param file a file.
	*/
public static void storeBytes(final ByteIterator i, final File file) throws IOException {
	final DataOutputStream dos = new DataOutputStream(new FastBufferedOutputStream(new FileOutputStream(file)));
	while(i.hasNext()) dos.writeByte(i.nextByte());
	dos.close();
}
/** Stores the element returned by an iterator to a file given by a filename.
	*
	* @param i an iterator whose output will be written to the file {@code filename}.
	* @param filename a filename.
	*/
public static void storeBytes(final ByteIterator i, final CharSequence filename) throws IOException {
	storeBytes(i, new File(filename.toString()));
}
/** A wrapper that exhibits the content of a data input stream as a type-specific iterator. */
private static final class ByteDataInputWrapper implements ByteIterator {
	private final DataInput dataInput;
	private boolean toAdvance = true;
	private boolean endOfProcess = false;
	private byte next;
	public ByteDataInputWrapper(final DataInput dataInput) {
	 this.dataInput = dataInput;
	}
	@Override
	public boolean hasNext() {
	 if (! toAdvance) return ! endOfProcess;
	 toAdvance = false;
	 try { next = dataInput.readByte(); }
	 catch(EOFException eof) { endOfProcess = true; }
	 catch(IOException rethrow) { throw new RuntimeException(rethrow); }
	 return ! endOfProcess;
	}
	@Override
	public byte nextByte() {
	 if (! hasNext()) throw new NoSuchElementException();
	 toAdvance = true;
	 return next;
	}
}
/** Wraps the given data input stream into an iterator.
	*
	* @param dataInput a data input.
	*/
public static ByteIterator asByteIterator(final DataInput dataInput) {
	return new ByteDataInputWrapper(dataInput);
}
/** Wraps a file given by a {@link File} object into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param file a file.
	*/
public static ByteIterator asByteIterator(final File file) throws IOException {
	return new ByteDataInputWrapper(new DataInputStream(new FastBufferedInputStream(new FileInputStream(file))));
}
/** Wraps a file given by a filename into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param filename a filename.
	*/
public static ByteIterator asByteIterator(final CharSequence filename) throws IOException {
	return asByteIterator(new File(filename.toString()));
}
/** Wraps a file given by a {@link File} object into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param file a file.
	*/
public static ByteIterable asByteIterable(final File file) {
	return () -> {
	 try { return asByteIterator(file); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
/** Wraps a file given by a filename into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param filename a filename.
	*/
public static ByteIterable asByteIterable(final CharSequence filename) {
	return () -> {
	 try { return asByteIterator(filename); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
/* Generic definitions */
/* Assertions (useful to generate conditional code) */
/* Narrowing and widening */
/* Current type and class (and size, if applicable) */
/* Value methods */
/* Interfaces (keys) */
/* Interfaces (values) */
/* Types and methods related to primitive-type support in the JDK */
/* Abstract implementations (keys) */
/* Abstract implementations (values) */
/* Static containers (keys) */
/* Static containers (values) */
/* Implementations */
/* Synchronized wrappers */
/* Unmodifiable wrappers */
/* Other wrappers */
/* Methods (keys) */
/* Methods (values) */
/* Methods (keys/values) */
/* Methods that have special names depending on keys (but the special names depend on values) */
/* Equality */
/* Object/Reference-only definitions (keys) */
/* Primitive-type-only definitions (keys) */
/* Object/Reference-only definitions (values) */
/* START_OF_JAVA_SOURCE */
/*
	* Copyright (C) 2004-2024 Sebastiano Vigna
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
/** Loads elements from a given readable channel, using the given byte order, storing them in a given array fragment.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	* @param array an array which will be filled with data from {@code channel}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code channel} (it might be less than {@code length} if {@code channel} ends).
	*/
public static int loadChars(final ReadableByteChannel channel, final ByteOrder byteOrder, final char[] array, int offset, int length) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE).order(byteOrder);
	final CharBuffer buffer = byteBuffer.asCharBuffer();
	int read = 0;
	for (;;) {
	 byteBuffer.clear();
	 byteBuffer.limit((int)Math.min(buffer.capacity(), (long)length << CharMappedBigList.LOG2_BYTES));
	 int r = channel.read(byteBuffer);
	 if (r <= 0) return read;
	 r >>>= CharMappedBigList.LOG2_BYTES;
	 read += r;
	 // TODO: use the indexed get() method when switching to Java 13+
	 buffer.clear();
	 buffer.limit(r);
	 buffer.get(array, offset, r);
	 offset += r;
	 length -= r;
	}
}
/** Loads elements from a given readable channel, using the given byte order, storing them in a given array.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	* @param array an array which will be filled with data from {@code channel}.
	* @return the number of elements actually read from {@code channel} (it might be less than the array length if {@code channel} ends).
	*/
public static int loadChars(final ReadableByteChannel channel, final ByteOrder byteOrder, final char[] array) throws IOException {
	return loadChars(channel, byteOrder, array, 0, array.length);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a given array fragment.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadChars(final File file, final ByteOrder byteOrder, final char[] array, final int offset, final int length) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	final FileChannel channel = FileChannel.open(file.toPath());
	final int read = loadChars(channel, byteOrder, array, offset, length);
	channel.close();
	return read;
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a given array fragment.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadChars(final CharSequence filename, final ByteOrder byteOrder, final char[] array, final int offset, final int length) throws IOException {
	return loadChars(new File(filename.toString()), byteOrder, array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a given array.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadChars(final File file, final ByteOrder byteOrder, final char[] array) throws IOException {
	return loadChars(file, byteOrder, array, 0, array.length);
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a given array.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadChars(final CharSequence filename, final ByteOrder byteOrder, final char[] array) throws IOException {
	return loadChars(new File(filename.toString()), byteOrder, array);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @return an array filled with the content of the specified file.
	*/
public static char[] loadChars(final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	final long length = channel.size() / Character.BYTES;
	if (length > Integer.MAX_VALUE) {
	 channel.close();
	 throw new IllegalArgumentException("File too long: " + channel.size()+ " bytes (" + length + " elements)");
	}
	final char[] array = new char[(int)length];
	if (loadChars(channel, byteOrder, array) < length) throw new EOFException();
	channel.close();
	return array;
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @return an array filled with the content of the specified file.
	*/
public static char[] loadChars(final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	return loadChars(new File(filename.toString()), byteOrder);
}
/** Stores an array fragment to a given writable channel, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code channel}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeChars(final char array[], int offset, int length, final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE).order(byteOrder);
	final CharBuffer buffer = byteBuffer.asCharBuffer();
	while(length != 0) {
	 final int l = Math.min(length, buffer.capacity());
	 buffer.clear();
	 buffer.put(array, offset, l);
	 buffer.flip();
	 byteBuffer.clear();
	 byteBuffer.limit(buffer.limit() << CharMappedBigList.LOG2_BYTES);
	 channel.write(byteBuffer);
	 offset += l;
	 length -= l;
	}
}
/** Stores an array to a given writable channel, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code channel}.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeChars(final char array[], final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	storeChars(array, 0, array.length, channel, byteOrder);
}
/** Stores an array fragment to a file given by a {@link File} object, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeChars(final char array[], final int offset, final int length, final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	storeChars(array, offset, length, channel, byteOrder);
	channel.close();
}
/** Stores an array fragment to a file given by a filename, using the given byte order.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeChars(final char array[], final int offset, final int length, final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeChars(array, offset, length, new File(filename.toString()), byteOrder);
}
/** Stores an array to a file given by a {@link File} object, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeChars(final char array[], final File file, final ByteOrder byteOrder) throws IOException {
	storeChars(array, 0, array.length, file, byteOrder);
}
/** Stores an array to a file given by a filename, using the given byte order.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeChars(final char array[], final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeChars(array, new File(filename.toString()), byteOrder);
}
/** Loads elements from a given readable channel, using the given byte order, storing them in a given big-array fragment.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	* @param array a big array which will be filled with data from {@code channel}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code channel} (it might be less than {@code length} if {@code channel} ends).
	*/
public static long loadChars(final ReadableByteChannel channel, final ByteOrder byteOrder, final char[][] array, final long offset, final long length) throws IOException {
	ensureOffsetLength(array, offset, length);
	long read = 0;
	for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	 final char[] t = array[i];
	 final int s = (int)Math.max(0, offset - start(i));
	 final int e = (int)Math.min(t.length, offset + length - start(i));
	 final int r = loadChars(channel, byteOrder, t, s, e - s);
	 read += r;
	 if (r < e -s) break;
	}
	return read;
}
/** Loads elements from a given readable channel, using the given byte order, storing them in a given big array.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	* @param array a big array which will be filled with data from {@code channel}.
	* @return the number of elements actually read from {@code channel} (it might be less than the array length if {@code channel} ends).
	*/
public static long loadChars(final ReadableByteChannel channel, final ByteOrder byteOrder, final char[][] array) throws IOException {
	return loadChars(channel, byteOrder, array, 0, length(array));
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a given big-array fragment.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @param array a big array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadChars(final File file, final ByteOrder byteOrder, final char[][] array, final long offset, final long length) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	final long read = loadChars(channel, byteOrder, array, offset, length);
	channel.close();
	return read;
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a given big-array fragment.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadChars(final CharSequence filename, final ByteOrder byteOrder, final char[][] array, final long offset, final long length) throws IOException {
	return loadChars(new File(filename.toString()), byteOrder, array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a given big array.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadChars(final File file, final ByteOrder byteOrder, final char[][] array) throws IOException {
	return loadChars(file, byteOrder, array, 0, length(array));
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a given big array.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadChars(final CharSequence filename, final ByteOrder byteOrder, final char[][] array) throws IOException {
	return loadChars(new File(filename.toString()), byteOrder, array);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @return a big array filled with the content of the specified file.
	*/
public static char[][] loadCharsBig(final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	final long length = channel.size() / Character.BYTES;
	final char[][] array = CharBigArrays.newBigArray(length);
	for(final char[] t: array) loadChars(channel, byteOrder, t);
	channel.close();
	return array;
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @return a big array filled with the content of the specified file.
	*/
public static char[][] loadCharsBig(final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	return loadCharsBig(new File(filename.toString()), byteOrder);
}
/** Stores an array fragment to a given writable channel, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code channel}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeChars(final char array[][], final long offset, final long length, final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	 final int s = (int)Math.max(0, offset - start(i));
	 final int l = (int)Math.min(array[i].length, offset + length - start(i));
	 storeChars(array[i], s, l - s, channel, byteOrder);
	}
}
/** Stores a big array to a given writable channel, using the given byte order.
	*
	* @param array a big array whose elements will be written to {@code channel}.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeChars(final char array[][], final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	for(char[] t: array) storeChars(t, channel, byteOrder);
}
/** Stores a big-array fragment to a file given by a {@link File} object, using the given byte order.
	*
	* @param array a big array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeChars(final char array[][], final long offset, final long length, final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	storeChars(array, offset, length, channel, byteOrder);
	channel.close();
}
/** Stores a big-array fragment to a file given by a filename, using the given byte order.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeChars(final char array[][], final long offset, final long length, final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeChars(array, offset, length, new File(filename.toString()), byteOrder);
}
/** Stores an array to a file given by a {@link File} object, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeChars(final char array[][], final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	storeChars(array, channel, byteOrder);
	channel.close();
}
/** Stores a big array to a file given by a filename, using the given byte order.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeChars(final char array[][], final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeChars(array, new File(filename.toString()), byteOrder);
}
/** Stores the element returned by an iterator to a given writable channel, using the given byte order.
	*
	* @param i an iterator whose output will be written to {@code channel}.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeChars(final CharIterator i, final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE).order(byteOrder);
	final CharBuffer buffer = byteBuffer.asCharBuffer();
	while(i.hasNext()) {
	 if (! buffer.hasRemaining()) {
	  buffer.flip();
	  byteBuffer.clear();
	  byteBuffer.limit(buffer.limit() << CharMappedBigList.LOG2_BYTES);
	  channel.write(byteBuffer);
	  buffer.clear();
	 }
	 buffer.put(i.nextChar());
	}
	buffer.flip();
	byteBuffer.clear();
	byteBuffer.limit(buffer.limit() << CharMappedBigList.LOG2_BYTES);
	channel.write(byteBuffer);
}
/** Stores the element returned by an iterator to a file given by a {@link File} object, using the given byte order.
	*
	* @param i an iterator whose output will be written to {@code file}.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeChars(final CharIterator i, final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	storeChars(i, channel, byteOrder);
	channel.close();
}
/** Stores the element returned by an iterator to a file given by a filename, using the given byte order.
	*
	* @param i an iterator whose output will be written to the file {@code filename}.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeChars(final CharIterator i, final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeChars(i, new File(filename.toString()), byteOrder);
}
/** A wrapper that exhibits the content of a readable channel as a type-specific iterator. */
private static final class CharDataNioInputWrapper implements CharIterator {
	private final ReadableByteChannel channel;
	private final ByteBuffer byteBuffer;
	private final CharBuffer buffer;
	public CharDataNioInputWrapper(final ReadableByteChannel channel, final ByteOrder byteOrder) {
	 this.channel = channel;
	 byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE).order(byteOrder);
	 buffer = byteBuffer.asCharBuffer();
	 buffer.clear().flip();
	}
	@Override
	public boolean hasNext() {
	 if (! buffer.hasRemaining()) {
	  byteBuffer.clear();
	  try {
	   channel.read(byteBuffer);
	  } catch(IOException e) {
	   throw new RuntimeException(e);
	  }
	  byteBuffer.flip();
	  buffer.clear();
	  buffer.limit(byteBuffer.limit() >>> CharMappedBigList.LOG2_BYTES);
	 }
	 return buffer.hasRemaining();
	}
	@Override
	public char nextChar() {
	 if (! hasNext()) throw new NoSuchElementException();
	 return buffer.get();
	}
}
/** Wraps the given readable channel, using the given byte order, into an iterator.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	*/
public static CharIterator asCharIterator(final ReadableByteChannel channel, final ByteOrder byteOrder) {
	return new CharDataNioInputWrapper(channel, byteOrder);
}
/** Wraps a file given by a {@link File} object, using the given byte order, into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	*/
public static CharIterator asCharIterator(final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	return new CharDataNioInputWrapper(channel, byteOrder);
}
/** Wraps a file given by a filename, using the given byte order, into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	*/
public static CharIterator asCharIterator(final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	return asCharIterator(new File(filename.toString()), byteOrder);
}
/** Wraps a file given by a {@link File} object, using the given byte order, into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	*/
public static CharIterable asCharIterable(final File file, final ByteOrder byteOrder) {
	return () -> {
	 try { return asCharIterator(file, byteOrder); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
/** Wraps a file given by a filename, using the given byte order, into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	*/
public static CharIterable asCharIterable(final CharSequence filename, final ByteOrder byteOrder) {
	return () -> {
	 try { return asCharIterator(filename, byteOrder); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
/** Loads elements from a given data input, storing them in a given array fragment.
	*
	* @param dataInput a data input.
	* @param array an array which will be filled with data from {@code dataInput}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code dataInput} (it might be less than {@code length} if {@code dataInput} ends).
	*/
public static int loadChars(final DataInput dataInput, final char[] array, final int offset, final int length) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	int i = 0;
	try {
	 for(i = 0; i < length; i++) array[i + offset] = dataInput.readChar();
	}
	catch(EOFException itsOk) {}
	return i;
}
/** Loads elements from a given data input, storing them in a given array.
	*
	* @param dataInput a data input.
	* @param array an array which will be filled with data from {@code dataInput}.
	* @return the number of elements actually read from {@code dataInput} (it might be less than the array length if {@code dataInput} ends).
	*/
public static int loadChars(final DataInput dataInput, final char[] array) throws IOException {
	int i = 0;
	try {
	 final int length = array.length;
	 for(i = 0; i < length; i++) array[i] = dataInput.readChar();
	}
	catch(EOFException itsOk) {}
	return i;
}
/** Loads elements from a file given by a {@link File} object, storing them in a given array fragment.
	*
	* @param file a file.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadChars(final File file, final char[] array, final int offset, final int length) throws IOException {
	return loadChars(file, ByteOrder.BIG_ENDIAN, array, offset, length);
}
/** Loads elements from a file given by a filename, storing them in a given array fragment.
	*
	* @param filename a filename.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadChars(final CharSequence filename, final char[] array, final int offset, final int length) throws IOException {
	return loadChars(new File(filename.toString()), array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, storing them in a given array.
	*
	* @param file a file.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadChars(final File file, final char[] array) throws IOException {
	return loadChars(file, array, 0, array.length);
}
/** Loads elements from a file given by a filename, storing them in a given array.
	*
	* @param filename a filename.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadChars(final CharSequence filename, final char[] array) throws IOException {
	return loadChars(new File(filename.toString()), array);
}
/** Loads elements from a file given by a {@link File} object, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @return an array filled with the content of the specified file.
	*/
public static char[] loadChars(final File file) throws IOException {
	return loadChars(file, ByteOrder.BIG_ENDIAN);
}
/** Loads elements from a file given by a filename, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @return an array filled with the content of the specified file.
	*/
public static char[] loadChars(final CharSequence filename) throws IOException {
	return loadChars(new File(filename.toString()));
}
/** Stores an array fragment to a given data output.
	*
	* @param array an array whose elements will be written to {@code dataOutput}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param dataOutput a data output.
	*/
public static void storeChars(final char array[], final int offset, final int length, final DataOutput dataOutput) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	for(int i = 0; i < length; i++) dataOutput.writeChar(array[offset + i]);
}
/** Stores an array to a given data output.
	*
	* @param array an array whose elements will be written to {@code dataOutput}.
	* @param dataOutput a data output.
	*/
public static void storeChars(final char array[], final DataOutput dataOutput) throws IOException {
	final int length = array.length;
	for(int i = 0; i < length; i++) dataOutput.writeChar(array[i]);
}
/** Stores an array fragment to a file given by a {@link File} object.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	*/
public static void storeChars(final char array[], final int offset, final int length, final File file) throws IOException {
	storeChars(array, offset, length, file, ByteOrder.BIG_ENDIAN);
}
/** Stores an array fragment to a file given by a filename.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	*/
public static void storeChars(final char array[], final int offset, final int length, final CharSequence filename) throws IOException {
	storeChars(array, offset, length, new File(filename.toString()));
}
/** Stores an array to a file given by a {@link File} object.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	*/
public static void storeChars(final char array[], final File file) throws IOException {
	storeChars(array, 0, array.length, file);
}
/** Stores an array to a file given by a filename.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	*/
public static void storeChars(final char array[], final CharSequence filename) throws IOException {
	storeChars(array, new File(filename.toString()));
}
/** Loads elements from a given data input, storing them in a given big-array fragment.
	*
	* @param dataInput a data input.
	* @param array a big array which will be filled with data from {@code dataInput}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code dataInput} (it might be less than {@code length} if {@code dataInput} ends).
	*/
public static long loadChars(final DataInput dataInput, final char[][] array, final long offset, final long length) throws IOException {
	ensureOffsetLength(array, offset, length);
	long c = 0;
	try {
	 for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	  final char[] t = array[i];
	  final int l = (int)Math.min(t.length, offset + length - start(i));
	  for(int d = (int)Math.max(0, offset - start(i)); d < l; d++) {
	   t[d] = dataInput.readChar();
	   c++;
	  }
	 }
	}
	catch(EOFException itsOk) {}
	return c;
}
/** Loads elements from a given data input, storing them in a given big array.
	*
	* @param dataInput a data input.
	* @param array a big array which will be filled with data from {@code dataInput}.
	* @return the number of elements actually read from {@code dataInput} (it might be less than the array length if {@code dataInput} ends).
	*/
public static long loadChars(final DataInput dataInput, final char[][] array) throws IOException {
	long c = 0;
	try {
	 for(int i = 0; i < array.length; i++) {
	  final char[] t = array[i];
	  final int l = t.length;
	  for(int d = 0; d < l; d++) {
	   t[d] = dataInput.readChar();
	   c++;
	  }
	 }
	}
	catch(EOFException itsOk) {}
	return c;
}
/** Loads elements from a file given by a {@link File} object, storing them in a given big-array fragment.
	*
	* @param file a file.
	* @param array a big array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadChars(final File file, final char[][] array, final long offset, final long length) throws IOException {
	return loadChars(file, ByteOrder.BIG_ENDIAN, array, offset, length);
}
/** Loads elements from a file given by a filename, storing them in a given big-array fragment.
	*
	* @param filename a filename.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadChars(final CharSequence filename, final char[][] array, final long offset, final long length) throws IOException {
	return loadChars(new File(filename.toString()), array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, storing them in a given big array.
	*
	* @param file a file.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadChars(final File file, final char[][] array) throws IOException {
	return loadChars(file, ByteOrder.BIG_ENDIAN, array);
}
/** Loads elements from a file given by a filename, storing them in a given big array.
	*
	* @param filename a filename.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadChars(final CharSequence filename, final char[][] array) throws IOException {
	return loadChars(new File(filename.toString()), array);
}
/** Loads elements from a file given by a {@link File} object, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @return a big array filled with the content of the specified file.
	*/
public static char[][] loadCharsBig(final File file) throws IOException {
	return loadCharsBig(file, ByteOrder.BIG_ENDIAN);
}
/** Loads elements from a file given by a filename, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @return a big array filled with the content of the specified file.
	*/
public static char[][] loadCharsBig(final CharSequence filename) throws IOException {
	return loadCharsBig(new File(filename.toString()));
}
/** Stores an array fragment to a given data output.
	*
	* @param array an array whose elements will be written to {@code dataOutput}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param dataOutput a data output.
	*/
public static void storeChars(final char array[][], final long offset, final long length, final DataOutput dataOutput) throws IOException {
	ensureOffsetLength(array, offset, length);
	for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	 final char[] t = array[i];
	 final int l = (int)Math.min(t.length, offset + length - start(i));
	 for(int d = (int)Math.max(0, offset - start(i)); d < l; d++) dataOutput.writeChar(t[d]);
	}
}
/** Stores a big array to a given data output.
	*
	* @param array a big array whose elements will be written to {@code dataOutput}.
	* @param dataOutput a data output.
	*/
public static void storeChars(final char array[][], final DataOutput dataOutput) throws IOException {
	for(int i = 0; i < array.length; i++) {
	 final char[] t = array[i];
	 final int l = t.length;
	 for(int d = 0; d < l; d++) dataOutput.writeChar(t[d]);
	}
}
/** Stores a big-array fragment to a file given by a {@link File} object.
	*
	* @param array a big array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	*/
public static void storeChars(final char array[][], final long offset, final long length, final File file) throws IOException {
	storeChars(array, offset, length, file, ByteOrder.BIG_ENDIAN);
}
/** Stores a big-array fragment to a file given by a filename.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	*/
public static void storeChars(final char array[][], final long offset, final long length, final CharSequence filename) throws IOException {
	storeChars(array, offset, length, new File(filename.toString()));
}
/** Stores an array to a file given by a {@link File} object.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	*/
public static void storeChars(final char array[][], final File file) throws IOException {
	storeChars(array, file, ByteOrder.BIG_ENDIAN);
}
/** Stores a big array to a file given by a filename.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	*/
public static void storeChars(final char array[][], final CharSequence filename) throws IOException {
	storeChars(array, new File(filename.toString()));
}
/** Stores the element returned by an iterator to a given data output.
	*
	* @param i an iterator whose output will be written to {@code dataOutput}.
	* @param dataOutput a filename.
	*/
public static void storeChars(final CharIterator i, final DataOutput dataOutput) throws IOException {
	while(i.hasNext()) dataOutput.writeChar(i.nextChar());
}
/** Stores the element returned by an iterator to a file given by a {@link File} object.
	*
	* @param i an iterator whose output will be written to {@code file}.
	* @param file a file.
	*/
public static void storeChars(final CharIterator i, final File file) throws IOException {
	storeChars(i, file, ByteOrder.BIG_ENDIAN);
}
/** Stores the element returned by an iterator to a file given by a filename.
	*
	* @param i an iterator whose output will be written to the file {@code filename}.
	* @param filename a filename.
	*/
public static void storeChars(final CharIterator i, final CharSequence filename) throws IOException {
	storeChars(i, new File(filename.toString()));
}
/** A wrapper that exhibits the content of a data input stream as a type-specific iterator. */
private static final class CharDataInputWrapper implements CharIterator {
	private final DataInput dataInput;
	private boolean toAdvance = true;
	private boolean endOfProcess = false;
	private char next;
	public CharDataInputWrapper(final DataInput dataInput) {
	 this.dataInput = dataInput;
	}
	@Override
	public boolean hasNext() {
	 if (! toAdvance) return ! endOfProcess;
	 toAdvance = false;
	 try { next = dataInput.readChar(); }
	 catch(EOFException eof) { endOfProcess = true; }
	 catch(IOException rethrow) { throw new RuntimeException(rethrow); }
	 return ! endOfProcess;
	}
	@Override
	public char nextChar() {
	 if (! hasNext()) throw new NoSuchElementException();
	 toAdvance = true;
	 return next;
	}
}
/** Wraps the given data input stream into an iterator.
	*
	* @param dataInput a data input.
	*/
public static CharIterator asCharIterator(final DataInput dataInput) {
	return new CharDataInputWrapper(dataInput);
}
/** Wraps a file given by a {@link File} object into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param file a file.
	*/
public static CharIterator asCharIterator(final File file) throws IOException {
	return asCharIterator(file, ByteOrder.BIG_ENDIAN);
}
/** Wraps a file given by a filename into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param filename a filename.
	*/
public static CharIterator asCharIterator(final CharSequence filename) throws IOException {
	return asCharIterator(new File(filename.toString()));
}
/** Wraps a file given by a {@link File} object into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param file a file.
	*/
public static CharIterable asCharIterable(final File file) {
	return () -> {
	 try { return asCharIterator(file); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
/** Wraps a file given by a filename into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param filename a filename.
	*/
public static CharIterable asCharIterable(final CharSequence filename) {
	return () -> {
	 try { return asCharIterator(filename); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
/* Generic definitions */
/* Assertions (useful to generate conditional code) */
/* Narrowing and widening */
/* Current type and class (and size, if applicable) */
/* Value methods */
/* Interfaces (keys) */
/* Interfaces (values) */
/* Types and methods related to primitive-type support in the JDK */
/* Abstract implementations (keys) */
/* Abstract implementations (values) */
/* Static containers (keys) */
/* Static containers (values) */
/* Implementations */
/* Synchronized wrappers */
/* Unmodifiable wrappers */
/* Other wrappers */
/* Methods (keys) */
/* Methods (values) */
/* Methods (keys/values) */
/* Methods that have special names depending on keys (but the special names depend on values) */
/* Equality */
/* Object/Reference-only definitions (keys) */
/* Primitive-type-only definitions (keys) */
/* Object/Reference-only definitions (values) */
/* START_OF_JAVA_SOURCE */
/*
	* Copyright (C) 2004-2024 Sebastiano Vigna
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
/** Loads elements from a given readable channel, using the given byte order, storing them in a given array fragment.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	* @param array an array which will be filled with data from {@code channel}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code channel} (it might be less than {@code length} if {@code channel} ends).
	*/
public static int loadShorts(final ReadableByteChannel channel, final ByteOrder byteOrder, final short[] array, int offset, int length) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE).order(byteOrder);
	final ShortBuffer buffer = byteBuffer.asShortBuffer();
	int read = 0;
	for (;;) {
	 byteBuffer.clear();
	 byteBuffer.limit((int)Math.min(buffer.capacity(), (long)length << ShortMappedBigList.LOG2_BYTES));
	 int r = channel.read(byteBuffer);
	 if (r <= 0) return read;
	 r >>>= ShortMappedBigList.LOG2_BYTES;
	 read += r;
	 // TODO: use the indexed get() method when switching to Java 13+
	 buffer.clear();
	 buffer.limit(r);
	 buffer.get(array, offset, r);
	 offset += r;
	 length -= r;
	}
}
/** Loads elements from a given readable channel, using the given byte order, storing them in a given array.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	* @param array an array which will be filled with data from {@code channel}.
	* @return the number of elements actually read from {@code channel} (it might be less than the array length if {@code channel} ends).
	*/
public static int loadShorts(final ReadableByteChannel channel, final ByteOrder byteOrder, final short[] array) throws IOException {
	return loadShorts(channel, byteOrder, array, 0, array.length);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a given array fragment.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadShorts(final File file, final ByteOrder byteOrder, final short[] array, final int offset, final int length) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	final FileChannel channel = FileChannel.open(file.toPath());
	final int read = loadShorts(channel, byteOrder, array, offset, length);
	channel.close();
	return read;
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a given array fragment.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadShorts(final CharSequence filename, final ByteOrder byteOrder, final short[] array, final int offset, final int length) throws IOException {
	return loadShorts(new File(filename.toString()), byteOrder, array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a given array.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadShorts(final File file, final ByteOrder byteOrder, final short[] array) throws IOException {
	return loadShorts(file, byteOrder, array, 0, array.length);
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a given array.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadShorts(final CharSequence filename, final ByteOrder byteOrder, final short[] array) throws IOException {
	return loadShorts(new File(filename.toString()), byteOrder, array);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @return an array filled with the content of the specified file.
	*/
public static short[] loadShorts(final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	final long length = channel.size() / Short.BYTES;
	if (length > Integer.MAX_VALUE) {
	 channel.close();
	 throw new IllegalArgumentException("File too long: " + channel.size()+ " bytes (" + length + " elements)");
	}
	final short[] array = new short[(int)length];
	if (loadShorts(channel, byteOrder, array) < length) throw new EOFException();
	channel.close();
	return array;
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @return an array filled with the content of the specified file.
	*/
public static short[] loadShorts(final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	return loadShorts(new File(filename.toString()), byteOrder);
}
/** Stores an array fragment to a given writable channel, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code channel}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeShorts(final short array[], int offset, int length, final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE).order(byteOrder);
	final ShortBuffer buffer = byteBuffer.asShortBuffer();
	while(length != 0) {
	 final int l = Math.min(length, buffer.capacity());
	 buffer.clear();
	 buffer.put(array, offset, l);
	 buffer.flip();
	 byteBuffer.clear();
	 byteBuffer.limit(buffer.limit() << ShortMappedBigList.LOG2_BYTES);
	 channel.write(byteBuffer);
	 offset += l;
	 length -= l;
	}
}
/** Stores an array to a given writable channel, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code channel}.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeShorts(final short array[], final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	storeShorts(array, 0, array.length, channel, byteOrder);
}
/** Stores an array fragment to a file given by a {@link File} object, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeShorts(final short array[], final int offset, final int length, final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	storeShorts(array, offset, length, channel, byteOrder);
	channel.close();
}
/** Stores an array fragment to a file given by a filename, using the given byte order.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeShorts(final short array[], final int offset, final int length, final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeShorts(array, offset, length, new File(filename.toString()), byteOrder);
}
/** Stores an array to a file given by a {@link File} object, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeShorts(final short array[], final File file, final ByteOrder byteOrder) throws IOException {
	storeShorts(array, 0, array.length, file, byteOrder);
}
/** Stores an array to a file given by a filename, using the given byte order.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeShorts(final short array[], final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeShorts(array, new File(filename.toString()), byteOrder);
}
/** Loads elements from a given readable channel, using the given byte order, storing them in a given big-array fragment.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	* @param array a big array which will be filled with data from {@code channel}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code channel} (it might be less than {@code length} if {@code channel} ends).
	*/
public static long loadShorts(final ReadableByteChannel channel, final ByteOrder byteOrder, final short[][] array, final long offset, final long length) throws IOException {
	ensureOffsetLength(array, offset, length);
	long read = 0;
	for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	 final short[] t = array[i];
	 final int s = (int)Math.max(0, offset - start(i));
	 final int e = (int)Math.min(t.length, offset + length - start(i));
	 final int r = loadShorts(channel, byteOrder, t, s, e - s);
	 read += r;
	 if (r < e -s) break;
	}
	return read;
}
/** Loads elements from a given readable channel, using the given byte order, storing them in a given big array.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	* @param array a big array which will be filled with data from {@code channel}.
	* @return the number of elements actually read from {@code channel} (it might be less than the array length if {@code channel} ends).
	*/
public static long loadShorts(final ReadableByteChannel channel, final ByteOrder byteOrder, final short[][] array) throws IOException {
	return loadShorts(channel, byteOrder, array, 0, length(array));
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a given big-array fragment.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @param array a big array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadShorts(final File file, final ByteOrder byteOrder, final short[][] array, final long offset, final long length) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	final long read = loadShorts(channel, byteOrder, array, offset, length);
	channel.close();
	return read;
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a given big-array fragment.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadShorts(final CharSequence filename, final ByteOrder byteOrder, final short[][] array, final long offset, final long length) throws IOException {
	return loadShorts(new File(filename.toString()), byteOrder, array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a given big array.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadShorts(final File file, final ByteOrder byteOrder, final short[][] array) throws IOException {
	return loadShorts(file, byteOrder, array, 0, length(array));
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a given big array.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadShorts(final CharSequence filename, final ByteOrder byteOrder, final short[][] array) throws IOException {
	return loadShorts(new File(filename.toString()), byteOrder, array);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @return a big array filled with the content of the specified file.
	*/
public static short[][] loadShortsBig(final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	final long length = channel.size() / Short.BYTES;
	final short[][] array = ShortBigArrays.newBigArray(length);
	for(final short[] t: array) loadShorts(channel, byteOrder, t);
	channel.close();
	return array;
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @return a big array filled with the content of the specified file.
	*/
public static short[][] loadShortsBig(final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	return loadShortsBig(new File(filename.toString()), byteOrder);
}
/** Stores an array fragment to a given writable channel, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code channel}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeShorts(final short array[][], final long offset, final long length, final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	 final int s = (int)Math.max(0, offset - start(i));
	 final int l = (int)Math.min(array[i].length, offset + length - start(i));
	 storeShorts(array[i], s, l - s, channel, byteOrder);
	}
}
/** Stores a big array to a given writable channel, using the given byte order.
	*
	* @param array a big array whose elements will be written to {@code channel}.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeShorts(final short array[][], final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	for(short[] t: array) storeShorts(t, channel, byteOrder);
}
/** Stores a big-array fragment to a file given by a {@link File} object, using the given byte order.
	*
	* @param array a big array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeShorts(final short array[][], final long offset, final long length, final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	storeShorts(array, offset, length, channel, byteOrder);
	channel.close();
}
/** Stores a big-array fragment to a file given by a filename, using the given byte order.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeShorts(final short array[][], final long offset, final long length, final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeShorts(array, offset, length, new File(filename.toString()), byteOrder);
}
/** Stores an array to a file given by a {@link File} object, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeShorts(final short array[][], final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	storeShorts(array, channel, byteOrder);
	channel.close();
}
/** Stores a big array to a file given by a filename, using the given byte order.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeShorts(final short array[][], final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeShorts(array, new File(filename.toString()), byteOrder);
}
/** Stores the element returned by an iterator to a given writable channel, using the given byte order.
	*
	* @param i an iterator whose output will be written to {@code channel}.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeShorts(final ShortIterator i, final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE).order(byteOrder);
	final ShortBuffer buffer = byteBuffer.asShortBuffer();
	while(i.hasNext()) {
	 if (! buffer.hasRemaining()) {
	  buffer.flip();
	  byteBuffer.clear();
	  byteBuffer.limit(buffer.limit() << ShortMappedBigList.LOG2_BYTES);
	  channel.write(byteBuffer);
	  buffer.clear();
	 }
	 buffer.put(i.nextShort());
	}
	buffer.flip();
	byteBuffer.clear();
	byteBuffer.limit(buffer.limit() << ShortMappedBigList.LOG2_BYTES);
	channel.write(byteBuffer);
}
/** Stores the element returned by an iterator to a file given by a {@link File} object, using the given byte order.
	*
	* @param i an iterator whose output will be written to {@code file}.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeShorts(final ShortIterator i, final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	storeShorts(i, channel, byteOrder);
	channel.close();
}
/** Stores the element returned by an iterator to a file given by a filename, using the given byte order.
	*
	* @param i an iterator whose output will be written to the file {@code filename}.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeShorts(final ShortIterator i, final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeShorts(i, new File(filename.toString()), byteOrder);
}
/** A wrapper that exhibits the content of a readable channel as a type-specific iterator. */
private static final class ShortDataNioInputWrapper implements ShortIterator {
	private final ReadableByteChannel channel;
	private final ByteBuffer byteBuffer;
	private final ShortBuffer buffer;
	public ShortDataNioInputWrapper(final ReadableByteChannel channel, final ByteOrder byteOrder) {
	 this.channel = channel;
	 byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE).order(byteOrder);
	 buffer = byteBuffer.asShortBuffer();
	 buffer.clear().flip();
	}
	@Override
	public boolean hasNext() {
	 if (! buffer.hasRemaining()) {
	  byteBuffer.clear();
	  try {
	   channel.read(byteBuffer);
	  } catch(IOException e) {
	   throw new RuntimeException(e);
	  }
	  byteBuffer.flip();
	  buffer.clear();
	  buffer.limit(byteBuffer.limit() >>> ShortMappedBigList.LOG2_BYTES);
	 }
	 return buffer.hasRemaining();
	}
	@Override
	public short nextShort() {
	 if (! hasNext()) throw new NoSuchElementException();
	 return buffer.get();
	}
}
/** Wraps the given readable channel, using the given byte order, into an iterator.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	*/
public static ShortIterator asShortIterator(final ReadableByteChannel channel, final ByteOrder byteOrder) {
	return new ShortDataNioInputWrapper(channel, byteOrder);
}
/** Wraps a file given by a {@link File} object, using the given byte order, into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	*/
public static ShortIterator asShortIterator(final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	return new ShortDataNioInputWrapper(channel, byteOrder);
}
/** Wraps a file given by a filename, using the given byte order, into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	*/
public static ShortIterator asShortIterator(final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	return asShortIterator(new File(filename.toString()), byteOrder);
}
/** Wraps a file given by a {@link File} object, using the given byte order, into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	*/
public static ShortIterable asShortIterable(final File file, final ByteOrder byteOrder) {
	return () -> {
	 try { return asShortIterator(file, byteOrder); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
/** Wraps a file given by a filename, using the given byte order, into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	*/
public static ShortIterable asShortIterable(final CharSequence filename, final ByteOrder byteOrder) {
	return () -> {
	 try { return asShortIterator(filename, byteOrder); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
/** Loads elements from a given data input, storing them in a given array fragment.
	*
	* @param dataInput a data input.
	* @param array an array which will be filled with data from {@code dataInput}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code dataInput} (it might be less than {@code length} if {@code dataInput} ends).
	*/
public static int loadShorts(final DataInput dataInput, final short[] array, final int offset, final int length) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	int i = 0;
	try {
	 for(i = 0; i < length; i++) array[i + offset] = dataInput.readShort();
	}
	catch(EOFException itsOk) {}
	return i;
}
/** Loads elements from a given data input, storing them in a given array.
	*
	* @param dataInput a data input.
	* @param array an array which will be filled with data from {@code dataInput}.
	* @return the number of elements actually read from {@code dataInput} (it might be less than the array length if {@code dataInput} ends).
	*/
public static int loadShorts(final DataInput dataInput, final short[] array) throws IOException {
	int i = 0;
	try {
	 final int length = array.length;
	 for(i = 0; i < length; i++) array[i] = dataInput.readShort();
	}
	catch(EOFException itsOk) {}
	return i;
}
/** Loads elements from a file given by a {@link File} object, storing them in a given array fragment.
	*
	* @param file a file.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadShorts(final File file, final short[] array, final int offset, final int length) throws IOException {
	return loadShorts(file, ByteOrder.BIG_ENDIAN, array, offset, length);
}
/** Loads elements from a file given by a filename, storing them in a given array fragment.
	*
	* @param filename a filename.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadShorts(final CharSequence filename, final short[] array, final int offset, final int length) throws IOException {
	return loadShorts(new File(filename.toString()), array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, storing them in a given array.
	*
	* @param file a file.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadShorts(final File file, final short[] array) throws IOException {
	return loadShorts(file, array, 0, array.length);
}
/** Loads elements from a file given by a filename, storing them in a given array.
	*
	* @param filename a filename.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadShorts(final CharSequence filename, final short[] array) throws IOException {
	return loadShorts(new File(filename.toString()), array);
}
/** Loads elements from a file given by a {@link File} object, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @return an array filled with the content of the specified file.
	*/
public static short[] loadShorts(final File file) throws IOException {
	return loadShorts(file, ByteOrder.BIG_ENDIAN);
}
/** Loads elements from a file given by a filename, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @return an array filled with the content of the specified file.
	*/
public static short[] loadShorts(final CharSequence filename) throws IOException {
	return loadShorts(new File(filename.toString()));
}
/** Stores an array fragment to a given data output.
	*
	* @param array an array whose elements will be written to {@code dataOutput}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param dataOutput a data output.
	*/
public static void storeShorts(final short array[], final int offset, final int length, final DataOutput dataOutput) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	for(int i = 0; i < length; i++) dataOutput.writeShort(array[offset + i]);
}
/** Stores an array to a given data output.
	*
	* @param array an array whose elements will be written to {@code dataOutput}.
	* @param dataOutput a data output.
	*/
public static void storeShorts(final short array[], final DataOutput dataOutput) throws IOException {
	final int length = array.length;
	for(int i = 0; i < length; i++) dataOutput.writeShort(array[i]);
}
/** Stores an array fragment to a file given by a {@link File} object.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	*/
public static void storeShorts(final short array[], final int offset, final int length, final File file) throws IOException {
	storeShorts(array, offset, length, file, ByteOrder.BIG_ENDIAN);
}
/** Stores an array fragment to a file given by a filename.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	*/
public static void storeShorts(final short array[], final int offset, final int length, final CharSequence filename) throws IOException {
	storeShorts(array, offset, length, new File(filename.toString()));
}
/** Stores an array to a file given by a {@link File} object.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	*/
public static void storeShorts(final short array[], final File file) throws IOException {
	storeShorts(array, 0, array.length, file);
}
/** Stores an array to a file given by a filename.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	*/
public static void storeShorts(final short array[], final CharSequence filename) throws IOException {
	storeShorts(array, new File(filename.toString()));
}
/** Loads elements from a given data input, storing them in a given big-array fragment.
	*
	* @param dataInput a data input.
	* @param array a big array which will be filled with data from {@code dataInput}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code dataInput} (it might be less than {@code length} if {@code dataInput} ends).
	*/
public static long loadShorts(final DataInput dataInput, final short[][] array, final long offset, final long length) throws IOException {
	ensureOffsetLength(array, offset, length);
	long c = 0;
	try {
	 for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	  final short[] t = array[i];
	  final int l = (int)Math.min(t.length, offset + length - start(i));
	  for(int d = (int)Math.max(0, offset - start(i)); d < l; d++) {
	   t[d] = dataInput.readShort();
	   c++;
	  }
	 }
	}
	catch(EOFException itsOk) {}
	return c;
}
/** Loads elements from a given data input, storing them in a given big array.
	*
	* @param dataInput a data input.
	* @param array a big array which will be filled with data from {@code dataInput}.
	* @return the number of elements actually read from {@code dataInput} (it might be less than the array length if {@code dataInput} ends).
	*/
public static long loadShorts(final DataInput dataInput, final short[][] array) throws IOException {
	long c = 0;
	try {
	 for(int i = 0; i < array.length; i++) {
	  final short[] t = array[i];
	  final int l = t.length;
	  for(int d = 0; d < l; d++) {
	   t[d] = dataInput.readShort();
	   c++;
	  }
	 }
	}
	catch(EOFException itsOk) {}
	return c;
}
/** Loads elements from a file given by a {@link File} object, storing them in a given big-array fragment.
	*
	* @param file a file.
	* @param array a big array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadShorts(final File file, final short[][] array, final long offset, final long length) throws IOException {
	return loadShorts(file, ByteOrder.BIG_ENDIAN, array, offset, length);
}
/** Loads elements from a file given by a filename, storing them in a given big-array fragment.
	*
	* @param filename a filename.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadShorts(final CharSequence filename, final short[][] array, final long offset, final long length) throws IOException {
	return loadShorts(new File(filename.toString()), array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, storing them in a given big array.
	*
	* @param file a file.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadShorts(final File file, final short[][] array) throws IOException {
	return loadShorts(file, ByteOrder.BIG_ENDIAN, array);
}
/** Loads elements from a file given by a filename, storing them in a given big array.
	*
	* @param filename a filename.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadShorts(final CharSequence filename, final short[][] array) throws IOException {
	return loadShorts(new File(filename.toString()), array);
}
/** Loads elements from a file given by a {@link File} object, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @return a big array filled with the content of the specified file.
	*/
public static short[][] loadShortsBig(final File file) throws IOException {
	return loadShortsBig(file, ByteOrder.BIG_ENDIAN);
}
/** Loads elements from a file given by a filename, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @return a big array filled with the content of the specified file.
	*/
public static short[][] loadShortsBig(final CharSequence filename) throws IOException {
	return loadShortsBig(new File(filename.toString()));
}
/** Stores an array fragment to a given data output.
	*
	* @param array an array whose elements will be written to {@code dataOutput}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param dataOutput a data output.
	*/
public static void storeShorts(final short array[][], final long offset, final long length, final DataOutput dataOutput) throws IOException {
	ensureOffsetLength(array, offset, length);
	for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	 final short[] t = array[i];
	 final int l = (int)Math.min(t.length, offset + length - start(i));
	 for(int d = (int)Math.max(0, offset - start(i)); d < l; d++) dataOutput.writeShort(t[d]);
	}
}
/** Stores a big array to a given data output.
	*
	* @param array a big array whose elements will be written to {@code dataOutput}.
	* @param dataOutput a data output.
	*/
public static void storeShorts(final short array[][], final DataOutput dataOutput) throws IOException {
	for(int i = 0; i < array.length; i++) {
	 final short[] t = array[i];
	 final int l = t.length;
	 for(int d = 0; d < l; d++) dataOutput.writeShort(t[d]);
	}
}
/** Stores a big-array fragment to a file given by a {@link File} object.
	*
	* @param array a big array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	*/
public static void storeShorts(final short array[][], final long offset, final long length, final File file) throws IOException {
	storeShorts(array, offset, length, file, ByteOrder.BIG_ENDIAN);
}
/** Stores a big-array fragment to a file given by a filename.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	*/
public static void storeShorts(final short array[][], final long offset, final long length, final CharSequence filename) throws IOException {
	storeShorts(array, offset, length, new File(filename.toString()));
}
/** Stores an array to a file given by a {@link File} object.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	*/
public static void storeShorts(final short array[][], final File file) throws IOException {
	storeShorts(array, file, ByteOrder.BIG_ENDIAN);
}
/** Stores a big array to a file given by a filename.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	*/
public static void storeShorts(final short array[][], final CharSequence filename) throws IOException {
	storeShorts(array, new File(filename.toString()));
}
/** Stores the element returned by an iterator to a given data output.
	*
	* @param i an iterator whose output will be written to {@code dataOutput}.
	* @param dataOutput a filename.
	*/
public static void storeShorts(final ShortIterator i, final DataOutput dataOutput) throws IOException {
	while(i.hasNext()) dataOutput.writeShort(i.nextShort());
}
/** Stores the element returned by an iterator to a file given by a {@link File} object.
	*
	* @param i an iterator whose output will be written to {@code file}.
	* @param file a file.
	*/
public static void storeShorts(final ShortIterator i, final File file) throws IOException {
	storeShorts(i, file, ByteOrder.BIG_ENDIAN);
}
/** Stores the element returned by an iterator to a file given by a filename.
	*
	* @param i an iterator whose output will be written to the file {@code filename}.
	* @param filename a filename.
	*/
public static void storeShorts(final ShortIterator i, final CharSequence filename) throws IOException {
	storeShorts(i, new File(filename.toString()));
}
/** A wrapper that exhibits the content of a data input stream as a type-specific iterator. */
private static final class ShortDataInputWrapper implements ShortIterator {
	private final DataInput dataInput;
	private boolean toAdvance = true;
	private boolean endOfProcess = false;
	private short next;
	public ShortDataInputWrapper(final DataInput dataInput) {
	 this.dataInput = dataInput;
	}
	@Override
	public boolean hasNext() {
	 if (! toAdvance) return ! endOfProcess;
	 toAdvance = false;
	 try { next = dataInput.readShort(); }
	 catch(EOFException eof) { endOfProcess = true; }
	 catch(IOException rethrow) { throw new RuntimeException(rethrow); }
	 return ! endOfProcess;
	}
	@Override
	public short nextShort() {
	 if (! hasNext()) throw new NoSuchElementException();
	 toAdvance = true;
	 return next;
	}
}
/** Wraps the given data input stream into an iterator.
	*
	* @param dataInput a data input.
	*/
public static ShortIterator asShortIterator(final DataInput dataInput) {
	return new ShortDataInputWrapper(dataInput);
}
/** Wraps a file given by a {@link File} object into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param file a file.
	*/
public static ShortIterator asShortIterator(final File file) throws IOException {
	return asShortIterator(file, ByteOrder.BIG_ENDIAN);
}
/** Wraps a file given by a filename into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param filename a filename.
	*/
public static ShortIterator asShortIterator(final CharSequence filename) throws IOException {
	return asShortIterator(new File(filename.toString()));
}
/** Wraps a file given by a {@link File} object into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param file a file.
	*/
public static ShortIterable asShortIterable(final File file) {
	return () -> {
	 try { return asShortIterator(file); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
/** Wraps a file given by a filename into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param filename a filename.
	*/
public static ShortIterable asShortIterable(final CharSequence filename) {
	return () -> {
	 try { return asShortIterator(filename); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
/* Generic definitions */
/* Assertions (useful to generate conditional code) */
/* Narrowing and widening */
/* Current type and class (and size, if applicable) */
/* Value methods */
/* Interfaces (keys) */
/* Interfaces (values) */
/* Types and methods related to primitive-type support in the JDK */
/* Abstract implementations (keys) */
/* Abstract implementations (values) */
/* Static containers (keys) */
/* Static containers (values) */
/* Implementations */
/* Synchronized wrappers */
/* Unmodifiable wrappers */
/* Other wrappers */
/* Methods (keys) */
/* Methods (values) */
/* Methods (keys/values) */
/* Methods that have special names depending on keys (but the special names depend on values) */
/* Equality */
/* Object/Reference-only definitions (keys) */
/* Primitive-type-only definitions (keys) */
/* Object/Reference-only definitions (values) */
/* START_OF_JAVA_SOURCE */
/*
	* Copyright (C) 2004-2024 Sebastiano Vigna
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
/** Loads elements from a given readable channel, using the given byte order, storing them in a given array fragment.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	* @param array an array which will be filled with data from {@code channel}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code channel} (it might be less than {@code length} if {@code channel} ends).
	*/
public static int loadInts(final ReadableByteChannel channel, final ByteOrder byteOrder, final int[] array, int offset, int length) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE).order(byteOrder);
	final IntBuffer buffer = byteBuffer.asIntBuffer();
	int read = 0;
	for (;;) {
	 byteBuffer.clear();
	 byteBuffer.limit((int)Math.min(buffer.capacity(), (long)length << IntMappedBigList.LOG2_BYTES));
	 int r = channel.read(byteBuffer);
	 if (r <= 0) return read;
	 r >>>= IntMappedBigList.LOG2_BYTES;
	 read += r;
	 // TODO: use the indexed get() method when switching to Java 13+
	 buffer.clear();
	 buffer.limit(r);
	 buffer.get(array, offset, r);
	 offset += r;
	 length -= r;
	}
}
/** Loads elements from a given readable channel, using the given byte order, storing them in a given array.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	* @param array an array which will be filled with data from {@code channel}.
	* @return the number of elements actually read from {@code channel} (it might be less than the array length if {@code channel} ends).
	*/
public static int loadInts(final ReadableByteChannel channel, final ByteOrder byteOrder, final int[] array) throws IOException {
	return loadInts(channel, byteOrder, array, 0, array.length);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a given array fragment.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadInts(final File file, final ByteOrder byteOrder, final int[] array, final int offset, final int length) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	final FileChannel channel = FileChannel.open(file.toPath());
	final int read = loadInts(channel, byteOrder, array, offset, length);
	channel.close();
	return read;
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a given array fragment.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadInts(final CharSequence filename, final ByteOrder byteOrder, final int[] array, final int offset, final int length) throws IOException {
	return loadInts(new File(filename.toString()), byteOrder, array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a given array.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadInts(final File file, final ByteOrder byteOrder, final int[] array) throws IOException {
	return loadInts(file, byteOrder, array, 0, array.length);
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a given array.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadInts(final CharSequence filename, final ByteOrder byteOrder, final int[] array) throws IOException {
	return loadInts(new File(filename.toString()), byteOrder, array);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @return an array filled with the content of the specified file.
	*/
public static int[] loadInts(final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	final long length = channel.size() / Integer.BYTES;
	if (length > Integer.MAX_VALUE) {
	 channel.close();
	 throw new IllegalArgumentException("File too long: " + channel.size()+ " bytes (" + length + " elements)");
	}
	final int[] array = new int[(int)length];
	if (loadInts(channel, byteOrder, array) < length) throw new EOFException();
	channel.close();
	return array;
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @return an array filled with the content of the specified file.
	*/
public static int[] loadInts(final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	return loadInts(new File(filename.toString()), byteOrder);
}
/** Stores an array fragment to a given writable channel, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code channel}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeInts(final int array[], int offset, int length, final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE).order(byteOrder);
	final IntBuffer buffer = byteBuffer.asIntBuffer();
	while(length != 0) {
	 final int l = Math.min(length, buffer.capacity());
	 buffer.clear();
	 buffer.put(array, offset, l);
	 buffer.flip();
	 byteBuffer.clear();
	 byteBuffer.limit(buffer.limit() << IntMappedBigList.LOG2_BYTES);
	 channel.write(byteBuffer);
	 offset += l;
	 length -= l;
	}
}
/** Stores an array to a given writable channel, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code channel}.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeInts(final int array[], final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	storeInts(array, 0, array.length, channel, byteOrder);
}
/** Stores an array fragment to a file given by a {@link File} object, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeInts(final int array[], final int offset, final int length, final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	storeInts(array, offset, length, channel, byteOrder);
	channel.close();
}
/** Stores an array fragment to a file given by a filename, using the given byte order.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeInts(final int array[], final int offset, final int length, final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeInts(array, offset, length, new File(filename.toString()), byteOrder);
}
/** Stores an array to a file given by a {@link File} object, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeInts(final int array[], final File file, final ByteOrder byteOrder) throws IOException {
	storeInts(array, 0, array.length, file, byteOrder);
}
/** Stores an array to a file given by a filename, using the given byte order.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeInts(final int array[], final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeInts(array, new File(filename.toString()), byteOrder);
}
/** Loads elements from a given readable channel, using the given byte order, storing them in a given big-array fragment.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	* @param array a big array which will be filled with data from {@code channel}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code channel} (it might be less than {@code length} if {@code channel} ends).
	*/
public static long loadInts(final ReadableByteChannel channel, final ByteOrder byteOrder, final int[][] array, final long offset, final long length) throws IOException {
	ensureOffsetLength(array, offset, length);
	long read = 0;
	for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	 final int[] t = array[i];
	 final int s = (int)Math.max(0, offset - start(i));
	 final int e = (int)Math.min(t.length, offset + length - start(i));
	 final int r = loadInts(channel, byteOrder, t, s, e - s);
	 read += r;
	 if (r < e -s) break;
	}
	return read;
}
/** Loads elements from a given readable channel, using the given byte order, storing them in a given big array.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	* @param array a big array which will be filled with data from {@code channel}.
	* @return the number of elements actually read from {@code channel} (it might be less than the array length if {@code channel} ends).
	*/
public static long loadInts(final ReadableByteChannel channel, final ByteOrder byteOrder, final int[][] array) throws IOException {
	return loadInts(channel, byteOrder, array, 0, length(array));
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a given big-array fragment.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @param array a big array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadInts(final File file, final ByteOrder byteOrder, final int[][] array, final long offset, final long length) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	final long read = loadInts(channel, byteOrder, array, offset, length);
	channel.close();
	return read;
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a given big-array fragment.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadInts(final CharSequence filename, final ByteOrder byteOrder, final int[][] array, final long offset, final long length) throws IOException {
	return loadInts(new File(filename.toString()), byteOrder, array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a given big array.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadInts(final File file, final ByteOrder byteOrder, final int[][] array) throws IOException {
	return loadInts(file, byteOrder, array, 0, length(array));
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a given big array.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadInts(final CharSequence filename, final ByteOrder byteOrder, final int[][] array) throws IOException {
	return loadInts(new File(filename.toString()), byteOrder, array);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @return a big array filled with the content of the specified file.
	*/
public static int[][] loadIntsBig(final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	final long length = channel.size() / Integer.BYTES;
	final int[][] array = IntBigArrays.newBigArray(length);
	for(final int[] t: array) loadInts(channel, byteOrder, t);
	channel.close();
	return array;
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @return a big array filled with the content of the specified file.
	*/
public static int[][] loadIntsBig(final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	return loadIntsBig(new File(filename.toString()), byteOrder);
}
/** Stores an array fragment to a given writable channel, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code channel}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeInts(final int array[][], final long offset, final long length, final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	 final int s = (int)Math.max(0, offset - start(i));
	 final int l = (int)Math.min(array[i].length, offset + length - start(i));
	 storeInts(array[i], s, l - s, channel, byteOrder);
	}
}
/** Stores a big array to a given writable channel, using the given byte order.
	*
	* @param array a big array whose elements will be written to {@code channel}.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeInts(final int array[][], final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	for(int[] t: array) storeInts(t, channel, byteOrder);
}
/** Stores a big-array fragment to a file given by a {@link File} object, using the given byte order.
	*
	* @param array a big array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeInts(final int array[][], final long offset, final long length, final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	storeInts(array, offset, length, channel, byteOrder);
	channel.close();
}
/** Stores a big-array fragment to a file given by a filename, using the given byte order.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeInts(final int array[][], final long offset, final long length, final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeInts(array, offset, length, new File(filename.toString()), byteOrder);
}
/** Stores an array to a file given by a {@link File} object, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeInts(final int array[][], final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	storeInts(array, channel, byteOrder);
	channel.close();
}
/** Stores a big array to a file given by a filename, using the given byte order.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeInts(final int array[][], final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeInts(array, new File(filename.toString()), byteOrder);
}
/** Stores the element returned by an iterator to a given writable channel, using the given byte order.
	*
	* @param i an iterator whose output will be written to {@code channel}.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeInts(final IntIterator i, final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE).order(byteOrder);
	final IntBuffer buffer = byteBuffer.asIntBuffer();
	while(i.hasNext()) {
	 if (! buffer.hasRemaining()) {
	  buffer.flip();
	  byteBuffer.clear();
	  byteBuffer.limit(buffer.limit() << IntMappedBigList.LOG2_BYTES);
	  channel.write(byteBuffer);
	  buffer.clear();
	 }
	 buffer.put(i.nextInt());
	}
	buffer.flip();
	byteBuffer.clear();
	byteBuffer.limit(buffer.limit() << IntMappedBigList.LOG2_BYTES);
	channel.write(byteBuffer);
}
/** Stores the element returned by an iterator to a file given by a {@link File} object, using the given byte order.
	*
	* @param i an iterator whose output will be written to {@code file}.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeInts(final IntIterator i, final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	storeInts(i, channel, byteOrder);
	channel.close();
}
/** Stores the element returned by an iterator to a file given by a filename, using the given byte order.
	*
	* @param i an iterator whose output will be written to the file {@code filename}.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeInts(final IntIterator i, final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeInts(i, new File(filename.toString()), byteOrder);
}
/** A wrapper that exhibits the content of a readable channel as a type-specific iterator. */
private static final class IntDataNioInputWrapper implements IntIterator {
	private final ReadableByteChannel channel;
	private final ByteBuffer byteBuffer;
	private final IntBuffer buffer;
	public IntDataNioInputWrapper(final ReadableByteChannel channel, final ByteOrder byteOrder) {
	 this.channel = channel;
	 byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE).order(byteOrder);
	 buffer = byteBuffer.asIntBuffer();
	 buffer.clear().flip();
	}
	@Override
	public boolean hasNext() {
	 if (! buffer.hasRemaining()) {
	  byteBuffer.clear();
	  try {
	   channel.read(byteBuffer);
	  } catch(IOException e) {
	   throw new RuntimeException(e);
	  }
	  byteBuffer.flip();
	  buffer.clear();
	  buffer.limit(byteBuffer.limit() >>> IntMappedBigList.LOG2_BYTES);
	 }
	 return buffer.hasRemaining();
	}
	@Override
	public int nextInt() {
	 if (! hasNext()) throw new NoSuchElementException();
	 return buffer.get();
	}
}
/** Wraps the given readable channel, using the given byte order, into an iterator.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	*/
public static IntIterator asIntIterator(final ReadableByteChannel channel, final ByteOrder byteOrder) {
	return new IntDataNioInputWrapper(channel, byteOrder);
}
/** Wraps a file given by a {@link File} object, using the given byte order, into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	*/
public static IntIterator asIntIterator(final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	return new IntDataNioInputWrapper(channel, byteOrder);
}
/** Wraps a file given by a filename, using the given byte order, into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	*/
public static IntIterator asIntIterator(final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	return asIntIterator(new File(filename.toString()), byteOrder);
}
/** Wraps a file given by a {@link File} object, using the given byte order, into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	*/
public static IntIterable asIntIterable(final File file, final ByteOrder byteOrder) {
	return () -> {
	 try { return asIntIterator(file, byteOrder); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
/** Wraps a file given by a filename, using the given byte order, into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	*/
public static IntIterable asIntIterable(final CharSequence filename, final ByteOrder byteOrder) {
	return () -> {
	 try { return asIntIterator(filename, byteOrder); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
/** Loads elements from a given data input, storing them in a given array fragment.
	*
	* @param dataInput a data input.
	* @param array an array which will be filled with data from {@code dataInput}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code dataInput} (it might be less than {@code length} if {@code dataInput} ends).
	*/
public static int loadInts(final DataInput dataInput, final int[] array, final int offset, final int length) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	int i = 0;
	try {
	 for(i = 0; i < length; i++) array[i + offset] = dataInput.readInt();
	}
	catch(EOFException itsOk) {}
	return i;
}
/** Loads elements from a given data input, storing them in a given array.
	*
	* @param dataInput a data input.
	* @param array an array which will be filled with data from {@code dataInput}.
	* @return the number of elements actually read from {@code dataInput} (it might be less than the array length if {@code dataInput} ends).
	*/
public static int loadInts(final DataInput dataInput, final int[] array) throws IOException {
	int i = 0;
	try {
	 final int length = array.length;
	 for(i = 0; i < length; i++) array[i] = dataInput.readInt();
	}
	catch(EOFException itsOk) {}
	return i;
}
/** Loads elements from a file given by a {@link File} object, storing them in a given array fragment.
	*
	* @param file a file.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadInts(final File file, final int[] array, final int offset, final int length) throws IOException {
	return loadInts(file, ByteOrder.BIG_ENDIAN, array, offset, length);
}
/** Loads elements from a file given by a filename, storing them in a given array fragment.
	*
	* @param filename a filename.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadInts(final CharSequence filename, final int[] array, final int offset, final int length) throws IOException {
	return loadInts(new File(filename.toString()), array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, storing them in a given array.
	*
	* @param file a file.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadInts(final File file, final int[] array) throws IOException {
	return loadInts(file, array, 0, array.length);
}
/** Loads elements from a file given by a filename, storing them in a given array.
	*
	* @param filename a filename.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadInts(final CharSequence filename, final int[] array) throws IOException {
	return loadInts(new File(filename.toString()), array);
}
/** Loads elements from a file given by a {@link File} object, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @return an array filled with the content of the specified file.
	*/
public static int[] loadInts(final File file) throws IOException {
	return loadInts(file, ByteOrder.BIG_ENDIAN);
}
/** Loads elements from a file given by a filename, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @return an array filled with the content of the specified file.
	*/
public static int[] loadInts(final CharSequence filename) throws IOException {
	return loadInts(new File(filename.toString()));
}
/** Stores an array fragment to a given data output.
	*
	* @param array an array whose elements will be written to {@code dataOutput}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param dataOutput a data output.
	*/
public static void storeInts(final int array[], final int offset, final int length, final DataOutput dataOutput) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	for(int i = 0; i < length; i++) dataOutput.writeInt(array[offset + i]);
}
/** Stores an array to a given data output.
	*
	* @param array an array whose elements will be written to {@code dataOutput}.
	* @param dataOutput a data output.
	*/
public static void storeInts(final int array[], final DataOutput dataOutput) throws IOException {
	final int length = array.length;
	for(int i = 0; i < length; i++) dataOutput.writeInt(array[i]);
}
/** Stores an array fragment to a file given by a {@link File} object.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	*/
public static void storeInts(final int array[], final int offset, final int length, final File file) throws IOException {
	storeInts(array, offset, length, file, ByteOrder.BIG_ENDIAN);
}
/** Stores an array fragment to a file given by a filename.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	*/
public static void storeInts(final int array[], final int offset, final int length, final CharSequence filename) throws IOException {
	storeInts(array, offset, length, new File(filename.toString()));
}
/** Stores an array to a file given by a {@link File} object.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	*/
public static void storeInts(final int array[], final File file) throws IOException {
	storeInts(array, 0, array.length, file);
}
/** Stores an array to a file given by a filename.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	*/
public static void storeInts(final int array[], final CharSequence filename) throws IOException {
	storeInts(array, new File(filename.toString()));
}
/** Loads elements from a given data input, storing them in a given big-array fragment.
	*
	* @param dataInput a data input.
	* @param array a big array which will be filled with data from {@code dataInput}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code dataInput} (it might be less than {@code length} if {@code dataInput} ends).
	*/
public static long loadInts(final DataInput dataInput, final int[][] array, final long offset, final long length) throws IOException {
	ensureOffsetLength(array, offset, length);
	long c = 0;
	try {
	 for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	  final int[] t = array[i];
	  final int l = (int)Math.min(t.length, offset + length - start(i));
	  for(int d = (int)Math.max(0, offset - start(i)); d < l; d++) {
	   t[d] = dataInput.readInt();
	   c++;
	  }
	 }
	}
	catch(EOFException itsOk) {}
	return c;
}
/** Loads elements from a given data input, storing them in a given big array.
	*
	* @param dataInput a data input.
	* @param array a big array which will be filled with data from {@code dataInput}.
	* @return the number of elements actually read from {@code dataInput} (it might be less than the array length if {@code dataInput} ends).
	*/
public static long loadInts(final DataInput dataInput, final int[][] array) throws IOException {
	long c = 0;
	try {
	 for(int i = 0; i < array.length; i++) {
	  final int[] t = array[i];
	  final int l = t.length;
	  for(int d = 0; d < l; d++) {
	   t[d] = dataInput.readInt();
	   c++;
	  }
	 }
	}
	catch(EOFException itsOk) {}
	return c;
}
/** Loads elements from a file given by a {@link File} object, storing them in a given big-array fragment.
	*
	* @param file a file.
	* @param array a big array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadInts(final File file, final int[][] array, final long offset, final long length) throws IOException {
	return loadInts(file, ByteOrder.BIG_ENDIAN, array, offset, length);
}
/** Loads elements from a file given by a filename, storing them in a given big-array fragment.
	*
	* @param filename a filename.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadInts(final CharSequence filename, final int[][] array, final long offset, final long length) throws IOException {
	return loadInts(new File(filename.toString()), array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, storing them in a given big array.
	*
	* @param file a file.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadInts(final File file, final int[][] array) throws IOException {
	return loadInts(file, ByteOrder.BIG_ENDIAN, array);
}
/** Loads elements from a file given by a filename, storing them in a given big array.
	*
	* @param filename a filename.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadInts(final CharSequence filename, final int[][] array) throws IOException {
	return loadInts(new File(filename.toString()), array);
}
/** Loads elements from a file given by a {@link File} object, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @return a big array filled with the content of the specified file.
	*/
public static int[][] loadIntsBig(final File file) throws IOException {
	return loadIntsBig(file, ByteOrder.BIG_ENDIAN);
}
/** Loads elements from a file given by a filename, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @return a big array filled with the content of the specified file.
	*/
public static int[][] loadIntsBig(final CharSequence filename) throws IOException {
	return loadIntsBig(new File(filename.toString()));
}
/** Stores an array fragment to a given data output.
	*
	* @param array an array whose elements will be written to {@code dataOutput}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param dataOutput a data output.
	*/
public static void storeInts(final int array[][], final long offset, final long length, final DataOutput dataOutput) throws IOException {
	ensureOffsetLength(array, offset, length);
	for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	 final int[] t = array[i];
	 final int l = (int)Math.min(t.length, offset + length - start(i));
	 for(int d = (int)Math.max(0, offset - start(i)); d < l; d++) dataOutput.writeInt(t[d]);
	}
}
/** Stores a big array to a given data output.
	*
	* @param array a big array whose elements will be written to {@code dataOutput}.
	* @param dataOutput a data output.
	*/
public static void storeInts(final int array[][], final DataOutput dataOutput) throws IOException {
	for(int i = 0; i < array.length; i++) {
	 final int[] t = array[i];
	 final int l = t.length;
	 for(int d = 0; d < l; d++) dataOutput.writeInt(t[d]);
	}
}
/** Stores a big-array fragment to a file given by a {@link File} object.
	*
	* @param array a big array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	*/
public static void storeInts(final int array[][], final long offset, final long length, final File file) throws IOException {
	storeInts(array, offset, length, file, ByteOrder.BIG_ENDIAN);
}
/** Stores a big-array fragment to a file given by a filename.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	*/
public static void storeInts(final int array[][], final long offset, final long length, final CharSequence filename) throws IOException {
	storeInts(array, offset, length, new File(filename.toString()));
}
/** Stores an array to a file given by a {@link File} object.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	*/
public static void storeInts(final int array[][], final File file) throws IOException {
	storeInts(array, file, ByteOrder.BIG_ENDIAN);
}
/** Stores a big array to a file given by a filename.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	*/
public static void storeInts(final int array[][], final CharSequence filename) throws IOException {
	storeInts(array, new File(filename.toString()));
}
/** Stores the element returned by an iterator to a given data output.
	*
	* @param i an iterator whose output will be written to {@code dataOutput}.
	* @param dataOutput a filename.
	*/
public static void storeInts(final IntIterator i, final DataOutput dataOutput) throws IOException {
	while(i.hasNext()) dataOutput.writeInt(i.nextInt());
}
/** Stores the element returned by an iterator to a file given by a {@link File} object.
	*
	* @param i an iterator whose output will be written to {@code file}.
	* @param file a file.
	*/
public static void storeInts(final IntIterator i, final File file) throws IOException {
	storeInts(i, file, ByteOrder.BIG_ENDIAN);
}
/** Stores the element returned by an iterator to a file given by a filename.
	*
	* @param i an iterator whose output will be written to the file {@code filename}.
	* @param filename a filename.
	*/
public static void storeInts(final IntIterator i, final CharSequence filename) throws IOException {
	storeInts(i, new File(filename.toString()));
}
/** A wrapper that exhibits the content of a data input stream as a type-specific iterator. */
private static final class IntDataInputWrapper implements IntIterator {
	private final DataInput dataInput;
	private boolean toAdvance = true;
	private boolean endOfProcess = false;
	private int next;
	public IntDataInputWrapper(final DataInput dataInput) {
	 this.dataInput = dataInput;
	}
	@Override
	public boolean hasNext() {
	 if (! toAdvance) return ! endOfProcess;
	 toAdvance = false;
	 try { next = dataInput.readInt(); }
	 catch(EOFException eof) { endOfProcess = true; }
	 catch(IOException rethrow) { throw new RuntimeException(rethrow); }
	 return ! endOfProcess;
	}
	@Override
	public int nextInt() {
	 if (! hasNext()) throw new NoSuchElementException();
	 toAdvance = true;
	 return next;
	}
}
/** Wraps the given data input stream into an iterator.
	*
	* @param dataInput a data input.
	*/
public static IntIterator asIntIterator(final DataInput dataInput) {
	return new IntDataInputWrapper(dataInput);
}
/** Wraps a file given by a {@link File} object into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param file a file.
	*/
public static IntIterator asIntIterator(final File file) throws IOException {
	return asIntIterator(file, ByteOrder.BIG_ENDIAN);
}
/** Wraps a file given by a filename into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param filename a filename.
	*/
public static IntIterator asIntIterator(final CharSequence filename) throws IOException {
	return asIntIterator(new File(filename.toString()));
}
/** Wraps a file given by a {@link File} object into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param file a file.
	*/
public static IntIterable asIntIterable(final File file) {
	return () -> {
	 try { return asIntIterator(file); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
/** Wraps a file given by a filename into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param filename a filename.
	*/
public static IntIterable asIntIterable(final CharSequence filename) {
	return () -> {
	 try { return asIntIterator(filename); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
/* Generic definitions */
/* Assertions (useful to generate conditional code) */
/* Narrowing and widening */
/* Current type and class (and size, if applicable) */
/* Value methods */
/* Interfaces (keys) */
/* Interfaces (values) */
/* Types and methods related to primitive-type support in the JDK */
/* Abstract implementations (keys) */
/* Abstract implementations (values) */
/* Static containers (keys) */
/* Static containers (values) */
/* Implementations */
/* Synchronized wrappers */
/* Unmodifiable wrappers */
/* Other wrappers */
/* Methods (keys) */
/* Methods (values) */
/* Methods (keys/values) */
/* Methods that have special names depending on keys (but the special names depend on values) */
/* Equality */
/* Object/Reference-only definitions (keys) */
/* Primitive-type-only definitions (keys) */
/* Object/Reference-only definitions (values) */
/* START_OF_JAVA_SOURCE */
/*
	* Copyright (C) 2004-2024 Sebastiano Vigna
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
/** Loads elements from a given readable channel, using the given byte order, storing them in a given array fragment.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	* @param array an array which will be filled with data from {@code channel}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code channel} (it might be less than {@code length} if {@code channel} ends).
	*/
public static int loadFloats(final ReadableByteChannel channel, final ByteOrder byteOrder, final float[] array, int offset, int length) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE).order(byteOrder);
	final FloatBuffer buffer = byteBuffer.asFloatBuffer();
	int read = 0;
	for (;;) {
	 byteBuffer.clear();
	 byteBuffer.limit((int)Math.min(buffer.capacity(), (long)length << FloatMappedBigList.LOG2_BYTES));
	 int r = channel.read(byteBuffer);
	 if (r <= 0) return read;
	 r >>>= FloatMappedBigList.LOG2_BYTES;
	 read += r;
	 // TODO: use the indexed get() method when switching to Java 13+
	 buffer.clear();
	 buffer.limit(r);
	 buffer.get(array, offset, r);
	 offset += r;
	 length -= r;
	}
}
/** Loads elements from a given readable channel, using the given byte order, storing them in a given array.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	* @param array an array which will be filled with data from {@code channel}.
	* @return the number of elements actually read from {@code channel} (it might be less than the array length if {@code channel} ends).
	*/
public static int loadFloats(final ReadableByteChannel channel, final ByteOrder byteOrder, final float[] array) throws IOException {
	return loadFloats(channel, byteOrder, array, 0, array.length);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a given array fragment.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadFloats(final File file, final ByteOrder byteOrder, final float[] array, final int offset, final int length) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	final FileChannel channel = FileChannel.open(file.toPath());
	final int read = loadFloats(channel, byteOrder, array, offset, length);
	channel.close();
	return read;
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a given array fragment.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadFloats(final CharSequence filename, final ByteOrder byteOrder, final float[] array, final int offset, final int length) throws IOException {
	return loadFloats(new File(filename.toString()), byteOrder, array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a given array.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadFloats(final File file, final ByteOrder byteOrder, final float[] array) throws IOException {
	return loadFloats(file, byteOrder, array, 0, array.length);
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a given array.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadFloats(final CharSequence filename, final ByteOrder byteOrder, final float[] array) throws IOException {
	return loadFloats(new File(filename.toString()), byteOrder, array);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @return an array filled with the content of the specified file.
	*/
public static float[] loadFloats(final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	final long length = channel.size() / Float.BYTES;
	if (length > Integer.MAX_VALUE) {
	 channel.close();
	 throw new IllegalArgumentException("File too long: " + channel.size()+ " bytes (" + length + " elements)");
	}
	final float[] array = new float[(int)length];
	if (loadFloats(channel, byteOrder, array) < length) throw new EOFException();
	channel.close();
	return array;
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @return an array filled with the content of the specified file.
	*/
public static float[] loadFloats(final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	return loadFloats(new File(filename.toString()), byteOrder);
}
/** Stores an array fragment to a given writable channel, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code channel}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeFloats(final float array[], int offset, int length, final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE).order(byteOrder);
	final FloatBuffer buffer = byteBuffer.asFloatBuffer();
	while(length != 0) {
	 final int l = Math.min(length, buffer.capacity());
	 buffer.clear();
	 buffer.put(array, offset, l);
	 buffer.flip();
	 byteBuffer.clear();
	 byteBuffer.limit(buffer.limit() << FloatMappedBigList.LOG2_BYTES);
	 channel.write(byteBuffer);
	 offset += l;
	 length -= l;
	}
}
/** Stores an array to a given writable channel, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code channel}.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeFloats(final float array[], final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	storeFloats(array, 0, array.length, channel, byteOrder);
}
/** Stores an array fragment to a file given by a {@link File} object, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeFloats(final float array[], final int offset, final int length, final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	storeFloats(array, offset, length, channel, byteOrder);
	channel.close();
}
/** Stores an array fragment to a file given by a filename, using the given byte order.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeFloats(final float array[], final int offset, final int length, final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeFloats(array, offset, length, new File(filename.toString()), byteOrder);
}
/** Stores an array to a file given by a {@link File} object, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeFloats(final float array[], final File file, final ByteOrder byteOrder) throws IOException {
	storeFloats(array, 0, array.length, file, byteOrder);
}
/** Stores an array to a file given by a filename, using the given byte order.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeFloats(final float array[], final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeFloats(array, new File(filename.toString()), byteOrder);
}
/** Loads elements from a given readable channel, using the given byte order, storing them in a given big-array fragment.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	* @param array a big array which will be filled with data from {@code channel}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code channel} (it might be less than {@code length} if {@code channel} ends).
	*/
public static long loadFloats(final ReadableByteChannel channel, final ByteOrder byteOrder, final float[][] array, final long offset, final long length) throws IOException {
	ensureOffsetLength(array, offset, length);
	long read = 0;
	for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	 final float[] t = array[i];
	 final int s = (int)Math.max(0, offset - start(i));
	 final int e = (int)Math.min(t.length, offset + length - start(i));
	 final int r = loadFloats(channel, byteOrder, t, s, e - s);
	 read += r;
	 if (r < e -s) break;
	}
	return read;
}
/** Loads elements from a given readable channel, using the given byte order, storing them in a given big array.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	* @param array a big array which will be filled with data from {@code channel}.
	* @return the number of elements actually read from {@code channel} (it might be less than the array length if {@code channel} ends).
	*/
public static long loadFloats(final ReadableByteChannel channel, final ByteOrder byteOrder, final float[][] array) throws IOException {
	return loadFloats(channel, byteOrder, array, 0, length(array));
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a given big-array fragment.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @param array a big array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadFloats(final File file, final ByteOrder byteOrder, final float[][] array, final long offset, final long length) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	final long read = loadFloats(channel, byteOrder, array, offset, length);
	channel.close();
	return read;
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a given big-array fragment.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadFloats(final CharSequence filename, final ByteOrder byteOrder, final float[][] array, final long offset, final long length) throws IOException {
	return loadFloats(new File(filename.toString()), byteOrder, array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a given big array.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadFloats(final File file, final ByteOrder byteOrder, final float[][] array) throws IOException {
	return loadFloats(file, byteOrder, array, 0, length(array));
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a given big array.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadFloats(final CharSequence filename, final ByteOrder byteOrder, final float[][] array) throws IOException {
	return loadFloats(new File(filename.toString()), byteOrder, array);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @return a big array filled with the content of the specified file.
	*/
public static float[][] loadFloatsBig(final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	final long length = channel.size() / Float.BYTES;
	final float[][] array = FloatBigArrays.newBigArray(length);
	for(final float[] t: array) loadFloats(channel, byteOrder, t);
	channel.close();
	return array;
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @return a big array filled with the content of the specified file.
	*/
public static float[][] loadFloatsBig(final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	return loadFloatsBig(new File(filename.toString()), byteOrder);
}
/** Stores an array fragment to a given writable channel, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code channel}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeFloats(final float array[][], final long offset, final long length, final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	 final int s = (int)Math.max(0, offset - start(i));
	 final int l = (int)Math.min(array[i].length, offset + length - start(i));
	 storeFloats(array[i], s, l - s, channel, byteOrder);
	}
}
/** Stores a big array to a given writable channel, using the given byte order.
	*
	* @param array a big array whose elements will be written to {@code channel}.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeFloats(final float array[][], final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	for(float[] t: array) storeFloats(t, channel, byteOrder);
}
/** Stores a big-array fragment to a file given by a {@link File} object, using the given byte order.
	*
	* @param array a big array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeFloats(final float array[][], final long offset, final long length, final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	storeFloats(array, offset, length, channel, byteOrder);
	channel.close();
}
/** Stores a big-array fragment to a file given by a filename, using the given byte order.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeFloats(final float array[][], final long offset, final long length, final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeFloats(array, offset, length, new File(filename.toString()), byteOrder);
}
/** Stores an array to a file given by a {@link File} object, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeFloats(final float array[][], final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	storeFloats(array, channel, byteOrder);
	channel.close();
}
/** Stores a big array to a file given by a filename, using the given byte order.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeFloats(final float array[][], final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeFloats(array, new File(filename.toString()), byteOrder);
}
/** Stores the element returned by an iterator to a given writable channel, using the given byte order.
	*
	* @param i an iterator whose output will be written to {@code channel}.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeFloats(final FloatIterator i, final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE).order(byteOrder);
	final FloatBuffer buffer = byteBuffer.asFloatBuffer();
	while(i.hasNext()) {
	 if (! buffer.hasRemaining()) {
	  buffer.flip();
	  byteBuffer.clear();
	  byteBuffer.limit(buffer.limit() << FloatMappedBigList.LOG2_BYTES);
	  channel.write(byteBuffer);
	  buffer.clear();
	 }
	 buffer.put(i.nextFloat());
	}
	buffer.flip();
	byteBuffer.clear();
	byteBuffer.limit(buffer.limit() << FloatMappedBigList.LOG2_BYTES);
	channel.write(byteBuffer);
}
/** Stores the element returned by an iterator to a file given by a {@link File} object, using the given byte order.
	*
	* @param i an iterator whose output will be written to {@code file}.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeFloats(final FloatIterator i, final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	storeFloats(i, channel, byteOrder);
	channel.close();
}
/** Stores the element returned by an iterator to a file given by a filename, using the given byte order.
	*
	* @param i an iterator whose output will be written to the file {@code filename}.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeFloats(final FloatIterator i, final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeFloats(i, new File(filename.toString()), byteOrder);
}
/** A wrapper that exhibits the content of a readable channel as a type-specific iterator. */
private static final class FloatDataNioInputWrapper implements FloatIterator {
	private final ReadableByteChannel channel;
	private final ByteBuffer byteBuffer;
	private final FloatBuffer buffer;
	public FloatDataNioInputWrapper(final ReadableByteChannel channel, final ByteOrder byteOrder) {
	 this.channel = channel;
	 byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE).order(byteOrder);
	 buffer = byteBuffer.asFloatBuffer();
	 buffer.clear().flip();
	}
	@Override
	public boolean hasNext() {
	 if (! buffer.hasRemaining()) {
	  byteBuffer.clear();
	  try {
	   channel.read(byteBuffer);
	  } catch(IOException e) {
	   throw new RuntimeException(e);
	  }
	  byteBuffer.flip();
	  buffer.clear();
	  buffer.limit(byteBuffer.limit() >>> FloatMappedBigList.LOG2_BYTES);
	 }
	 return buffer.hasRemaining();
	}
	@Override
	public float nextFloat() {
	 if (! hasNext()) throw new NoSuchElementException();
	 return buffer.get();
	}
}
/** Wraps the given readable channel, using the given byte order, into an iterator.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	*/
public static FloatIterator asFloatIterator(final ReadableByteChannel channel, final ByteOrder byteOrder) {
	return new FloatDataNioInputWrapper(channel, byteOrder);
}
/** Wraps a file given by a {@link File} object, using the given byte order, into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	*/
public static FloatIterator asFloatIterator(final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	return new FloatDataNioInputWrapper(channel, byteOrder);
}
/** Wraps a file given by a filename, using the given byte order, into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	*/
public static FloatIterator asFloatIterator(final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	return asFloatIterator(new File(filename.toString()), byteOrder);
}
/** Wraps a file given by a {@link File} object, using the given byte order, into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	*/
public static FloatIterable asFloatIterable(final File file, final ByteOrder byteOrder) {
	return () -> {
	 try { return asFloatIterator(file, byteOrder); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
/** Wraps a file given by a filename, using the given byte order, into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	*/
public static FloatIterable asFloatIterable(final CharSequence filename, final ByteOrder byteOrder) {
	return () -> {
	 try { return asFloatIterator(filename, byteOrder); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
/** Loads elements from a given data input, storing them in a given array fragment.
	*
	* @param dataInput a data input.
	* @param array an array which will be filled with data from {@code dataInput}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code dataInput} (it might be less than {@code length} if {@code dataInput} ends).
	*/
public static int loadFloats(final DataInput dataInput, final float[] array, final int offset, final int length) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	int i = 0;
	try {
	 for(i = 0; i < length; i++) array[i + offset] = dataInput.readFloat();
	}
	catch(EOFException itsOk) {}
	return i;
}
/** Loads elements from a given data input, storing them in a given array.
	*
	* @param dataInput a data input.
	* @param array an array which will be filled with data from {@code dataInput}.
	* @return the number of elements actually read from {@code dataInput} (it might be less than the array length if {@code dataInput} ends).
	*/
public static int loadFloats(final DataInput dataInput, final float[] array) throws IOException {
	int i = 0;
	try {
	 final int length = array.length;
	 for(i = 0; i < length; i++) array[i] = dataInput.readFloat();
	}
	catch(EOFException itsOk) {}
	return i;
}
/** Loads elements from a file given by a {@link File} object, storing them in a given array fragment.
	*
	* @param file a file.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadFloats(final File file, final float[] array, final int offset, final int length) throws IOException {
	return loadFloats(file, ByteOrder.BIG_ENDIAN, array, offset, length);
}
/** Loads elements from a file given by a filename, storing them in a given array fragment.
	*
	* @param filename a filename.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadFloats(final CharSequence filename, final float[] array, final int offset, final int length) throws IOException {
	return loadFloats(new File(filename.toString()), array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, storing them in a given array.
	*
	* @param file a file.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadFloats(final File file, final float[] array) throws IOException {
	return loadFloats(file, array, 0, array.length);
}
/** Loads elements from a file given by a filename, storing them in a given array.
	*
	* @param filename a filename.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadFloats(final CharSequence filename, final float[] array) throws IOException {
	return loadFloats(new File(filename.toString()), array);
}
/** Loads elements from a file given by a {@link File} object, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @return an array filled with the content of the specified file.
	*/
public static float[] loadFloats(final File file) throws IOException {
	return loadFloats(file, ByteOrder.BIG_ENDIAN);
}
/** Loads elements from a file given by a filename, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @return an array filled with the content of the specified file.
	*/
public static float[] loadFloats(final CharSequence filename) throws IOException {
	return loadFloats(new File(filename.toString()));
}
/** Stores an array fragment to a given data output.
	*
	* @param array an array whose elements will be written to {@code dataOutput}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param dataOutput a data output.
	*/
public static void storeFloats(final float array[], final int offset, final int length, final DataOutput dataOutput) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	for(int i = 0; i < length; i++) dataOutput.writeFloat(array[offset + i]);
}
/** Stores an array to a given data output.
	*
	* @param array an array whose elements will be written to {@code dataOutput}.
	* @param dataOutput a data output.
	*/
public static void storeFloats(final float array[], final DataOutput dataOutput) throws IOException {
	final int length = array.length;
	for(int i = 0; i < length; i++) dataOutput.writeFloat(array[i]);
}
/** Stores an array fragment to a file given by a {@link File} object.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	*/
public static void storeFloats(final float array[], final int offset, final int length, final File file) throws IOException {
	storeFloats(array, offset, length, file, ByteOrder.BIG_ENDIAN);
}
/** Stores an array fragment to a file given by a filename.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	*/
public static void storeFloats(final float array[], final int offset, final int length, final CharSequence filename) throws IOException {
	storeFloats(array, offset, length, new File(filename.toString()));
}
/** Stores an array to a file given by a {@link File} object.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	*/
public static void storeFloats(final float array[], final File file) throws IOException {
	storeFloats(array, 0, array.length, file);
}
/** Stores an array to a file given by a filename.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	*/
public static void storeFloats(final float array[], final CharSequence filename) throws IOException {
	storeFloats(array, new File(filename.toString()));
}
/** Loads elements from a given data input, storing them in a given big-array fragment.
	*
	* @param dataInput a data input.
	* @param array a big array which will be filled with data from {@code dataInput}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code dataInput} (it might be less than {@code length} if {@code dataInput} ends).
	*/
public static long loadFloats(final DataInput dataInput, final float[][] array, final long offset, final long length) throws IOException {
	ensureOffsetLength(array, offset, length);
	long c = 0;
	try {
	 for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	  final float[] t = array[i];
	  final int l = (int)Math.min(t.length, offset + length - start(i));
	  for(int d = (int)Math.max(0, offset - start(i)); d < l; d++) {
	   t[d] = dataInput.readFloat();
	   c++;
	  }
	 }
	}
	catch(EOFException itsOk) {}
	return c;
}
/** Loads elements from a given data input, storing them in a given big array.
	*
	* @param dataInput a data input.
	* @param array a big array which will be filled with data from {@code dataInput}.
	* @return the number of elements actually read from {@code dataInput} (it might be less than the array length if {@code dataInput} ends).
	*/
public static long loadFloats(final DataInput dataInput, final float[][] array) throws IOException {
	long c = 0;
	try {
	 for(int i = 0; i < array.length; i++) {
	  final float[] t = array[i];
	  final int l = t.length;
	  for(int d = 0; d < l; d++) {
	   t[d] = dataInput.readFloat();
	   c++;
	  }
	 }
	}
	catch(EOFException itsOk) {}
	return c;
}
/** Loads elements from a file given by a {@link File} object, storing them in a given big-array fragment.
	*
	* @param file a file.
	* @param array a big array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadFloats(final File file, final float[][] array, final long offset, final long length) throws IOException {
	return loadFloats(file, ByteOrder.BIG_ENDIAN, array, offset, length);
}
/** Loads elements from a file given by a filename, storing them in a given big-array fragment.
	*
	* @param filename a filename.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadFloats(final CharSequence filename, final float[][] array, final long offset, final long length) throws IOException {
	return loadFloats(new File(filename.toString()), array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, storing them in a given big array.
	*
	* @param file a file.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadFloats(final File file, final float[][] array) throws IOException {
	return loadFloats(file, ByteOrder.BIG_ENDIAN, array);
}
/** Loads elements from a file given by a filename, storing them in a given big array.
	*
	* @param filename a filename.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadFloats(final CharSequence filename, final float[][] array) throws IOException {
	return loadFloats(new File(filename.toString()), array);
}
/** Loads elements from a file given by a {@link File} object, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @return a big array filled with the content of the specified file.
	*/
public static float[][] loadFloatsBig(final File file) throws IOException {
	return loadFloatsBig(file, ByteOrder.BIG_ENDIAN);
}
/** Loads elements from a file given by a filename, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @return a big array filled with the content of the specified file.
	*/
public static float[][] loadFloatsBig(final CharSequence filename) throws IOException {
	return loadFloatsBig(new File(filename.toString()));
}
/** Stores an array fragment to a given data output.
	*
	* @param array an array whose elements will be written to {@code dataOutput}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param dataOutput a data output.
	*/
public static void storeFloats(final float array[][], final long offset, final long length, final DataOutput dataOutput) throws IOException {
	ensureOffsetLength(array, offset, length);
	for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	 final float[] t = array[i];
	 final int l = (int)Math.min(t.length, offset + length - start(i));
	 for(int d = (int)Math.max(0, offset - start(i)); d < l; d++) dataOutput.writeFloat(t[d]);
	}
}
/** Stores a big array to a given data output.
	*
	* @param array a big array whose elements will be written to {@code dataOutput}.
	* @param dataOutput a data output.
	*/
public static void storeFloats(final float array[][], final DataOutput dataOutput) throws IOException {
	for(int i = 0; i < array.length; i++) {
	 final float[] t = array[i];
	 final int l = t.length;
	 for(int d = 0; d < l; d++) dataOutput.writeFloat(t[d]);
	}
}
/** Stores a big-array fragment to a file given by a {@link File} object.
	*
	* @param array a big array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	*/
public static void storeFloats(final float array[][], final long offset, final long length, final File file) throws IOException {
	storeFloats(array, offset, length, file, ByteOrder.BIG_ENDIAN);
}
/** Stores a big-array fragment to a file given by a filename.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	*/
public static void storeFloats(final float array[][], final long offset, final long length, final CharSequence filename) throws IOException {
	storeFloats(array, offset, length, new File(filename.toString()));
}
/** Stores an array to a file given by a {@link File} object.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	*/
public static void storeFloats(final float array[][], final File file) throws IOException {
	storeFloats(array, file, ByteOrder.BIG_ENDIAN);
}
/** Stores a big array to a file given by a filename.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	*/
public static void storeFloats(final float array[][], final CharSequence filename) throws IOException {
	storeFloats(array, new File(filename.toString()));
}
/** Stores the element returned by an iterator to a given data output.
	*
	* @param i an iterator whose output will be written to {@code dataOutput}.
	* @param dataOutput a filename.
	*/
public static void storeFloats(final FloatIterator i, final DataOutput dataOutput) throws IOException {
	while(i.hasNext()) dataOutput.writeFloat(i.nextFloat());
}
/** Stores the element returned by an iterator to a file given by a {@link File} object.
	*
	* @param i an iterator whose output will be written to {@code file}.
	* @param file a file.
	*/
public static void storeFloats(final FloatIterator i, final File file) throws IOException {
	storeFloats(i, file, ByteOrder.BIG_ENDIAN);
}
/** Stores the element returned by an iterator to a file given by a filename.
	*
	* @param i an iterator whose output will be written to the file {@code filename}.
	* @param filename a filename.
	*/
public static void storeFloats(final FloatIterator i, final CharSequence filename) throws IOException {
	storeFloats(i, new File(filename.toString()));
}
/** A wrapper that exhibits the content of a data input stream as a type-specific iterator. */
private static final class FloatDataInputWrapper implements FloatIterator {
	private final DataInput dataInput;
	private boolean toAdvance = true;
	private boolean endOfProcess = false;
	private float next;
	public FloatDataInputWrapper(final DataInput dataInput) {
	 this.dataInput = dataInput;
	}
	@Override
	public boolean hasNext() {
	 if (! toAdvance) return ! endOfProcess;
	 toAdvance = false;
	 try { next = dataInput.readFloat(); }
	 catch(EOFException eof) { endOfProcess = true; }
	 catch(IOException rethrow) { throw new RuntimeException(rethrow); }
	 return ! endOfProcess;
	}
	@Override
	public float nextFloat() {
	 if (! hasNext()) throw new NoSuchElementException();
	 toAdvance = true;
	 return next;
	}
}
/** Wraps the given data input stream into an iterator.
	*
	* @param dataInput a data input.
	*/
public static FloatIterator asFloatIterator(final DataInput dataInput) {
	return new FloatDataInputWrapper(dataInput);
}
/** Wraps a file given by a {@link File} object into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param file a file.
	*/
public static FloatIterator asFloatIterator(final File file) throws IOException {
	return asFloatIterator(file, ByteOrder.BIG_ENDIAN);
}
/** Wraps a file given by a filename into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param filename a filename.
	*/
public static FloatIterator asFloatIterator(final CharSequence filename) throws IOException {
	return asFloatIterator(new File(filename.toString()));
}
/** Wraps a file given by a {@link File} object into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param file a file.
	*/
public static FloatIterable asFloatIterable(final File file) {
	return () -> {
	 try { return asFloatIterator(file); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
/** Wraps a file given by a filename into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param filename a filename.
	*/
public static FloatIterable asFloatIterable(final CharSequence filename) {
	return () -> {
	 try { return asFloatIterator(filename); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
/* Generic definitions */
/* Assertions (useful to generate conditional code) */
/* Narrowing and widening */
/* Current type and class (and size, if applicable) */
/* Value methods */
/* Interfaces (keys) */
/* Interfaces (values) */
/* Types and methods related to primitive-type support in the JDK */
/* Abstract implementations (keys) */
/* Abstract implementations (values) */
/* Static containers (keys) */
/* Static containers (values) */
/* Implementations */
/* Synchronized wrappers */
/* Unmodifiable wrappers */
/* Other wrappers */
/* Methods (keys) */
/* Methods (values) */
/* Methods (keys/values) */
/* Methods that have special names depending on keys (but the special names depend on values) */
/* Equality */
/* Object/Reference-only definitions (keys) */
/* Primitive-type-only definitions (keys) */
/* Object/Reference-only definitions (values) */
/* START_OF_JAVA_SOURCE */
/*
	* Copyright (C) 2004-2024 Sebastiano Vigna
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
/** Loads elements from a given readable channel, using the given byte order, storing them in a given array fragment.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	* @param array an array which will be filled with data from {@code channel}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code channel} (it might be less than {@code length} if {@code channel} ends).
	*/
public static int loadLongs(final ReadableByteChannel channel, final ByteOrder byteOrder, final long[] array, int offset, int length) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE).order(byteOrder);
	final LongBuffer buffer = byteBuffer.asLongBuffer();
	int read = 0;
	for (;;) {
	 byteBuffer.clear();
	 byteBuffer.limit((int)Math.min(buffer.capacity(), (long)length << LongMappedBigList.LOG2_BYTES));
	 int r = channel.read(byteBuffer);
	 if (r <= 0) return read;
	 r >>>= LongMappedBigList.LOG2_BYTES;
	 read += r;
	 // TODO: use the indexed get() method when switching to Java 13+
	 buffer.clear();
	 buffer.limit(r);
	 buffer.get(array, offset, r);
	 offset += r;
	 length -= r;
	}
}
/** Loads elements from a given readable channel, using the given byte order, storing them in a given array.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	* @param array an array which will be filled with data from {@code channel}.
	* @return the number of elements actually read from {@code channel} (it might be less than the array length if {@code channel} ends).
	*/
public static int loadLongs(final ReadableByteChannel channel, final ByteOrder byteOrder, final long[] array) throws IOException {
	return loadLongs(channel, byteOrder, array, 0, array.length);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a given array fragment.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadLongs(final File file, final ByteOrder byteOrder, final long[] array, final int offset, final int length) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	final FileChannel channel = FileChannel.open(file.toPath());
	final int read = loadLongs(channel, byteOrder, array, offset, length);
	channel.close();
	return read;
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a given array fragment.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadLongs(final CharSequence filename, final ByteOrder byteOrder, final long[] array, final int offset, final int length) throws IOException {
	return loadLongs(new File(filename.toString()), byteOrder, array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a given array.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadLongs(final File file, final ByteOrder byteOrder, final long[] array) throws IOException {
	return loadLongs(file, byteOrder, array, 0, array.length);
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a given array.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadLongs(final CharSequence filename, final ByteOrder byteOrder, final long[] array) throws IOException {
	return loadLongs(new File(filename.toString()), byteOrder, array);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @return an array filled with the content of the specified file.
	*/
public static long[] loadLongs(final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	final long length = channel.size() / Long.BYTES;
	if (length > Integer.MAX_VALUE) {
	 channel.close();
	 throw new IllegalArgumentException("File too long: " + channel.size()+ " bytes (" + length + " elements)");
	}
	final long[] array = new long[(int)length];
	if (loadLongs(channel, byteOrder, array) < length) throw new EOFException();
	channel.close();
	return array;
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @return an array filled with the content of the specified file.
	*/
public static long[] loadLongs(final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	return loadLongs(new File(filename.toString()), byteOrder);
}
/** Stores an array fragment to a given writable channel, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code channel}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeLongs(final long array[], int offset, int length, final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE).order(byteOrder);
	final LongBuffer buffer = byteBuffer.asLongBuffer();
	while(length != 0) {
	 final int l = Math.min(length, buffer.capacity());
	 buffer.clear();
	 buffer.put(array, offset, l);
	 buffer.flip();
	 byteBuffer.clear();
	 byteBuffer.limit(buffer.limit() << LongMappedBigList.LOG2_BYTES);
	 channel.write(byteBuffer);
	 offset += l;
	 length -= l;
	}
}
/** Stores an array to a given writable channel, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code channel}.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeLongs(final long array[], final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	storeLongs(array, 0, array.length, channel, byteOrder);
}
/** Stores an array fragment to a file given by a {@link File} object, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeLongs(final long array[], final int offset, final int length, final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	storeLongs(array, offset, length, channel, byteOrder);
	channel.close();
}
/** Stores an array fragment to a file given by a filename, using the given byte order.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeLongs(final long array[], final int offset, final int length, final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeLongs(array, offset, length, new File(filename.toString()), byteOrder);
}
/** Stores an array to a file given by a {@link File} object, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeLongs(final long array[], final File file, final ByteOrder byteOrder) throws IOException {
	storeLongs(array, 0, array.length, file, byteOrder);
}
/** Stores an array to a file given by a filename, using the given byte order.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeLongs(final long array[], final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeLongs(array, new File(filename.toString()), byteOrder);
}
/** Loads elements from a given readable channel, using the given byte order, storing them in a given big-array fragment.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	* @param array a big array which will be filled with data from {@code channel}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code channel} (it might be less than {@code length} if {@code channel} ends).
	*/
public static long loadLongs(final ReadableByteChannel channel, final ByteOrder byteOrder, final long[][] array, final long offset, final long length) throws IOException {
	ensureOffsetLength(array, offset, length);
	long read = 0;
	for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	 final long[] t = array[i];
	 final int s = (int)Math.max(0, offset - start(i));
	 final int e = (int)Math.min(t.length, offset + length - start(i));
	 final int r = loadLongs(channel, byteOrder, t, s, e - s);
	 read += r;
	 if (r < e -s) break;
	}
	return read;
}
/** Loads elements from a given readable channel, using the given byte order, storing them in a given big array.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	* @param array a big array which will be filled with data from {@code channel}.
	* @return the number of elements actually read from {@code channel} (it might be less than the array length if {@code channel} ends).
	*/
public static long loadLongs(final ReadableByteChannel channel, final ByteOrder byteOrder, final long[][] array) throws IOException {
	return loadLongs(channel, byteOrder, array, 0, length(array));
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a given big-array fragment.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @param array a big array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadLongs(final File file, final ByteOrder byteOrder, final long[][] array, final long offset, final long length) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	final long read = loadLongs(channel, byteOrder, array, offset, length);
	channel.close();
	return read;
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a given big-array fragment.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadLongs(final CharSequence filename, final ByteOrder byteOrder, final long[][] array, final long offset, final long length) throws IOException {
	return loadLongs(new File(filename.toString()), byteOrder, array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a given big array.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadLongs(final File file, final ByteOrder byteOrder, final long[][] array) throws IOException {
	return loadLongs(file, byteOrder, array, 0, length(array));
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a given big array.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadLongs(final CharSequence filename, final ByteOrder byteOrder, final long[][] array) throws IOException {
	return loadLongs(new File(filename.toString()), byteOrder, array);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @return a big array filled with the content of the specified file.
	*/
public static long[][] loadLongsBig(final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	final long length = channel.size() / Long.BYTES;
	final long[][] array = LongBigArrays.newBigArray(length);
	for(final long[] t: array) loadLongs(channel, byteOrder, t);
	channel.close();
	return array;
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @return a big array filled with the content of the specified file.
	*/
public static long[][] loadLongsBig(final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	return loadLongsBig(new File(filename.toString()), byteOrder);
}
/** Stores an array fragment to a given writable channel, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code channel}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeLongs(final long array[][], final long offset, final long length, final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	 final int s = (int)Math.max(0, offset - start(i));
	 final int l = (int)Math.min(array[i].length, offset + length - start(i));
	 storeLongs(array[i], s, l - s, channel, byteOrder);
	}
}
/** Stores a big array to a given writable channel, using the given byte order.
	*
	* @param array a big array whose elements will be written to {@code channel}.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeLongs(final long array[][], final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	for(long[] t: array) storeLongs(t, channel, byteOrder);
}
/** Stores a big-array fragment to a file given by a {@link File} object, using the given byte order.
	*
	* @param array a big array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeLongs(final long array[][], final long offset, final long length, final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	storeLongs(array, offset, length, channel, byteOrder);
	channel.close();
}
/** Stores a big-array fragment to a file given by a filename, using the given byte order.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeLongs(final long array[][], final long offset, final long length, final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeLongs(array, offset, length, new File(filename.toString()), byteOrder);
}
/** Stores an array to a file given by a {@link File} object, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeLongs(final long array[][], final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	storeLongs(array, channel, byteOrder);
	channel.close();
}
/** Stores a big array to a file given by a filename, using the given byte order.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeLongs(final long array[][], final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeLongs(array, new File(filename.toString()), byteOrder);
}
/** Stores the element returned by an iterator to a given writable channel, using the given byte order.
	*
	* @param i an iterator whose output will be written to {@code channel}.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeLongs(final LongIterator i, final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE).order(byteOrder);
	final LongBuffer buffer = byteBuffer.asLongBuffer();
	while(i.hasNext()) {
	 if (! buffer.hasRemaining()) {
	  buffer.flip();
	  byteBuffer.clear();
	  byteBuffer.limit(buffer.limit() << LongMappedBigList.LOG2_BYTES);
	  channel.write(byteBuffer);
	  buffer.clear();
	 }
	 buffer.put(i.nextLong());
	}
	buffer.flip();
	byteBuffer.clear();
	byteBuffer.limit(buffer.limit() << LongMappedBigList.LOG2_BYTES);
	channel.write(byteBuffer);
}
/** Stores the element returned by an iterator to a file given by a {@link File} object, using the given byte order.
	*
	* @param i an iterator whose output will be written to {@code file}.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeLongs(final LongIterator i, final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	storeLongs(i, channel, byteOrder);
	channel.close();
}
/** Stores the element returned by an iterator to a file given by a filename, using the given byte order.
	*
	* @param i an iterator whose output will be written to the file {@code filename}.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeLongs(final LongIterator i, final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeLongs(i, new File(filename.toString()), byteOrder);
}
/** A wrapper that exhibits the content of a readable channel as a type-specific iterator. */
private static final class LongDataNioInputWrapper implements LongIterator {
	private final ReadableByteChannel channel;
	private final ByteBuffer byteBuffer;
	private final LongBuffer buffer;
	public LongDataNioInputWrapper(final ReadableByteChannel channel, final ByteOrder byteOrder) {
	 this.channel = channel;
	 byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE).order(byteOrder);
	 buffer = byteBuffer.asLongBuffer();
	 buffer.clear().flip();
	}
	@Override
	public boolean hasNext() {
	 if (! buffer.hasRemaining()) {
	  byteBuffer.clear();
	  try {
	   channel.read(byteBuffer);
	  } catch(IOException e) {
	   throw new RuntimeException(e);
	  }
	  byteBuffer.flip();
	  buffer.clear();
	  buffer.limit(byteBuffer.limit() >>> LongMappedBigList.LOG2_BYTES);
	 }
	 return buffer.hasRemaining();
	}
	@Override
	public long nextLong() {
	 if (! hasNext()) throw new NoSuchElementException();
	 return buffer.get();
	}
}
/** Wraps the given readable channel, using the given byte order, into an iterator.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	*/
public static LongIterator asLongIterator(final ReadableByteChannel channel, final ByteOrder byteOrder) {
	return new LongDataNioInputWrapper(channel, byteOrder);
}
/** Wraps a file given by a {@link File} object, using the given byte order, into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	*/
public static LongIterator asLongIterator(final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	return new LongDataNioInputWrapper(channel, byteOrder);
}
/** Wraps a file given by a filename, using the given byte order, into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	*/
public static LongIterator asLongIterator(final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	return asLongIterator(new File(filename.toString()), byteOrder);
}
/** Wraps a file given by a {@link File} object, using the given byte order, into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	*/
public static LongIterable asLongIterable(final File file, final ByteOrder byteOrder) {
	return () -> {
	 try { return asLongIterator(file, byteOrder); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
/** Wraps a file given by a filename, using the given byte order, into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	*/
public static LongIterable asLongIterable(final CharSequence filename, final ByteOrder byteOrder) {
	return () -> {
	 try { return asLongIterator(filename, byteOrder); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
/** Loads elements from a given data input, storing them in a given array fragment.
	*
	* @param dataInput a data input.
	* @param array an array which will be filled with data from {@code dataInput}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code dataInput} (it might be less than {@code length} if {@code dataInput} ends).
	*/
public static int loadLongs(final DataInput dataInput, final long[] array, final int offset, final int length) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	int i = 0;
	try {
	 for(i = 0; i < length; i++) array[i + offset] = dataInput.readLong();
	}
	catch(EOFException itsOk) {}
	return i;
}
/** Loads elements from a given data input, storing them in a given array.
	*
	* @param dataInput a data input.
	* @param array an array which will be filled with data from {@code dataInput}.
	* @return the number of elements actually read from {@code dataInput} (it might be less than the array length if {@code dataInput} ends).
	*/
public static int loadLongs(final DataInput dataInput, final long[] array) throws IOException {
	int i = 0;
	try {
	 final int length = array.length;
	 for(i = 0; i < length; i++) array[i] = dataInput.readLong();
	}
	catch(EOFException itsOk) {}
	return i;
}
/** Loads elements from a file given by a {@link File} object, storing them in a given array fragment.
	*
	* @param file a file.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadLongs(final File file, final long[] array, final int offset, final int length) throws IOException {
	return loadLongs(file, ByteOrder.BIG_ENDIAN, array, offset, length);
}
/** Loads elements from a file given by a filename, storing them in a given array fragment.
	*
	* @param filename a filename.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadLongs(final CharSequence filename, final long[] array, final int offset, final int length) throws IOException {
	return loadLongs(new File(filename.toString()), array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, storing them in a given array.
	*
	* @param file a file.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadLongs(final File file, final long[] array) throws IOException {
	return loadLongs(file, array, 0, array.length);
}
/** Loads elements from a file given by a filename, storing them in a given array.
	*
	* @param filename a filename.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadLongs(final CharSequence filename, final long[] array) throws IOException {
	return loadLongs(new File(filename.toString()), array);
}
/** Loads elements from a file given by a {@link File} object, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @return an array filled with the content of the specified file.
	*/
public static long[] loadLongs(final File file) throws IOException {
	return loadLongs(file, ByteOrder.BIG_ENDIAN);
}
/** Loads elements from a file given by a filename, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @return an array filled with the content of the specified file.
	*/
public static long[] loadLongs(final CharSequence filename) throws IOException {
	return loadLongs(new File(filename.toString()));
}
/** Stores an array fragment to a given data output.
	*
	* @param array an array whose elements will be written to {@code dataOutput}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param dataOutput a data output.
	*/
public static void storeLongs(final long array[], final int offset, final int length, final DataOutput dataOutput) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	for(int i = 0; i < length; i++) dataOutput.writeLong(array[offset + i]);
}
/** Stores an array to a given data output.
	*
	* @param array an array whose elements will be written to {@code dataOutput}.
	* @param dataOutput a data output.
	*/
public static void storeLongs(final long array[], final DataOutput dataOutput) throws IOException {
	final int length = array.length;
	for(int i = 0; i < length; i++) dataOutput.writeLong(array[i]);
}
/** Stores an array fragment to a file given by a {@link File} object.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	*/
public static void storeLongs(final long array[], final int offset, final int length, final File file) throws IOException {
	storeLongs(array, offset, length, file, ByteOrder.BIG_ENDIAN);
}
/** Stores an array fragment to a file given by a filename.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	*/
public static void storeLongs(final long array[], final int offset, final int length, final CharSequence filename) throws IOException {
	storeLongs(array, offset, length, new File(filename.toString()));
}
/** Stores an array to a file given by a {@link File} object.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	*/
public static void storeLongs(final long array[], final File file) throws IOException {
	storeLongs(array, 0, array.length, file);
}
/** Stores an array to a file given by a filename.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	*/
public static void storeLongs(final long array[], final CharSequence filename) throws IOException {
	storeLongs(array, new File(filename.toString()));
}
/** Loads elements from a given data input, storing them in a given big-array fragment.
	*
	* @param dataInput a data input.
	* @param array a big array which will be filled with data from {@code dataInput}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code dataInput} (it might be less than {@code length} if {@code dataInput} ends).
	*/
public static long loadLongs(final DataInput dataInput, final long[][] array, final long offset, final long length) throws IOException {
	ensureOffsetLength(array, offset, length);
	long c = 0;
	try {
	 for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	  final long[] t = array[i];
	  final int l = (int)Math.min(t.length, offset + length - start(i));
	  for(int d = (int)Math.max(0, offset - start(i)); d < l; d++) {
	   t[d] = dataInput.readLong();
	   c++;
	  }
	 }
	}
	catch(EOFException itsOk) {}
	return c;
}
/** Loads elements from a given data input, storing them in a given big array.
	*
	* @param dataInput a data input.
	* @param array a big array which will be filled with data from {@code dataInput}.
	* @return the number of elements actually read from {@code dataInput} (it might be less than the array length if {@code dataInput} ends).
	*/
public static long loadLongs(final DataInput dataInput, final long[][] array) throws IOException {
	long c = 0;
	try {
	 for(int i = 0; i < array.length; i++) {
	  final long[] t = array[i];
	  final int l = t.length;
	  for(int d = 0; d < l; d++) {
	   t[d] = dataInput.readLong();
	   c++;
	  }
	 }
	}
	catch(EOFException itsOk) {}
	return c;
}
/** Loads elements from a file given by a {@link File} object, storing them in a given big-array fragment.
	*
	* @param file a file.
	* @param array a big array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadLongs(final File file, final long[][] array, final long offset, final long length) throws IOException {
	return loadLongs(file, ByteOrder.BIG_ENDIAN, array, offset, length);
}
/** Loads elements from a file given by a filename, storing them in a given big-array fragment.
	*
	* @param filename a filename.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadLongs(final CharSequence filename, final long[][] array, final long offset, final long length) throws IOException {
	return loadLongs(new File(filename.toString()), array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, storing them in a given big array.
	*
	* @param file a file.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadLongs(final File file, final long[][] array) throws IOException {
	return loadLongs(file, ByteOrder.BIG_ENDIAN, array);
}
/** Loads elements from a file given by a filename, storing them in a given big array.
	*
	* @param filename a filename.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadLongs(final CharSequence filename, final long[][] array) throws IOException {
	return loadLongs(new File(filename.toString()), array);
}
/** Loads elements from a file given by a {@link File} object, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @return a big array filled with the content of the specified file.
	*/
public static long[][] loadLongsBig(final File file) throws IOException {
	return loadLongsBig(file, ByteOrder.BIG_ENDIAN);
}
/** Loads elements from a file given by a filename, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @return a big array filled with the content of the specified file.
	*/
public static long[][] loadLongsBig(final CharSequence filename) throws IOException {
	return loadLongsBig(new File(filename.toString()));
}
/** Stores an array fragment to a given data output.
	*
	* @param array an array whose elements will be written to {@code dataOutput}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param dataOutput a data output.
	*/
public static void storeLongs(final long array[][], final long offset, final long length, final DataOutput dataOutput) throws IOException {
	ensureOffsetLength(array, offset, length);
	for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	 final long[] t = array[i];
	 final int l = (int)Math.min(t.length, offset + length - start(i));
	 for(int d = (int)Math.max(0, offset - start(i)); d < l; d++) dataOutput.writeLong(t[d]);
	}
}
/** Stores a big array to a given data output.
	*
	* @param array a big array whose elements will be written to {@code dataOutput}.
	* @param dataOutput a data output.
	*/
public static void storeLongs(final long array[][], final DataOutput dataOutput) throws IOException {
	for(int i = 0; i < array.length; i++) {
	 final long[] t = array[i];
	 final int l = t.length;
	 for(int d = 0; d < l; d++) dataOutput.writeLong(t[d]);
	}
}
/** Stores a big-array fragment to a file given by a {@link File} object.
	*
	* @param array a big array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	*/
public static void storeLongs(final long array[][], final long offset, final long length, final File file) throws IOException {
	storeLongs(array, offset, length, file, ByteOrder.BIG_ENDIAN);
}
/** Stores a big-array fragment to a file given by a filename.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	*/
public static void storeLongs(final long array[][], final long offset, final long length, final CharSequence filename) throws IOException {
	storeLongs(array, offset, length, new File(filename.toString()));
}
/** Stores an array to a file given by a {@link File} object.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	*/
public static void storeLongs(final long array[][], final File file) throws IOException {
	storeLongs(array, file, ByteOrder.BIG_ENDIAN);
}
/** Stores a big array to a file given by a filename.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	*/
public static void storeLongs(final long array[][], final CharSequence filename) throws IOException {
	storeLongs(array, new File(filename.toString()));
}
/** Stores the element returned by an iterator to a given data output.
	*
	* @param i an iterator whose output will be written to {@code dataOutput}.
	* @param dataOutput a filename.
	*/
public static void storeLongs(final LongIterator i, final DataOutput dataOutput) throws IOException {
	while(i.hasNext()) dataOutput.writeLong(i.nextLong());
}
/** Stores the element returned by an iterator to a file given by a {@link File} object.
	*
	* @param i an iterator whose output will be written to {@code file}.
	* @param file a file.
	*/
public static void storeLongs(final LongIterator i, final File file) throws IOException {
	storeLongs(i, file, ByteOrder.BIG_ENDIAN);
}
/** Stores the element returned by an iterator to a file given by a filename.
	*
	* @param i an iterator whose output will be written to the file {@code filename}.
	* @param filename a filename.
	*/
public static void storeLongs(final LongIterator i, final CharSequence filename) throws IOException {
	storeLongs(i, new File(filename.toString()));
}
/** A wrapper that exhibits the content of a data input stream as a type-specific iterator. */
private static final class LongDataInputWrapper implements LongIterator {
	private final DataInput dataInput;
	private boolean toAdvance = true;
	private boolean endOfProcess = false;
	private long next;
	public LongDataInputWrapper(final DataInput dataInput) {
	 this.dataInput = dataInput;
	}
	@Override
	public boolean hasNext() {
	 if (! toAdvance) return ! endOfProcess;
	 toAdvance = false;
	 try { next = dataInput.readLong(); }
	 catch(EOFException eof) { endOfProcess = true; }
	 catch(IOException rethrow) { throw new RuntimeException(rethrow); }
	 return ! endOfProcess;
	}
	@Override
	public long nextLong() {
	 if (! hasNext()) throw new NoSuchElementException();
	 toAdvance = true;
	 return next;
	}
}
/** Wraps the given data input stream into an iterator.
	*
	* @param dataInput a data input.
	*/
public static LongIterator asLongIterator(final DataInput dataInput) {
	return new LongDataInputWrapper(dataInput);
}
/** Wraps a file given by a {@link File} object into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param file a file.
	*/
public static LongIterator asLongIterator(final File file) throws IOException {
	return asLongIterator(file, ByteOrder.BIG_ENDIAN);
}
/** Wraps a file given by a filename into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param filename a filename.
	*/
public static LongIterator asLongIterator(final CharSequence filename) throws IOException {
	return asLongIterator(new File(filename.toString()));
}
/** Wraps a file given by a {@link File} object into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param file a file.
	*/
public static LongIterable asLongIterable(final File file) {
	return () -> {
	 try { return asLongIterator(file); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
/** Wraps a file given by a filename into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param filename a filename.
	*/
public static LongIterable asLongIterable(final CharSequence filename) {
	return () -> {
	 try { return asLongIterator(filename); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
/* Generic definitions */
/* Assertions (useful to generate conditional code) */
/* Narrowing and widening */
/* Current type and class (and size, if applicable) */
/* Value methods */
/* Interfaces (keys) */
/* Interfaces (values) */
/* Types and methods related to primitive-type support in the JDK */
/* Abstract implementations (keys) */
/* Abstract implementations (values) */
/* Static containers (keys) */
/* Static containers (values) */
/* Implementations */
/* Synchronized wrappers */
/* Unmodifiable wrappers */
/* Other wrappers */
/* Methods (keys) */
/* Methods (values) */
/* Methods (keys/values) */
/* Methods that have special names depending on keys (but the special names depend on values) */
/* Equality */
/* Object/Reference-only definitions (keys) */
/* Primitive-type-only definitions (keys) */
/* Object/Reference-only definitions (values) */
/* START_OF_JAVA_SOURCE */
/*
	* Copyright (C) 2004-2024 Sebastiano Vigna
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
/** Loads elements from a given readable channel, using the given byte order, storing them in a given array fragment.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	* @param array an array which will be filled with data from {@code channel}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code channel} (it might be less than {@code length} if {@code channel} ends).
	*/
public static int loadDoubles(final ReadableByteChannel channel, final ByteOrder byteOrder, final double[] array, int offset, int length) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE).order(byteOrder);
	final DoubleBuffer buffer = byteBuffer.asDoubleBuffer();
	int read = 0;
	for (;;) {
	 byteBuffer.clear();
	 byteBuffer.limit((int)Math.min(buffer.capacity(), (long)length << DoubleMappedBigList.LOG2_BYTES));
	 int r = channel.read(byteBuffer);
	 if (r <= 0) return read;
	 r >>>= DoubleMappedBigList.LOG2_BYTES;
	 read += r;
	 // TODO: use the indexed get() method when switching to Java 13+
	 buffer.clear();
	 buffer.limit(r);
	 buffer.get(array, offset, r);
	 offset += r;
	 length -= r;
	}
}
/** Loads elements from a given readable channel, using the given byte order, storing them in a given array.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	* @param array an array which will be filled with data from {@code channel}.
	* @return the number of elements actually read from {@code channel} (it might be less than the array length if {@code channel} ends).
	*/
public static int loadDoubles(final ReadableByteChannel channel, final ByteOrder byteOrder, final double[] array) throws IOException {
	return loadDoubles(channel, byteOrder, array, 0, array.length);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a given array fragment.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadDoubles(final File file, final ByteOrder byteOrder, final double[] array, final int offset, final int length) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	final FileChannel channel = FileChannel.open(file.toPath());
	final int read = loadDoubles(channel, byteOrder, array, offset, length);
	channel.close();
	return read;
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a given array fragment.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadDoubles(final CharSequence filename, final ByteOrder byteOrder, final double[] array, final int offset, final int length) throws IOException {
	return loadDoubles(new File(filename.toString()), byteOrder, array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a given array.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadDoubles(final File file, final ByteOrder byteOrder, final double[] array) throws IOException {
	return loadDoubles(file, byteOrder, array, 0, array.length);
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a given array.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadDoubles(final CharSequence filename, final ByteOrder byteOrder, final double[] array) throws IOException {
	return loadDoubles(new File(filename.toString()), byteOrder, array);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @return an array filled with the content of the specified file.
	*/
public static double[] loadDoubles(final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	final long length = channel.size() / Double.BYTES;
	if (length > Integer.MAX_VALUE) {
	 channel.close();
	 throw new IllegalArgumentException("File too long: " + channel.size()+ " bytes (" + length + " elements)");
	}
	final double[] array = new double[(int)length];
	if (loadDoubles(channel, byteOrder, array) < length) throw new EOFException();
	channel.close();
	return array;
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @return an array filled with the content of the specified file.
	*/
public static double[] loadDoubles(final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	return loadDoubles(new File(filename.toString()), byteOrder);
}
/** Stores an array fragment to a given writable channel, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code channel}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeDoubles(final double array[], int offset, int length, final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE).order(byteOrder);
	final DoubleBuffer buffer = byteBuffer.asDoubleBuffer();
	while(length != 0) {
	 final int l = Math.min(length, buffer.capacity());
	 buffer.clear();
	 buffer.put(array, offset, l);
	 buffer.flip();
	 byteBuffer.clear();
	 byteBuffer.limit(buffer.limit() << DoubleMappedBigList.LOG2_BYTES);
	 channel.write(byteBuffer);
	 offset += l;
	 length -= l;
	}
}
/** Stores an array to a given writable channel, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code channel}.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeDoubles(final double array[], final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	storeDoubles(array, 0, array.length, channel, byteOrder);
}
/** Stores an array fragment to a file given by a {@link File} object, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeDoubles(final double array[], final int offset, final int length, final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	storeDoubles(array, offset, length, channel, byteOrder);
	channel.close();
}
/** Stores an array fragment to a file given by a filename, using the given byte order.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeDoubles(final double array[], final int offset, final int length, final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeDoubles(array, offset, length, new File(filename.toString()), byteOrder);
}
/** Stores an array to a file given by a {@link File} object, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeDoubles(final double array[], final File file, final ByteOrder byteOrder) throws IOException {
	storeDoubles(array, 0, array.length, file, byteOrder);
}
/** Stores an array to a file given by a filename, using the given byte order.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeDoubles(final double array[], final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeDoubles(array, new File(filename.toString()), byteOrder);
}
/** Loads elements from a given readable channel, using the given byte order, storing them in a given big-array fragment.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	* @param array a big array which will be filled with data from {@code channel}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code channel} (it might be less than {@code length} if {@code channel} ends).
	*/
public static long loadDoubles(final ReadableByteChannel channel, final ByteOrder byteOrder, final double[][] array, final long offset, final long length) throws IOException {
	ensureOffsetLength(array, offset, length);
	long read = 0;
	for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	 final double[] t = array[i];
	 final int s = (int)Math.max(0, offset - start(i));
	 final int e = (int)Math.min(t.length, offset + length - start(i));
	 final int r = loadDoubles(channel, byteOrder, t, s, e - s);
	 read += r;
	 if (r < e -s) break;
	}
	return read;
}
/** Loads elements from a given readable channel, using the given byte order, storing them in a given big array.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	* @param array a big array which will be filled with data from {@code channel}.
	* @return the number of elements actually read from {@code channel} (it might be less than the array length if {@code channel} ends).
	*/
public static long loadDoubles(final ReadableByteChannel channel, final ByteOrder byteOrder, final double[][] array) throws IOException {
	return loadDoubles(channel, byteOrder, array, 0, length(array));
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a given big-array fragment.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @param array a big array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadDoubles(final File file, final ByteOrder byteOrder, final double[][] array, final long offset, final long length) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	final long read = loadDoubles(channel, byteOrder, array, offset, length);
	channel.close();
	return read;
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a given big-array fragment.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadDoubles(final CharSequence filename, final ByteOrder byteOrder, final double[][] array, final long offset, final long length) throws IOException {
	return loadDoubles(new File(filename.toString()), byteOrder, array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a given big array.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadDoubles(final File file, final ByteOrder byteOrder, final double[][] array) throws IOException {
	return loadDoubles(file, byteOrder, array, 0, length(array));
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a given big array.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadDoubles(final CharSequence filename, final ByteOrder byteOrder, final double[][] array) throws IOException {
	return loadDoubles(new File(filename.toString()), byteOrder, array);
}
/** Loads elements from a file given by a {@link File} object, using the given byte order, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	* @return a big array filled with the content of the specified file.
	*/
public static double[][] loadDoublesBig(final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	final long length = channel.size() / Double.BYTES;
	final double[][] array = DoubleBigArrays.newBigArray(length);
	for(final double[] t: array) loadDoubles(channel, byteOrder, t);
	channel.close();
	return array;
}
/** Loads elements from a file given by a filename, using the given byte order, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	* @return a big array filled with the content of the specified file.
	*/
public static double[][] loadDoublesBig(final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	return loadDoublesBig(new File(filename.toString()), byteOrder);
}
/** Stores an array fragment to a given writable channel, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code channel}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeDoubles(final double array[][], final long offset, final long length, final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	 final int s = (int)Math.max(0, offset - start(i));
	 final int l = (int)Math.min(array[i].length, offset + length - start(i));
	 storeDoubles(array[i], s, l - s, channel, byteOrder);
	}
}
/** Stores a big array to a given writable channel, using the given byte order.
	*
	* @param array a big array whose elements will be written to {@code channel}.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeDoubles(final double array[][], final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	for(double[] t: array) storeDoubles(t, channel, byteOrder);
}
/** Stores a big-array fragment to a file given by a {@link File} object, using the given byte order.
	*
	* @param array a big array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeDoubles(final double array[][], final long offset, final long length, final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	storeDoubles(array, offset, length, channel, byteOrder);
	channel.close();
}
/** Stores a big-array fragment to a file given by a filename, using the given byte order.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeDoubles(final double array[][], final long offset, final long length, final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeDoubles(array, offset, length, new File(filename.toString()), byteOrder);
}
/** Stores an array to a file given by a {@link File} object, using the given byte order.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeDoubles(final double array[][], final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	storeDoubles(array, channel, byteOrder);
	channel.close();
}
/** Stores a big array to a file given by a filename, using the given byte order.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeDoubles(final double array[][], final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeDoubles(array, new File(filename.toString()), byteOrder);
}
/** Stores the element returned by an iterator to a given writable channel, using the given byte order.
	*
	* @param i an iterator whose output will be written to {@code channel}.
	* @param channel a writable channel.
	* @param byteOrder the byte order to be used to store data in {@code channel}.
	*/
public static void storeDoubles(final DoubleIterator i, final WritableByteChannel channel, final ByteOrder byteOrder) throws IOException {
	ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE).order(byteOrder);
	final DoubleBuffer buffer = byteBuffer.asDoubleBuffer();
	while(i.hasNext()) {
	 if (! buffer.hasRemaining()) {
	  buffer.flip();
	  byteBuffer.clear();
	  byteBuffer.limit(buffer.limit() << DoubleMappedBigList.LOG2_BYTES);
	  channel.write(byteBuffer);
	  buffer.clear();
	 }
	 buffer.put(i.nextDouble());
	}
	buffer.flip();
	byteBuffer.clear();
	byteBuffer.limit(buffer.limit() << DoubleMappedBigList.LOG2_BYTES);
	channel.write(byteBuffer);
}
/** Stores the element returned by an iterator to a file given by a {@link File} object, using the given byte order.
	*
	* @param i an iterator whose output will be written to {@code file}.
	* @param file a file.
	* @param byteOrder the byte order to be used to store data in {@code file}.
	*/
public static void storeDoubles(final DoubleIterator i, final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	storeDoubles(i, channel, byteOrder);
	channel.close();
}
/** Stores the element returned by an iterator to a file given by a filename, using the given byte order.
	*
	* @param i an iterator whose output will be written to the file {@code filename}.
	* @param filename a filename.
	* @param byteOrder the byte order to be used to store data in the file {@code filename}.
	*/
public static void storeDoubles(final DoubleIterator i, final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	storeDoubles(i, new File(filename.toString()), byteOrder);
}
/** A wrapper that exhibits the content of a readable channel as a type-specific iterator. */
private static final class DoubleDataNioInputWrapper implements DoubleIterator {
	private final ReadableByteChannel channel;
	private final ByteBuffer byteBuffer;
	private final DoubleBuffer buffer;
	public DoubleDataNioInputWrapper(final ReadableByteChannel channel, final ByteOrder byteOrder) {
	 this.channel = channel;
	 byteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE).order(byteOrder);
	 buffer = byteBuffer.asDoubleBuffer();
	 buffer.clear().flip();
	}
	@Override
	public boolean hasNext() {
	 if (! buffer.hasRemaining()) {
	  byteBuffer.clear();
	  try {
	   channel.read(byteBuffer);
	  } catch(IOException e) {
	   throw new RuntimeException(e);
	  }
	  byteBuffer.flip();
	  buffer.clear();
	  buffer.limit(byteBuffer.limit() >>> DoubleMappedBigList.LOG2_BYTES);
	 }
	 return buffer.hasRemaining();
	}
	@Override
	public double nextDouble() {
	 if (! hasNext()) throw new NoSuchElementException();
	 return buffer.get();
	}
}
/** Wraps the given readable channel, using the given byte order, into an iterator.
	*
	* @param channel a readable channel.
	* @param byteOrder the byte order of the data from {@code channel}.
	*/
public static DoubleIterator asDoubleIterator(final ReadableByteChannel channel, final ByteOrder byteOrder) {
	return new DoubleDataNioInputWrapper(channel, byteOrder);
}
/** Wraps a file given by a {@link File} object, using the given byte order, into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	*/
public static DoubleIterator asDoubleIterator(final File file, final ByteOrder byteOrder) throws IOException {
	final FileChannel channel = FileChannel.open(file.toPath());
	return new DoubleDataNioInputWrapper(channel, byteOrder);
}
/** Wraps a file given by a filename, using the given byte order, into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	*/
public static DoubleIterator asDoubleIterator(final CharSequence filename, final ByteOrder byteOrder) throws IOException {
	return asDoubleIterator(new File(filename.toString()), byteOrder);
}
/** Wraps a file given by a {@link File} object, using the given byte order, into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param file a file.
	* @param byteOrder the byte order of the data stored in {@code file}.
	*/
public static DoubleIterable asDoubleIterable(final File file, final ByteOrder byteOrder) {
	return () -> {
	 try { return asDoubleIterator(file, byteOrder); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
/** Wraps a file given by a filename, using the given byte order, into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param filename a filename.
	* @param byteOrder the byte order of the data stored in the file {@code filename}.
	*/
public static DoubleIterable asDoubleIterable(final CharSequence filename, final ByteOrder byteOrder) {
	return () -> {
	 try { return asDoubleIterator(filename, byteOrder); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
/** Loads elements from a given data input, storing them in a given array fragment.
	*
	* @param dataInput a data input.
	* @param array an array which will be filled with data from {@code dataInput}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code dataInput} (it might be less than {@code length} if {@code dataInput} ends).
	*/
public static int loadDoubles(final DataInput dataInput, final double[] array, final int offset, final int length) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	int i = 0;
	try {
	 for(i = 0; i < length; i++) array[i + offset] = dataInput.readDouble();
	}
	catch(EOFException itsOk) {}
	return i;
}
/** Loads elements from a given data input, storing them in a given array.
	*
	* @param dataInput a data input.
	* @param array an array which will be filled with data from {@code dataInput}.
	* @return the number of elements actually read from {@code dataInput} (it might be less than the array length if {@code dataInput} ends).
	*/
public static int loadDoubles(final DataInput dataInput, final double[] array) throws IOException {
	int i = 0;
	try {
	 final int length = array.length;
	 for(i = 0; i < length; i++) array[i] = dataInput.readDouble();
	}
	catch(EOFException itsOk) {}
	return i;
}
/** Loads elements from a file given by a {@link File} object, storing them in a given array fragment.
	*
	* @param file a file.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadDoubles(final File file, final double[] array, final int offset, final int length) throws IOException {
	return loadDoubles(file, ByteOrder.BIG_ENDIAN, array, offset, length);
}
/** Loads elements from a file given by a filename, storing them in a given array fragment.
	*
	* @param filename a filename.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static int loadDoubles(final CharSequence filename, final double[] array, final int offset, final int length) throws IOException {
	return loadDoubles(new File(filename.toString()), array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, storing them in a given array.
	*
	* @param file a file.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadDoubles(final File file, final double[] array) throws IOException {
	return loadDoubles(file, array, 0, array.length);
}
/** Loads elements from a file given by a filename, storing them in a given array.
	*
	* @param filename a filename.
	* @param array an array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static int loadDoubles(final CharSequence filename, final double[] array) throws IOException {
	return loadDoubles(new File(filename.toString()), array);
}
/** Loads elements from a file given by a {@link File} object, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @return an array filled with the content of the specified file.
	*/
public static double[] loadDoubles(final File file) throws IOException {
	return loadDoubles(file, ByteOrder.BIG_ENDIAN);
}
/** Loads elements from a file given by a filename, storing them in a new array.
	*
	* <p>Note that the length of the returned array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @return an array filled with the content of the specified file.
	*/
public static double[] loadDoubles(final CharSequence filename) throws IOException {
	return loadDoubles(new File(filename.toString()));
}
/** Stores an array fragment to a given data output.
	*
	* @param array an array whose elements will be written to {@code dataOutput}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param dataOutput a data output.
	*/
public static void storeDoubles(final double array[], final int offset, final int length, final DataOutput dataOutput) throws IOException {
	ensureOffsetLength(array.length, offset, length);
	for(int i = 0; i < length; i++) dataOutput.writeDouble(array[offset + i]);
}
/** Stores an array to a given data output.
	*
	* @param array an array whose elements will be written to {@code dataOutput}.
	* @param dataOutput a data output.
	*/
public static void storeDoubles(final double array[], final DataOutput dataOutput) throws IOException {
	final int length = array.length;
	for(int i = 0; i < length; i++) dataOutput.writeDouble(array[i]);
}
/** Stores an array fragment to a file given by a {@link File} object.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	*/
public static void storeDoubles(final double array[], final int offset, final int length, final File file) throws IOException {
	storeDoubles(array, offset, length, file, ByteOrder.BIG_ENDIAN);
}
/** Stores an array fragment to a file given by a filename.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	*/
public static void storeDoubles(final double array[], final int offset, final int length, final CharSequence filename) throws IOException {
	storeDoubles(array, offset, length, new File(filename.toString()));
}
/** Stores an array to a file given by a {@link File} object.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	*/
public static void storeDoubles(final double array[], final File file) throws IOException {
	storeDoubles(array, 0, array.length, file);
}
/** Stores an array to a file given by a filename.
	*
	* @param array an array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	*/
public static void storeDoubles(final double array[], final CharSequence filename) throws IOException {
	storeDoubles(array, new File(filename.toString()));
}
/** Loads elements from a given data input, storing them in a given big-array fragment.
	*
	* @param dataInput a data input.
	* @param array a big array which will be filled with data from {@code dataInput}.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from {@code dataInput} (it might be less than {@code length} if {@code dataInput} ends).
	*/
public static long loadDoubles(final DataInput dataInput, final double[][] array, final long offset, final long length) throws IOException {
	ensureOffsetLength(array, offset, length);
	long c = 0;
	try {
	 for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	  final double[] t = array[i];
	  final int l = (int)Math.min(t.length, offset + length - start(i));
	  for(int d = (int)Math.max(0, offset - start(i)); d < l; d++) {
	   t[d] = dataInput.readDouble();
	   c++;
	  }
	 }
	}
	catch(EOFException itsOk) {}
	return c;
}
/** Loads elements from a given data input, storing them in a given big array.
	*
	* @param dataInput a data input.
	* @param array a big array which will be filled with data from {@code dataInput}.
	* @return the number of elements actually read from {@code dataInput} (it might be less than the array length if {@code dataInput} ends).
	*/
public static long loadDoubles(final DataInput dataInput, final double[][] array) throws IOException {
	long c = 0;
	try {
	 for(int i = 0; i < array.length; i++) {
	  final double[] t = array[i];
	  final int l = t.length;
	  for(int d = 0; d < l; d++) {
	   t[d] = dataInput.readDouble();
	   c++;
	  }
	 }
	}
	catch(EOFException itsOk) {}
	return c;
}
/** Loads elements from a file given by a {@link File} object, storing them in a given big-array fragment.
	*
	* @param file a file.
	* @param array a big array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadDoubles(final File file, final double[][] array, final long offset, final long length) throws IOException {
	return loadDoubles(file, ByteOrder.BIG_ENDIAN, array, offset, length);
}
/** Loads elements from a file given by a filename, storing them in a given big-array fragment.
	*
	* @param filename a filename.
	* @param array an array which will be filled with data from the specified file.
	* @param offset the index of the first element of {@code array} to be filled.
	* @param length the number of elements of {@code array} to be filled.
	* @return the number of elements actually read from the given file (it might be less than {@code length} if the file is too short).
	*/
public static long loadDoubles(final CharSequence filename, final double[][] array, final long offset, final long length) throws IOException {
	return loadDoubles(new File(filename.toString()), array, offset, length);
}
/** Loads elements from a file given by a {@link File} object, storing them in a given big array.
	*
	* @param file a file.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadDoubles(final File file, final double[][] array) throws IOException {
	return loadDoubles(file, ByteOrder.BIG_ENDIAN, array);
}
/** Loads elements from a file given by a filename, storing them in a given big array.
	*
	* @param filename a filename.
	* @param array a big array which will be filled with data from the specified file.
	* @return the number of elements actually read from the given file (it might be less than the array length if the file is too short).
	*/
public static long loadDoubles(final CharSequence filename, final double[][] array) throws IOException {
	return loadDoubles(new File(filename.toString()), array);
}
/** Loads elements from a file given by a {@link File} object, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param file a file.
	* @return a big array filled with the content of the specified file.
	*/
public static double[][] loadDoublesBig(final File file) throws IOException {
	return loadDoublesBig(file, ByteOrder.BIG_ENDIAN);
}
/** Loads elements from a file given by a filename, storing them in a new big array.
	*
	* <p>Note that the length of the returned big array will be computed
	* dividing the specified file size by the number of bytes used to
	* represent each element.
	*
	* @param filename a filename.
	* @return a big array filled with the content of the specified file.
	*/
public static double[][] loadDoublesBig(final CharSequence filename) throws IOException {
	return loadDoublesBig(new File(filename.toString()));
}
/** Stores an array fragment to a given data output.
	*
	* @param array an array whose elements will be written to {@code dataOutput}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param dataOutput a data output.
	*/
public static void storeDoubles(final double array[][], final long offset, final long length, final DataOutput dataOutput) throws IOException {
	ensureOffsetLength(array, offset, length);
	for(int i = segment(offset); i < segment(offset + length + SEGMENT_MASK); i++) {
	 final double[] t = array[i];
	 final int l = (int)Math.min(t.length, offset + length - start(i));
	 for(int d = (int)Math.max(0, offset - start(i)); d < l; d++) dataOutput.writeDouble(t[d]);
	}
}
/** Stores a big array to a given data output.
	*
	* @param array a big array whose elements will be written to {@code dataOutput}.
	* @param dataOutput a data output.
	*/
public static void storeDoubles(final double array[][], final DataOutput dataOutput) throws IOException {
	for(int i = 0; i < array.length; i++) {
	 final double[] t = array[i];
	 final int l = t.length;
	 for(int d = 0; d < l; d++) dataOutput.writeDouble(t[d]);
	}
}
/** Stores a big-array fragment to a file given by a {@link File} object.
	*
	* @param array a big array whose elements will be written to {@code file}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param file a file.
	*/
public static void storeDoubles(final double array[][], final long offset, final long length, final File file) throws IOException {
	storeDoubles(array, offset, length, file, ByteOrder.BIG_ENDIAN);
}
/** Stores a big-array fragment to a file given by a filename.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param offset the index of the first element of {@code array} to be written.
	* @param length the number of elements of {@code array} to be written.
	* @param filename a filename.
	*/
public static void storeDoubles(final double array[][], final long offset, final long length, final CharSequence filename) throws IOException {
	storeDoubles(array, offset, length, new File(filename.toString()));
}
/** Stores an array to a file given by a {@link File} object.
	*
	* @param array an array whose elements will be written to {@code file}.
	* @param file a file.
	*/
public static void storeDoubles(final double array[][], final File file) throws IOException {
	storeDoubles(array, file, ByteOrder.BIG_ENDIAN);
}
/** Stores a big array to a file given by a filename.
	*
	* @param array a big array whose elements will be written to the file {@code filename}.
	* @param filename a filename.
	*/
public static void storeDoubles(final double array[][], final CharSequence filename) throws IOException {
	storeDoubles(array, new File(filename.toString()));
}
/** Stores the element returned by an iterator to a given data output.
	*
	* @param i an iterator whose output will be written to {@code dataOutput}.
	* @param dataOutput a filename.
	*/
public static void storeDoubles(final DoubleIterator i, final DataOutput dataOutput) throws IOException {
	while(i.hasNext()) dataOutput.writeDouble(i.nextDouble());
}
/** Stores the element returned by an iterator to a file given by a {@link File} object.
	*
	* @param i an iterator whose output will be written to {@code file}.
	* @param file a file.
	*/
public static void storeDoubles(final DoubleIterator i, final File file) throws IOException {
	storeDoubles(i, file, ByteOrder.BIG_ENDIAN);
}
/** Stores the element returned by an iterator to a file given by a filename.
	*
	* @param i an iterator whose output will be written to the file {@code filename}.
	* @param filename a filename.
	*/
public static void storeDoubles(final DoubleIterator i, final CharSequence filename) throws IOException {
	storeDoubles(i, new File(filename.toString()));
}
/** A wrapper that exhibits the content of a data input stream as a type-specific iterator. */
private static final class DoubleDataInputWrapper implements DoubleIterator {
	private final DataInput dataInput;
	private boolean toAdvance = true;
	private boolean endOfProcess = false;
	private double next;
	public DoubleDataInputWrapper(final DataInput dataInput) {
	 this.dataInput = dataInput;
	}
	@Override
	public boolean hasNext() {
	 if (! toAdvance) return ! endOfProcess;
	 toAdvance = false;
	 try { next = dataInput.readDouble(); }
	 catch(EOFException eof) { endOfProcess = true; }
	 catch(IOException rethrow) { throw new RuntimeException(rethrow); }
	 return ! endOfProcess;
	}
	@Override
	public double nextDouble() {
	 if (! hasNext()) throw new NoSuchElementException();
	 toAdvance = true;
	 return next;
	}
}
/** Wraps the given data input stream into an iterator.
	*
	* @param dataInput a data input.
	*/
public static DoubleIterator asDoubleIterator(final DataInput dataInput) {
	return new DoubleDataInputWrapper(dataInput);
}
/** Wraps a file given by a {@link File} object into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param file a file.
	*/
public static DoubleIterator asDoubleIterator(final File file) throws IOException {
	return asDoubleIterator(file, ByteOrder.BIG_ENDIAN);
}
/** Wraps a file given by a filename into an iterator.
	*
	* @implNote This method opens a {@link FileChannel} that will not be closed until
	* it is garbage collected.
	*
	* @param filename a filename.
	*/
public static DoubleIterator asDoubleIterator(final CharSequence filename) throws IOException {
	return asDoubleIterator(new File(filename.toString()));
}
/** Wraps a file given by a {@link File} object into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param file a file.
	*/
public static DoubleIterable asDoubleIterable(final File file) {
	return () -> {
	 try { return asDoubleIterator(file); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
/** Wraps a file given by a filename into an iterable object.
	*
	* @implNote Each iterator returned by this class opens a {@link FileChannel}
	* that will not be closed until it is garbage collected.
	*
	* @param filename a filename.
	*/
public static DoubleIterable asDoubleIterable(final CharSequence filename) {
	return () -> {
	 try { return asDoubleIterator(filename); }
	 catch(IOException e) { throw new RuntimeException(e); }
	};
}
}
