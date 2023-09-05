package dk.langli.bahco.url;

import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.HashMap;
import java.util.Map;

import dk.langli.bahco.url.handlers.ClasspathUrlStreamHandler;

public class ConfigurableUrlStreamHandlerFactory implements URLStreamHandlerFactory {
	private static final ConfigurableUrlStreamHandlerFactory instance = new ConfigurableUrlStreamHandlerFactory();
	private final Map<String, URLStreamHandler> protocolHandlers;

	public static void register() {
		URL.setURLStreamHandlerFactory(instance);
		new ClasspathUrlStreamHandler().register();
	}

	public static ConfigurableUrlStreamHandlerFactory getInstance() {
		return instance;
	}

	private ConfigurableUrlStreamHandlerFactory() {
		protocolHandlers = new HashMap<String, URLStreamHandler>();
	}

	public void addHandler(String protocol, URLStreamHandler urlHandler) {
		protocolHandlers.put(protocol, urlHandler);
	}

	public URLStreamHandler createURLStreamHandler(String protocol) {
		return protocolHandlers.get(protocol);
	}

	public boolean isSupported(String protocol) {
		return protocolHandlers.containsKey(protocol);
	}
}
