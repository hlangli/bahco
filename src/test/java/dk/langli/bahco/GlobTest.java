package dk.langli.bahco;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class GlobTest {
	@Test
	public void testGlob() {
		assertNotNull(new Glob());
		String regex = Glob.toRegex("f!a*[b*]c?[c?]d%[d%][e[e%]][f!][f[f!]]{g}h,i{h,i}\\,\\E\\O\\");
		assertEquals("f!a.*[b*]c.[c?]d\\%[d%][e[e%]][f!][f[f!]](g)h,i(h|i),\\\\E\\O\\", regex);
		Glob.toRegex("file[-[!0]]");
		Glob.toRegex("[^!]");
		Glob.toRegex("[%a]");
	}
}
