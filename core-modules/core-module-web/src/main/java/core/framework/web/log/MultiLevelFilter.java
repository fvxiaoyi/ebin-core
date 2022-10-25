package core.framework.web.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ebin
 */
public class MultiLevelFilter extends AbstractMatcherFilter<ILoggingEvent> {
    private Set<Level> levels;

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }

        if (levels.contains(event.getLevel())) {
            return onMatch;
        } else {
            return onMismatch;
        }
    }

    public void setLevels(String levels) {
        this.levels = Arrays.stream(levels.split(",")).map(Level::toLevel).collect(Collectors.toSet());
    }

    public void start() {
        if (this.levels != null) {
            super.start();
        }
    }
}
