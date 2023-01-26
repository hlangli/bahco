package dk.langli.bahco.function;

@FunctionalInterface
public interface ThrowableFunction<T, R, E extends Throwable> {
    R apply(T t) throws E;
}
