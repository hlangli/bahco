package dk.langli.bahco.function;

@FunctionalInterface
public interface ThrowableSupplier<T, E extends Throwable> {
	public T get() throws E;
}
