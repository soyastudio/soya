package soya.framework.commons.reflect.descriptor;

import com.google.common.base.*;
import com.google.common.collect.*;
import com.google.common.io.*;
import com.google.common.primitives.Primitives;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.*;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.*;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;

public final class ArbitraryInstances {

    private static final Ordering<Field> BY_FIELD_NAME =
            new Ordering<Field>() {
                @Override
                public int compare(Field left, Field right) {
                    return left.getName().compareTo(right.getName());
                }
            };

    /**
     * Returns a new {@code MatchResult} that corresponds to a successful match. Apache Harmony (used
     * in Android) requires a successful match in order to generate a {@code MatchResult}:
     * http://goo.gl/5VQFmC
     */
    private static MatchResult newMatchResult() {
        Matcher matcher = Pattern.compile(".").matcher("X");
        matcher.find();
        return matcher.toMatchResult();
    }

    private static final ClassToInstanceMap<Object> DEFAULTS =
            ImmutableClassToInstanceMap.builder()
                    // primitives
                    .put(Object.class, "")
                    .put(Number.class, 0)
                    .put(UnsignedInteger.class, UnsignedInteger.ZERO)
                    .put(UnsignedLong.class, UnsignedLong.ZERO)
                    .put(BigInteger.class, BigInteger.ZERO)
                    .put(BigDecimal.class, BigDecimal.ZERO)
                    .put(CharSequence.class, "")
                    .put(String.class, "")
                    .put(Pattern.class, Pattern.compile(""))
                    .put(MatchResult.class, newMatchResult())
                    .put(TimeUnit.class, TimeUnit.SECONDS)
                    .put(Charset.class, Charsets.UTF_8)
                    .put(Currency.class, Currency.getInstance(Locale.US))
                    .put(Locale.class, Locale.US)
                    .put(Optional.class, Optional.empty())
                    .put(OptionalInt.class, OptionalInt.empty())
                    .put(OptionalLong.class, OptionalLong.empty())
                    .put(OptionalDouble.class, OptionalDouble.empty())
                    .put(UUID.class, UUID.randomUUID())
                    // common.base
                    .put(CharMatcher.class, CharMatcher.none())
                    .put(Joiner.class, Joiner.on(','))
                    .put(Splitter.class, Splitter.on(','))
                    .put(com.google.common.base.Optional.class, com.google.common.base.Optional.absent())
                    .put(Predicate.class, Predicates.alwaysTrue())
                    .put(Equivalence.class, Equivalence.equals())
                    .put(Ticker.class, Ticker.systemTicker())
                    .put(Stopwatch.class, Stopwatch.createUnstarted())
                    // io types
                    .put(InputStream.class, new ByteArrayInputStream(new byte[0]))
                    .put(ByteArrayInputStream.class, new ByteArrayInputStream(new byte[0]))
                    .put(Readable.class, new StringReader(""))
                    .put(Reader.class, new StringReader(""))
                    .put(StringReader.class, new StringReader(""))
                    .put(Buffer.class, ByteBuffer.allocate(0))
                    .put(CharBuffer.class, CharBuffer.allocate(0))
                    .put(ByteBuffer.class, ByteBuffer.allocate(0))
                    .put(ShortBuffer.class, ShortBuffer.allocate(0))
                    .put(IntBuffer.class, IntBuffer.allocate(0))
                    .put(LongBuffer.class, LongBuffer.allocate(0))
                    .put(FloatBuffer.class, FloatBuffer.allocate(0))
                    .put(DoubleBuffer.class, DoubleBuffer.allocate(0))
                    .put(File.class, new File(""))
                    .put(ByteSource.class, ByteSource.empty())
                    .put(CharSource.class, CharSource.empty())
                    .put(ByteSink.class, NullByteSink.INSTANCE)
                    .put(CharSink.class, NullByteSink.INSTANCE.asCharSink(Charsets.UTF_8))
                    // All collections are immutable empty. So safe for any type parameter.
                    .put(Iterator.class, ImmutableSet.of().iterator())
                    .put(PeekingIterator.class, Iterators.peekingIterator(ImmutableSet.of().iterator()))
                    .put(ListIterator.class, ImmutableList.of().listIterator())
                    .put(Iterable.class, ImmutableSet.of())
                    .put(Collection.class, ImmutableList.of())
                    .put(ImmutableCollection.class, ImmutableList.of())
                    .put(List.class, ImmutableList.of())
                    .put(ImmutableList.class, ImmutableList.of())
                    .put(Set.class, ImmutableSet.of())
                    .put(ImmutableSet.class, ImmutableSet.of())
                    .put(SortedSet.class, ImmutableSortedSet.of())
                    .put(ImmutableSortedSet.class, ImmutableSortedSet.of())
                    .put(NavigableSet.class, Sets.unmodifiableNavigableSet(Sets.newTreeSet()))
                    .put(Map.class, ImmutableMap.of())
                    .put(ImmutableMap.class, ImmutableMap.of())
                    .put(SortedMap.class, ImmutableSortedMap.of())
                    .put(ImmutableSortedMap.class, ImmutableSortedMap.of())
                    .put(NavigableMap.class, Maps.unmodifiableNavigableMap(Maps.newTreeMap()))
                    .put(Multimap.class, ImmutableMultimap.of())
                    .put(ImmutableMultimap.class, ImmutableMultimap.of())
                    .put(ListMultimap.class, ImmutableListMultimap.of())
                    .put(ImmutableListMultimap.class, ImmutableListMultimap.of())
                    .put(SetMultimap.class, ImmutableSetMultimap.of())
                    .put(ImmutableSetMultimap.class, ImmutableSetMultimap.of())
                    .put(
                            SortedSetMultimap.class,
                            Multimaps.unmodifiableSortedSetMultimap(TreeMultimap.create()))
                    .put(Multiset.class, ImmutableMultiset.of())
                    .put(ImmutableMultiset.class, ImmutableMultiset.of())
                    .put(SortedMultiset.class, ImmutableSortedMultiset.of())
                    .put(ImmutableSortedMultiset.class, ImmutableSortedMultiset.of())
                    .put(BiMap.class, ImmutableBiMap.of())
                    .put(ImmutableBiMap.class, ImmutableBiMap.of())
                    .put(Table.class, ImmutableTable.of())
                    .put(ImmutableTable.class, ImmutableTable.of())
                    .put(RowSortedTable.class, Tables.unmodifiableRowSortedTable(TreeBasedTable.create()))
                    .put(ClassToInstanceMap.class, ImmutableClassToInstanceMap.builder().build())
                    .put(ImmutableClassToInstanceMap.class, ImmutableClassToInstanceMap.builder().build())
                    .put(Comparable.class, ByToString.INSTANCE)
                    .put(Comparator.class, AlwaysEqual.INSTANCE)
                    .put(Ordering.class, AlwaysEqual.INSTANCE)
                    .put(Range.class, Range.all())
                    .put(MapDifference.class, Maps.difference(ImmutableMap.of(), ImmutableMap.of()))
                    .put(
                            SortedMapDifference.class,
                            Maps.difference(ImmutableSortedMap.of(), ImmutableSortedMap.of()))
                    // reflect
                    .put(AnnotatedElement.class, Object.class)
                    .put(GenericDeclaration.class, Object.class)
                    .put(Type.class, Object.class)
                    .build();

