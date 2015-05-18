package pl.edu.agh.iiet.model;

import pl.edu.agh.iet.model.MinistryListEntry;
import pl.edu.agh.ztis.jcr.model.JCREntry;

public class MinistryListEntryJCREntryPair {
    private MinistryListEntry ministryListEntry;
    private JCREntry jcrEntry;

    public MinistryListEntryJCREntryPair(MinistryListEntry ministryListEntry, JCREntry jcrEntry) {
        this.ministryListEntry = ministryListEntry;
        this.jcrEntry = jcrEntry;
    }

    public String getCommonISSN() {
        if (ministryListEntry.getISSN().equals(jcrEntry.getISSN())) {
            return ministryListEntry.getISSN();
        } else {
            return null;
        }
    }

    public int getMinistryPoints() {
        return ministryListEntry.getMinistryPoints();
    }

    public double getImpactFactor() {
        return jcrEntry.getImpactFactor();
    }

    public MinistryListEntry getMinistryListEntry() {
        return ministryListEntry;
    }

    public void setMinistryListEntry(MinistryListEntry ministryListEntry) {
        this.ministryListEntry = ministryListEntry;
    }

    public JCREntry getJcrEntry() {
        return jcrEntry;
    }

    public void setJcrEntry(JCREntry jcrEntry) {
        this.jcrEntry = jcrEntry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MinistryListEntryJCREntryPair that = (MinistryListEntryJCREntryPair) o;

        if (ministryListEntry != null ? !ministryListEntry.equals(that.ministryListEntry) : that.ministryListEntry != null)
            return false;
        return !(jcrEntry != null ? !jcrEntry.equals(that.jcrEntry) : that.jcrEntry != null);

    }

    @Override
    public int hashCode() {
        int result = ministryListEntry != null ? ministryListEntry.hashCode() : 0;
        result = 31 * result + (jcrEntry != null ? jcrEntry.hashCode() : 0);
        return result;
    }
}
