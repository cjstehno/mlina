import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

appender('CONSOLE', ConsoleAppender){
    encoder(PatternLayoutEncoder){
        pattern = "%logger{35} - %msg%n"
    }
}

root(INFO, ['CONSOLE'])