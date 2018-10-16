package org.zhare.design.circuit;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author xufeng.deng dennisdxf@gmail.com
 * @since 2018-10-15 22:30
 */
public class CircuitState implements State {
    private AtomicReference<State> state;

    private static final double SWITCH_THRESHOLD = 1.0 / 2.0;
    private static final long WINDOW_SLIDE_TIME_INTERVAL = 500;
    private static final long STATE_SWITCH_TS_INTERVAL = 5000;

    CircuitState() {
        SlideWindow window = SlideWindow.getWindow();
        this.state = new AtomicReference<State>(new Closed(this, window));
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(window, WINDOW_SLIDE_TIME_INTERVAL, WINDOW_SLIDE_TIME_INTERVAL, TimeUnit.MILLISECONDS);
    }

    public void markSuccess() {
        state.get().markSuccess();
    }

    public void markFailed() {
        state.get().markFailed();
    }

    public boolean isAvailable() {
        return state.get().isAvailable();
    }

    private void switchState(State oldState, State newState) {
        this.state.compareAndSet(oldState, newState);
    }

    private static class Closed implements State {
        private final CircuitState state;
        private final SlideWindow window;

        Closed(final CircuitState state, final SlideWindow window) {
            this.state = state;
            this.window = window;
        }

        public void markSuccess() {
            window.markSuccess();
        }

        public void markFailed() {
            window.markFailed();
            if (window.threshold() < SWITCH_THRESHOLD) {
                state.switchState(this, new Open(state, window));
            }
        }

        public boolean isAvailable() {
            return true;
        }
    }

    private static class Open implements State {
        private final CircuitState state;
        private final SlideWindow window;
        private final long switchTs;

        Open(CircuitState state, SlideWindow window) {
            this.state = state;
            this.window = window;
            switchTs = System.currentTimeMillis();
        }

        public void markSuccess() {
            window.markSuccess();
        }

        public void markFailed() {
            window.markSuccess();
        }

        public boolean isAvailable() {
            if (System.currentTimeMillis() - switchTs > STATE_SWITCH_TS_INTERVAL) {
                state.switchState(this, new HalfOpen(state, window));
                return true;
            }

            return false;
        }
    }

    private static class HalfOpen implements State {
        private final CircuitState state;
        private final SlideWindow window;

        HalfOpen(CircuitState state, SlideWindow window) {
            this.state = state;
            this.window = window;
        }

        public void markSuccess() {
            window.markSuccess();
            if (window.threshold() > SWITCH_THRESHOLD) {
                state.switchState(this, new Closed(state, window));
            }
        }

        public void markFailed() {
            window.markFailed();
            state.switchState(this, new Open(state, window));
        }

        public boolean isAvailable() {
            return true;
        }
    }

    private static class SlideWindow implements Runnable {
        private final AtomicInteger successCount;
        private final AtomicInteger failedCount;
        private final AtomicInteger rejectCount;
        private final AtomicInteger timeoutCount;
        private volatile long windowSlideTs;

        private static final SlideWindow WINDOW = new SlideWindow();

        static SlideWindow getWindow() {
            return WINDOW;
        }

        private SlideWindow() {
            this.successCount = new AtomicInteger(0);
            this.failedCount = new AtomicInteger(0);
            this.rejectCount = new AtomicInteger(0);
            this.timeoutCount = new AtomicInteger(0);
            this.windowSlideTs = System.currentTimeMillis();
        }

        void slideWindow() {
            successCount.set(0);
            failedCount.set(0);
            rejectCount.set(0);
            timeoutCount.set(0);
            windowSlideTs = System.currentTimeMillis();
        }

        void markSuccess() {
            successCount.incrementAndGet();
        }

        void markFailed() {
            failedCount.incrementAndGet();
        }

        double threshold() {
            int success = successCount.get();
            int failed = failedCount.get();
            return (double) success / (success + failed);
        }

        public void run() {
            slideWindow();
        }
    }
}
