package io.geewit.utils.core.exceptions;

public class ExceptionUtils {

    public static String getSimpleStackTrace(Throwable ex, int maxFrames) {
        Throwable root = getRootCause(ex);
        StringBuilder sb = new StringBuilder();
        sb.append(root.getClass().getName());
        if (root.getMessage() != null) {
            sb.append(": ").append(root.getMessage());
        }

        StackTraceElement[] stackTrace = root.getStackTrace();
        int frames = Math.min(stackTrace.length, maxFrames);
        for (int i = 0; i < frames; i++) {
            sb.append("\n\tat ").append(stackTrace[i]);
        }

        if (stackTrace.length > frames) {
            sb.append("\n\t... ").append(stackTrace.length - frames).append(" more");
        }

        return sb.toString();
    }

    public static String getSimpleStackTrace(Throwable ex) {
        return getSimpleStackTrace(ex, 20); // 默认只输出前3帧
    }

    private static Throwable getRootCause(Throwable ex) {
        Throwable result = ex;
        while (result.getCause() != null) {
            result = result.getCause();
        }
        return result;
    }
}

