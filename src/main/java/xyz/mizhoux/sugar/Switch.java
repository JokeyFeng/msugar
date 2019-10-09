package xyz.mizhoux.sugar;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Switch
 *
 * @author 之叶
 * @date   2019/10/05
 */
public class Switch<T, R> {

    /**
     * 输入
     */
    private final T input;

    /**
     * 输出
     */
    private R output;

    /**
     * 当前的条件
     */
    private Predicate<T> condition;

    /**
     * 是否已经存在过满足的条件
     */
    private boolean met;

    private Switch(T input) {
        this.input = input;
    }

    public static <T, R> Switch<T, R> on(T input) {
        return new Switch<>(input);
    }

    public static <T, R> Switch<T, R> on(T input, Class<R> outType) {
        return new Switch<>(input);
    }

    public Switch<T, R> is(T target) {
        return when(Predicate.isEqual(target));
    }

    public Switch<T, R> in(T... values) {
        return when(e -> Arrays.asList(values).contains(e));
    }

    public Switch<T, R> when(Predicate<T> condition) {
        if (met) { return this; }

        this.condition = Objects.requireNonNull(condition);
        return this;
    }

    public Switch<T, R> thenAccept(Consumer<T> action) {
        if (met) { return this; }

        requireNonNullArgAndCondition(action);

        if (condition.test(input)) {
            action.accept(input);

            // 标记已经存在过满足的条件
            met = true;
        }

        return this;
    }

    public void elseAccept(Consumer<T> action) {
        if (met) { return; }

        Objects.requireNonNull(action);
        // 之前没有任何一个条件被满足
        action.accept(input);
    }

    public Switch<T, R> thenGet(R value) {
        if (met) { return this; }

        requireNonNullCondition();

        // 满足条件
        if (condition.test(input)) {
            output = value;

            // 标记已经存在输出
            met = true;
        }

        return this;
    }

    public R elseGet(R value) {
        return met ? output : value;
    }

    public Switch<T, R> thenApply(Function<T, R> mapper) {
        if (met) { return this; }

        requireNonNullArgAndCondition(mapper);

        if (condition.test(input)) {
            output = mapper.apply(input);

            // 标记已经存在输出
            met = true;
        }

        return this;
    }

    public R elseApply(Function<T, R> mapper) {
        Objects.requireNonNull(mapper);

        return met ? output : mapper.apply(input);
    }

    public Switch<T, R> thenSupply(Supplier<R> supplier) {
        if (met) { return this; }

        requireNonNullArgAndCondition(supplier);

        if (condition.test(input)) {
            output = supplier.get();

            // 标记已经存在输出
            met = true;
        }

        return this;
    }

    public R elseSupply(Supplier<R> supplier) {
        Objects.requireNonNull(supplier);

        return met ? output : supplier.get();
    }

    public Switch<T, R> or(T target) {
        return or(Predicate.isEqual(target));
    }

    public Switch<T, R> or(Predicate<T> other) {
        if (met) { return this; }

        requireNonNullArgAndCondition(other);
        condition = condition.or(other);
        return this;
    }

    public Switch<T, R> and(Predicate<T> other) {
        if (met) { return this; }

        requireNonNullArgAndCondition(other);
        condition = condition.and(other);
        return this;
    }

    private void requireNonNullCondition() {
        if (condition == null) {
            throw new IllegalStateException("A condition must be set first.");
        }
    }

    private void requireNonNullArgAndCondition(Object arg) {
        Objects.requireNonNull(arg);
        requireNonNullCondition();
    }

}
