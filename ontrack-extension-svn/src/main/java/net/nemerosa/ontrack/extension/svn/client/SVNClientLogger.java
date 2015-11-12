package net.nemerosa.ontrack.extension.svn.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.util.SVNDebugLogAdapter;
import org.tmatesoft.svn.util.SVNLogType;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

public class SVNClientLogger extends SVNDebugLogAdapter {

    private final Logger logger = LoggerFactory.getLogger(SVNClient.class);

    @Override
    public void log(SVNLogType logType, Throwable th, Level logLevel) {
        switch (logLevel.getName()) {
            case "SEVERE":
                logger.error(String.format("[%s]", logType.getName()), th);
                break;
            case "WARNING":
                logger.warn(String.format("[%s]", logType.getName()), th);
                break;
            case "INFO":
                logger.info(String.format("[%s]", logType.getName()), th);
                break;
            case "FINE":
                logger.debug(String.format("[%s]", logType.getName()), th);
                break;
            case "FINER":
            case "FINEST":
            default:
                logger.trace(String.format("[%s]", logType.getName()), th);
                break;
        }
    }

    @Override
    public void log(SVNLogType logType, String message, Level logLevel) {
        switch (logLevel.getName()) {
            case "SEVERE":
                logger.error("[{}] {}", logType.getName(), message);
                break;
            case "WARNING":
                logger.warn("[{}] {}", logType.getName(), message);
                break;
            case "INFO":
                logger.info("[{}] {}", logType.getName(), message);
                break;
            case "FINE":
                logger.debug("[{}] {}", logType.getName(), message);
                break;
            case "FINER":
            case "FINEST":
            default:
                logger.trace("[{}] {}", logType.getName(), message);
                break;
        }
    }

    @Override
    public void log(SVNLogType logType, String message, byte[] data) {
        try {
            String dataAsString = new String(data, "UTF-8");
            logger.trace("[{}][data] {}\n{}", logType.getName(), message, dataAsString);
        } catch (UnsupportedEncodingException e) {
            logger.trace("[{}][data] {}", logType.getName(), message);
        }
    }

}
