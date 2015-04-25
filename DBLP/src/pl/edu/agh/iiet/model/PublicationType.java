package pl.edu.agh.iiet.model;

public enum PublicationType {
	ARTICLE("ARTICLE");

	private String type;

	PublicationType(String name) {
		this.type = name;
	}

	@Override
	public String toString() {
		return type;
	}
}
