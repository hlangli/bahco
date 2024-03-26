package dk.langli.bahco.url.handlers;

import static dk.langli.bahco.Bahco.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import dk.langli.bahco.url.ConfigurableUrlStreamHandlerFactory;

public class ClasspathUrlStreamHandlerTest {
	@Test
	public void testClasspathUrlStreamHandler() throws IOException {
		assertFalse(ConfigurableUrlStreamHandlerFactory.getInstance().isSupported("classpath"));
		ConfigurableUrlStreamHandlerFactory.register();
		assertTrue(ConfigurableUrlStreamHandlerFactory.getInstance().isSupported("classpath"));
		{
			URL url = wrap(() -> new URL("classpath:/resource.txt"));
			String content = wrap(() -> IOUtils.toString(url.openStream(), Charsets.UTF_8));
			assertEquals("Swedish key", content.trim());
		}
		{
			URL url = wrap(() -> new URL("classpath:resource.txt"));
			String content = IOUtils.toString(url.openStream(), Charsets.UTF_8);
			assertEquals("SWeDiSH KeY", content.trim());
		}
		{
			URL url = wrap(() -> new URL("classpath:nonexistant.txt"));
			URLConnection urlConnection = url.openConnection();
			assertNull(urlConnection);
		}
	}
}
