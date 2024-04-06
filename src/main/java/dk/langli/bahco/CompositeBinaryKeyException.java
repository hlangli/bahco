package dk.langli.bahco;

import java.util.ArrayList;

import dk.langli.bahco.CompositeBinaryKey.KeyDef;
import lombok.Getter;

@SuppressWarnings("serial")
@Getter
public class CompositeBinaryKeyException extends RuntimeException {
	private final ArrayList<KeyDef> keyDefs;

	public CompositeBinaryKeyException(ArrayList<KeyDef> keyDefs) {
		this(keyDefs, (String) null);
	}

	public CompositeBinaryKeyException(ArrayList<KeyDef> keyDefs, String message, Throwable cause) {
		super(message, cause);
		this.keyDefs = keyDefs;
	}

	public CompositeBinaryKeyException(ArrayList<KeyDef> keyDefs, String message) {
		this(keyDefs, message, null);
	}

	public CompositeBinaryKeyException(ArrayList<KeyDef> keyDefs, Throwable cause) {
		this(keyDefs, null, cause);
	}
}