    /**
     * type → implementation. Inherently mutable interfaces and abstract classes are mapped to their
     * default implementations and are "new"d upon get().
     */
    private static final ConcurrentMap<Class<?>, Class<?>> implementations = Maps.newConcurrentMap();

    private static <T> void setImplementation(Class<T> type, Class<? extends T> implementation) {
        checkArgument(type != implementation, "Don't register %s to itself!", type);
        checkArgument(
                !DEFAULTS.containsKey(type), "A default value was already registered for %s", type);
        checkArgument(
                implementations.put(type, implementation) == null,
                "Implementation for %s was already registered",
                type);
    }

    static {
        setImplementation(Appendable.class, StringBuilder.class);
        setImplementation(BlockingQueue.class, LinkedBlockingDeque.class);
        setImplementation(BlockingDeque.class, LinkedBlockingDeque.class);
        setImplementation(ConcurrentMap.class, ConcurrentHashMap.class);
        setImplementation(ConcurrentNavigableMap.class, ConcurrentSkipListMap.class);
        setImplementation(CountDownLatch.class, Dummies.DummyCountDownLatch.class);
        setImplementation(Deque.class, ArrayDeque.class);
        setImplementation(OutputStream.class, ByteArrayOutputStream.class);
        setImplementation(PrintStream.class, Dummies.InMemoryPrintStream.class);
        setImplementation(PrintWriter.class, Dummies.InMemoryPrintWriter.class);
        setImplementation(Queue.class, ArrayDeque.class);
        setImplementation(Random.class, Dummies.DeterministicRandom.class);
        setImplementation(
                ScheduledThreadPoolExecutor.class, Dummies.DummyScheduledThreadPoolExecutor.class);
        setImplementation(ThreadPoolExecutor.class, Dummies.DummyScheduledThreadPoolExecutor.class);
        setImplementation(Writer.class, StringWriter.class);
        setImplementation(Runnable.class, Dummies.DummyRunnable.class);
        setImplementation(ThreadFactory.class, Dummies.DummyThreadFactory.class);
        setImplementation(Executor.class, Dummies.DummyExecutor.class);
    }

    @SuppressWarnings("unchecked") // it's a subtype map
    @Nullable
    private static <T> Class<? extends T> getImplementation(Class<T> type) {
        return (Class<? extends T>) implementations.get(type);
    }

    private static final Logger logger = Logger.getLogger(ArbitraryInstances.class.getName());

