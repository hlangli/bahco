package dk.langli.bahco.function;

@FunctionalInterface
public interface ThrowableRunnable<E extends Throwable> {
	public void run() throws E;
}
