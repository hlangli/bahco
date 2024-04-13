package dk.langli.bahco;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Subject {
	private String name;
	private List<Subject> children;
	private Map<String, Subject> relatives;
}