    /**
     * Returns an arbitrary instance for {@code type}, or {@code null} if no arbitrary instance can be
     * determined.
     */
    @Nullable
    public static <T> T get(Class<T> type) {
        T defaultValue = DEFAULTS.getInstance(type);
        if (defaultValue != null) {
            return defaultValue;
        }
        Class<? extends T> implementation = getImplementation(type);
        if (implementation != null) {
            return get(implementation);
        }
        if (type == Stream.class) {
            return type.cast(Stream.empty());
        }
        if (type.isEnum()) {
            T[] enumConstants = type.getEnumConstants();
            return (enumConstants.length == 0) ? null : enumConstants[0];
        }
        if (type.isArray()) {
            return createEmptyArray(type);
        }
        T jvmDefault = Defaults.defaultValue(Primitives.unwrap(type));
        if (jvmDefault != null) {
            return jvmDefault;
        }
        if (Modifier.isAbstract(type.getModifiers()) || !Modifier.isPublic(type.getModifiers())) {
            return arbitraryConstantInstanceOrNull(type);
        }
        final Constructor<T> constructor;
        try {
            constructor = type.getConstructor();
        } catch (NoSuchMethodException e) {
            return arbitraryConstantInstanceOrNull(type);
        }
        constructor.setAccessible(true); // accessibility check is too slow
        try {
            return constructor.newInstance();
            /*
             * Do not merge the 2 catch blocks below. javac would infer a type of
             * ReflectiveOperationException, which Animal Sniffer would reject. (Old versions of
             * Android don't *seem* to mind, but there might be edge cases of which we're unaware.)
             */
        } catch (InstantiationException impossible) {
            throw new AssertionError(impossible);
        } catch (IllegalAccessException impossible) {
            throw new AssertionError(impossible);
        } catch (InvocationTargetException e) {
            logger.log(Level.WARNING, "Exception while invoking default constructor.", e.getCause());
            return arbitraryConstantInstanceOrNull(type);
        }
    }

    public static boolean contains(Class<?> type) {
        return DEFAULTS.containsKey(type);
    }

    @Nullable
    private static <T> T arbitraryConstantInstanceOrNull(Class<T> type) {
        Field[] fields = type.getDeclaredFields();
        Arrays.sort(fields, BY_FIELD_NAME);
        for (Field field : fields) {
            if (Modifier.isPublic(field.getModifiers())
                    && Modifier.isStatic(field.getModifiers())
                    && Modifier.isFinal(field.getModifiers())) {
                if (field.getGenericType() == field.getType() && type.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    try {
                        T constant = type.cast(field.get(null));
                        if (constant != null) {
                            return constant;
                        }
                    } catch (IllegalAccessException impossible) {
                        throw new AssertionError(impossible);
                    }
                }
            }
        }
        return null;
    }

    private static <T> T createEmptyArray(Class<T> arrayType) {
        return arrayType.cast(Array.newInstance(arrayType.getComponentType(), 0));
    }

    // Internal implementations of some classes, with public default constructor that get() needs.
    private static final class Dummies {

        public static final class InMemoryPrintStream extends PrintStream {
            public InMemoryPrintStream() {
                super(new ByteArrayOutputStream());
            }
        }

        public static final class InMemoryPrintWriter extends PrintWriter {
            public InMemoryPrintWriter() {
                super(new StringWriter());
            }
        }

        public static final class DeterministicRandom extends Random {
            public DeterministicRandom() {
                super(0);
            }
        }

        public static final class DummyScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {
            public DummyScheduledThreadPoolExecutor() {
                super(1);
            }
        }

        public static final class DummyCountDownLatch extends CountDownLatch {
            public DummyCountDownLatch() {
                super(0);
            }
        }

        public static final class DummyRunnable implements Runnable, Serializable {
            @Override
            public void run() {
            }
        }

        public static final class DummyThreadFactory implements ThreadFactory, Serializable {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r);
            }
        }

        public static final class DummyExecutor implements Executor, Serializable {
            @Override
            public void execute(Runnable command) {
            }
        }
    }

    private static final class NullByteSink extends ByteSink implements Serializable {
        private static final NullByteSink INSTANCE = new NullByteSink();

        @Override
        public OutputStream openStream() {
            return ByteStreams.nullOutputStream();
        }
    }

    // Compare by toString() to satisfy 2 properties:
    // 1. compareTo(null) should throw NullPointerException
    // 2. the order is deterministic and easy to understand, for debugging purpose.
    @SuppressWarnings("ComparableType")
    private static final class ByToString implements Comparable<Object>, Serializable {
        private static final ByToString INSTANCE = new ByToString();

        @Override
        public int compareTo(Object o) {
            return toString().compareTo(o.toString());
        }

        @Override
        public String toString() {
            return "BY_TO_STRING";
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }

    // Always equal is a valid total ordering. And it works for any Object.
    private static final class AlwaysEqual extends Ordering<Object> implements Serializable {
        private static final AlwaysEqual INSTANCE = new AlwaysEqual();

        @Override
        public int compare(Object o1, Object o2) {
            return 0;
        }

        @Override
        public String toString() {
            return "ALWAYS_EQUAL";
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }

    private ArbitraryInstances() {
    }
}
