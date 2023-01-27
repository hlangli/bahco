package dk.langli.bahco.url;

import java.net.URLStreamHandler;

public abstract class ConfigurableUrlStreamHandler extends URLStreamHandler {
	public abstract void register();
}
