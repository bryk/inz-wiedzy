package pl.edu.agh.iiet.model;

public enum PublicationType {
	BOOK("BOOK"), WWW("WWW"), INPROCEEDINGS("INPROCEEDINGS"), PROCEEDINGS("PROCEEDINGS"), INCOLLECTION("INCOLLECTION");

    private String type;
    PublicationType(String name){
        this.type = name;
    }

    @Override
    public String toString() {
        return type;
    }
}
