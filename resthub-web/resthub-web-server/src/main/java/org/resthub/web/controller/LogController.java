package org.resthub.web.controller;

import javax.inject.Inject;
import javax.inject.Named;
import org.resthub.web.log.Log;
import org.resthub.web.log.LogStrategy;
import org.resthub.web.log.Logs;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Log Controller
 */
@Controller
@Profile("resthub-client-logging")
public class LogController {

    private LogStrategy logStrategy;

    @Inject @Named("defaultLogStrategy")
    public void setLogStrategy(LogStrategy logStrategy) {
        this.logStrategy = logStrategy;
    }

    @RequestMapping(value = "/api/log", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void logAction(@RequestBody Log log, @RequestHeader("User-Agent") String userAgent) {
        log.browser = userAgent;
        switch (log.level) {
            case debug:
                logStrategy.logDebug(log);
                break;
            case info:
                logStrategy.logInfo(log);
                break;
            case warn:
                logStrategy.logWarn(log);
                break;
            case error:
                logStrategy.logError(log);
                break;
            default:
                break;
        }
    }

    @RequestMapping(value = "/api/logs", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void log(@RequestBody Logs logs, @RequestHeader("User-Agent") String userAgent) {
        for (Log log : logs) {
            logAction(log, userAgent);
        }
    }
}
