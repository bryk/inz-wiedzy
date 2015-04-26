package pl.edu.agh.ztis.jcr.model;

public class JCREntry {
    private final int rank;
    private final String title;
    private final String ISSN;
    private final int totalCites;
    private final double impactFactor;
    private final double fiveYearImpactFactor;
    private final double immediacyIndex;
    private final double eigenfactorScore;
    private final double articleInfluenceScore;
    private final int articleCount;
    private final String citedHalfLife;

    private JCREntry(int rank, String title, String ISSN, int totalCites, double impactFactor, double fiveYearImpactFactor, double immediacyIndex, double eigenfactorScore, double articleInfluenceScore, int articleCount, String citedHalfLife) {
        this.rank = rank;
        this.title = title;
        this.ISSN = ISSN;
        this.totalCites = totalCites;
        this.impactFactor = impactFactor;
        this.fiveYearImpactFactor = fiveYearImpactFactor;
        this.immediacyIndex = immediacyIndex;
        this.eigenfactorScore = eigenfactorScore;
        this.articleInfluenceScore = articleInfluenceScore;
        this.articleCount = articleCount;
        this.citedHalfLife = citedHalfLife;
    }

    public String getCitedHalfLife() {
        return citedHalfLife;
    }

    public double getFiveYearImpactFactor() {
        return fiveYearImpactFactor;
    }

    public double getImmediacyIndex() {
        return immediacyIndex;
    }

    public double getEigenfactorScore() {
        return eigenfactorScore;
    }

    public double getArticleInfluenceScore() {
        return articleInfluenceScore;
    }

    public int getArticleCount() {
        return articleCount;
    }

    public int getRank() {
        return rank;
    }

    public String getTitle() {
        return title;
    }

    public String getISSN() {
        return ISSN;
    }

    public int getTotalCites() {
        return totalCites;
    }

    public double getImpactFactor() {
        return impactFactor;
    }

    public static class JCREntryBuilder {
        private int rank;
        private String title;
        private String issn;
        private int totalCites;
        private double impactFactor;
        private double fiveYearImpactFactor;
        private double immediacyIndex;
        private double eigenfactorScore;
        private double articleInfluenceScore;
        private int articleCount;
        private String citedHalfLife;


        public JCREntryBuilder withCitedHalfLife(String citedHalfLife) {
            this.citedHalfLife = citedHalfLife;
            return this;
        }

        public JCREntryBuilder withRank(int rank) {
            this.rank = rank;
            return this;
        }

        public JCREntryBuilder withRank(String rank) {
            this.rank = parseInt(rank);
            return this;
        }

        public JCREntryBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public JCREntryBuilder withISSN(String issn) {
            this.issn = issn;
            return this;
        }

        public JCREntryBuilder withTotalCites(int totalCites) {
            this.totalCites = totalCites;
            return this;
        }

        public JCREntryBuilder withTotalCites(String totalCites) {
            this.totalCites = parseInt(totalCites);
            return this;
        }

        public JCREntryBuilder withImpactFactor(double impactFactor) {
            this.impactFactor = impactFactor;
            return this;
        }

        public JCREntryBuilder withImpactFactor(String impactFactor) {
            this.impactFactor = parseDouble(impactFactor);
            return this;
        }

        public JCREntryBuilder withFiveYearImpactFactor(double fiveYearImpactFactor) {
            this.fiveYearImpactFactor = fiveYearImpactFactor;
            return this;
        }

        public JCREntryBuilder withFiveYearImpactFactor(String fiveYearImpactFactor) {
            this.fiveYearImpactFactor = parseDouble(fiveYearImpactFactor);
            return this;
        }

        public JCREntryBuilder withImmediacyIndex(double immediacyIndex) {
            this.immediacyIndex = immediacyIndex;
            return this;
        }

        public JCREntryBuilder withImmediacyIndex(String immediacyIndex) {
            this.immediacyIndex = parseDouble(immediacyIndex);
            return this;
        }

        public JCREntryBuilder withEigenfactorScore(double eigenfactorScore) {
            this.eigenfactorScore = eigenfactorScore;
            return this;
        }

        public JCREntryBuilder withEigenfactorScore(String eigenfactorScore) {
            this.eigenfactorScore = parseDouble(eigenfactorScore);
            return this;
        }

        public JCREntryBuilder withArticleInfluenceScore(double articleInfluenceScore) {
            this.articleInfluenceScore = articleInfluenceScore;
            return this;
        }

        public JCREntryBuilder withArticleInfluenceScore(String articleInfluenceScore) {
            this.articleInfluenceScore = parseDouble(articleInfluenceScore);
            return this;
        }

        public JCREntryBuilder withArticleCount(int articleCount) {
            this.articleCount = articleCount;
            return this;
        }

        public JCREntryBuilder withArticleCount(String articleCount) {
            this.articleCount = parseInt(articleCount);
            return this;
        }

        private int parseInt(String integer) {
            try {
                return Integer.parseInt(integer);
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        private double parseDouble(String d) {
            try {
                return Double.parseDouble(d);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }

        public JCREntry build() {
            return new JCREntry(rank, title, issn, totalCites, impactFactor, fiveYearImpactFactor, immediacyIndex, eigenfactorScore, articleInfluenceScore, articleCount, citedHalfLife);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JCREntry jcrEntry = (JCREntry) o;

        if (rank != jcrEntry.rank) return false;
        if (totalCites != jcrEntry.totalCites) return false;
        if (Double.compare(jcrEntry.impactFactor, impactFactor) != 0) return false;
        if (Double.compare(jcrEntry.fiveYearImpactFactor, fiveYearImpactFactor) != 0) return false;
        if (Double.compare(jcrEntry.immediacyIndex, immediacyIndex) != 0) return false;
        if (Double.compare(jcrEntry.eigenfactorScore, eigenfactorScore) != 0) return false;
        if (Double.compare(jcrEntry.articleInfluenceScore, articleInfluenceScore) != 0) return false;
        if (articleCount != jcrEntry.articleCount) return false;
        if (title != null ? !title.equals(jcrEntry.title) : jcrEntry.title != null) return false;
        if (ISSN != null ? !ISSN.equals(jcrEntry.ISSN) : jcrEntry.ISSN != null) return false;
        return !(citedHalfLife != null ? !citedHalfLife.equals(jcrEntry.citedHalfLife) : jcrEntry.citedHalfLife != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = rank;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (ISSN != null ? ISSN.hashCode() : 0);
        result = 31 * result + totalCites;
        temp = Double.doubleToLongBits(impactFactor);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(fiveYearImpactFactor);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(immediacyIndex);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(eigenfactorScore);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(articleInfluenceScore);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + articleCount;
        result = 31 * result + (citedHalfLife != null ? citedHalfLife.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JCREntry{" +
                "rank=" + rank +
                ", title='" + title + '\'' +
                ", ISSN='" + ISSN + '\'' +
                ", totalCites=" + totalCites +
                ", impactFactor=" + impactFactor +
                ", fiveYearImpactFactor=" + fiveYearImpactFactor +
                ", immediacyIndex=" + immediacyIndex +
                ", eigenfactorScore=" + eigenfactorScore +
                ", articleInfluenceScore=" + articleInfluenceScore +
                ", articleCount=" + articleCount +
                ", citedHalfLife='" + citedHalfLife + '\'' +
                '}';
    }
}
