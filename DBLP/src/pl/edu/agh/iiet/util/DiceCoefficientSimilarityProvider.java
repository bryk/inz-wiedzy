package pl.edu.agh.iiet.util;

import java.util.Arrays;

public class DiceCoefficientSimilarityProvider implements StringSimilarityProvider{
    @Override
    public double computeSimilarity(final String first, final String second) {
        if (first == null || second == null) {
            return 0;
        }
        if (first.equals(second)) {
            return 1;
        }
        if (first.length() < 2 || second.length() < 2) {
            return 0;
        }
        final int[] fBigrams = splitIntoBigrams(first);
        final int[] sBigrams = splitIntoBigrams(second);
        final int fLength = first.length() - 1;
        final int sLenght = second.length() - 1;
        int matches = 0, i = 0, j = 0;
        while (i < fLength && j < sLenght) {
            if (fBigrams[i] == sBigrams[j]) {
                matches += 2;
                i++;
                j++;
            } else if (fBigrams[i] < sBigrams[j])
                i++;
            else
                j++;
        }
        return (double) matches / (fLength + sLenght);

    }

    private int[] splitIntoBigrams(String s) {
        final int n = s.length() - 1;
        final int[] sPairs = new int[n];
        for (int i = 0; i <= n; i++)
            if (i == 0)
                sPairs[i] = s.charAt(i) << 16;
            else if (i == n)
                sPairs[i - 1] |= s.charAt(i);
            else
                sPairs[i] = (sPairs[i - 1] |= s.charAt(i)) << 16;
        Arrays.sort(sPairs);
        return sPairs;
    }
}