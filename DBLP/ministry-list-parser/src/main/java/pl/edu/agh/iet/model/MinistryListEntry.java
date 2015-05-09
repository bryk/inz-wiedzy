package main.java.pl.edu.agh.iet.model;

import java.util.List;

public class MinistryListEntry {
    private String title;
    private String ISSN;
    private int ministryPoints;

    MinistryListEntry(String title, String ISSN, int ministryPoints) {
        this.title = title;
        this.ISSN = ISSN;
        this.ministryPoints = ministryPoints;
    }

    public static MinistryListEntry fromList(List<String> list) {
        return new MinistryListEntry.Builder()
                .withTitle(list.get(1))
                .withISSN(list.get(2))
                .withMinistryPoints(Integer.parseInt(list.get(3)))
                .build();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getISSN() {
        return ISSN;
    }

    public void setISSN(String ISSN) {
        this.ISSN = ISSN;
    }

    public int getMinistryPoints() {
        return ministryPoints;
    }

    public void setMinistryPoints(int ministryPoints) {
        this.ministryPoints = ministryPoints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MinistryListEntry that = (MinistryListEntry) o;

        if (ministryPoints != that.ministryPoints) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        return !(ISSN != null ? !ISSN.equals(that.ISSN) : that.ISSN != null);

    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (ISSN != null ? ISSN.hashCode() : 0);
        result = 31 * result + ministryPoints;
        return result;
    }

    public static class Builder {
        private String title;
        private String ISSN;
        private int ministryPoints;

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withISSN(String ISSN) {
            this.ISSN = ISSN;
            return this;
        }

        public Builder withMinistryPoints(int ministryPoints) {
            this.ministryPoints = ministryPoints;
            return this;
        }

        public MinistryListEntry build() {
            return new MinistryListEntry(title, ISSN, ministryPoints);
        }
    }
}
