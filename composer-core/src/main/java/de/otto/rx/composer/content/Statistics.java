package de.otto.rx.composer.content;

import org.slf4j.Logger;

import static java.lang.System.currentTimeMillis;
import static org.slf4j.LoggerFactory.getLogger;

public class Statistics {

    private static final Logger LOG = getLogger(Statistics.class);

    private final long startedTs;
    private final int numRequested;
    private final int numEmpty;
    private final int numErrors;
    private final int numNonEmpty;
    private final long avgNonEmptyMillis;
    private final long maxNonEmptyMillis;
    private final long runtime;
    private final String slowestFragment;
    private final int numFallbacksRequested;
    private final int numNonEmptyFallbacks;

    Statistics(final long startedTs, final int numRequested, final int numEmpty, final int numErrors,
               final int numNonEmpty, final long avgNonEmptyMillis, final long slowestNonEmptyMillis,
               final long runtime, final String slowestFragment, final int numFallbacksRequested,
               final int numNonEmptyFallbacks) {
        this.startedTs = startedTs;
        this.numRequested = numRequested;
        this.numEmpty = numEmpty;
        this.numErrors = numErrors;
        this.numNonEmpty = numNonEmpty;
        this.avgNonEmptyMillis = avgNonEmptyMillis;
        this.maxNonEmptyMillis = slowestNonEmptyMillis;
        this.runtime = runtime;
        this.slowestFragment = slowestFragment;
        this.numFallbacksRequested = numFallbacksRequested;
        this.numNonEmptyFallbacks = numNonEmptyFallbacks;
    }

    public static Statistics emptyStats() {
        return new StatsBuilder().build();
    }

    public static StatsBuilder statsBuilder() {
        return new StatsBuilder();
    }

    public long getStartedTs() {
        return startedTs;
    }

    public int getNumRequested() {
        return numRequested;
    }

    public int getNumEmpty() {
        return numEmpty;
    }

    public int getNumErrors() {
        return numErrors;
    }

    public int getNumNonEmpty() {
        return numNonEmpty;
    }

    public long getAvgNonEmptyMillis() {
        return avgNonEmptyMillis;
    }

    public long getMaxNonEmptyMillis() {
        return maxNonEmptyMillis;
    }

    public long getRuntime() {
        return runtime;
    }

    public long getTotalRuntime() {
        return currentTimeMillis() - startedTs;
    }

    public String getSlowestFragment() {
        return slowestFragment;
    }

    public int getNumFallbacksRequested() {
        return numFallbacksRequested;
    }

    public int getNumNonEmptyFallbacks() {
        return numNonEmptyFallbacks;
    }

    public void logStats() {
        LOG.info(toString());
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "startedTs=" + startedTs +
                ", numRequested=" + numRequested +
                ", numEmpty=" + numEmpty +
                ", numErrors=" + numErrors +
                ", numNonEmpty=" + numNonEmpty +
                ", avgNonEmptyMillis=" + avgNonEmptyMillis +
                ", maxNonEmptyMillis=" + maxNonEmptyMillis +
                ", runtime=" + runtime +
                ", slowestFragment='" + slowestFragment + '\'' +
                ", numFallbacksRequested=" + numFallbacksRequested +
                ", numNonEmptyFallbacks=" + numNonEmptyFallbacks +
                '}';
    }

    public static class StatsBuilder {
        public long sumNonEmptyMillis = 0;
        public long startedTs = 0;
        public int numRequested = 0;
        public int numEmpty = 0;
        public int numErrors = 0;
        public int numNonEmpty = 0;
        public long slowestNonEmptyMillis = 0;
        public long runtime = 0;
        public String slowestFragment = "";
        public int numFallbacksRequested = 0;
        public int numNonEmptyFallbacks = 0;

        private StatsBuilder() {
        }

        public Statistics build() {
            final long avgNonEmptyMillis = numNonEmpty != 0L ? sumNonEmptyMillis / numNonEmpty : 0L;
            return new Statistics(startedTs, numRequested, numEmpty, numErrors, numNonEmpty, avgNonEmptyMillis, slowestNonEmptyMillis, runtime, slowestFragment, numFallbacksRequested, numNonEmptyFallbacks);
        }
    }


}
