package dk.langli.bahco.url.handlers;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import dk.langli.bahco.url.ConfigurableUrlStreamHandler;
import dk.langli.bahco.url.ConfigurableUrlStreamHandlerFactory;

public class ClasspathUrlStreamHandler extends ConfigurableUrlStreamHandler {
	@Override
	public void register() {
		ConfigurableUrlStreamHandlerFactory.getInstance().addHandler("classpath", this);
	}

	@Override
	protected URLConnection openConnection(URL u) throws IOException {
		String path = u.getPath();
		URL resourceUrl = null;
		if (path.startsWith("/")) {
			resourceUrl = ClassLoader.getSystemResource(u.getPath().substring(1));
		}
		else {
			Class<?> caller = null;
			try {
				caller = Class.forName(new Throwable().getStackTrace()[0].getClassName());
				resourceUrl = caller.getResource(path);
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return resourceUrl != null ? resourceUrl.openConnection() : null;
	}
}
