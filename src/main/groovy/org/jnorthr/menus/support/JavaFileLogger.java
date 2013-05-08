package org.jnorthr.menus.support;
 
/**

 * JavaFileLogger.java
 * Demonstrates the use of java.util.logging API to log
 * program information into file system.
 *
 */
import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
 
public class JavaFileLogger {
 
    /**
     * Defining a log level. Level.ALL logs all levels of messages.
     */
    static Level logLevel = Level.ALL;
 
    public static void main(String args[]) throws Exception {
 
        /**
         * Defining a location of log file
         */
        String fileName = "SomeLogFile.log";
 
        /**
         * Create a file logger with a custom log formatter
         * If you provide the second parameter as null,
         * a default XML Formatter will be applied, and the
         * generated log file will be an XML
         */
        Logger fileLogger = getFileLogger(fileName, getCustomFormatter());
        /**
         * Set the Log level.
         */
        fileLogger.setLevel(logLevel);
 
        fileLogger.log(new LogRecord(logLevel,
                "We are starting to log into the file"));
        fileLogger.fine("This is a fine level of message");
        fileLogger.finer("This is a finer level of message");
        fileLogger.finest("This is a finest level of message");
 
        /**
         * Calling a callMe() method to show the usage of entering ()
         * and exiting () method of Logger.
         *
         * These method calls are used to log when you enter a method
         * and when you exit it.
         */
        new JavaFileLogger().callMe(fileLogger);
 
        /**
         * Just creating and logging some sample messages by going through a loop.
         */
        for (int i = 0; i < 5; i++)
            fileLogger.info((i + 1)
                    + " - A Quick brown fox jumps over the lazy dog");
 
    }
 
    public static Logger getFileLogger(String fileName, Formatter formatter) {
 
        /**
         * Find or create a logger for a named subsystem. If a logger has
         * already been created with the given name it is returned.
         * Otherwise a new logger is created.
         *
         * If a new logger is created its log level will be configured based
         * on the LogManager configuration and it will configured to also
         * send logging output to its parent's handlers. It will be
         * registered in the LogManager global namespace.
         */
        Logger logger = Logger.getLogger("org.jnorthr.menus.support.JavaLogger");
 
        try {
 
            /**
             * Define whether the log should be appended to the existing one
             * If set to false, the log file will be overwritten, not appended
             */
            boolean append = true;
 
            /**
             * Defining the limit of file size in Bytes.
             * This is tentatively the maximum data that will be written
             * to one log file.
             */
            int limit = 10240; // 1 KB Per log file, modify as required.
 
            /**
             * If a file is full (as defined by 'limit' value above), files will
             * be renamed and a new log file will be crated.
             *
             * Pattern defines how the log file should be renamed.
             *
             * In this example, we are using the same fileName as the pattern.
             * So the files will be renamed as:
             * myAppLogFile.log.0
             * myAppLogFile.log.1
             * myAppLogFile.log.2 ..and so on.
             */
            String pattern = fileName;
            /**
             * This is the maximum number of log files to be created.
             * If this number is reached, the last file will be truncated
             * and a new one will be created.
             */
            int numLogFiles = 50;

            /**
             * Creating a file handler based on the above parameters
             */
            try
            {
            	FileHandler  handler = new FileHandler(pattern, limit, numLogFiles, append);
                if (formatter != null)
                    handler.setFormatter(formatter);
 
                logger.addHandler(handler);
 			} 
 			catch (Exception e) 
 			{
 				System.out.println("... OverlappingFileLockException !!!");
 			} // end of catch
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return logger;
    }
 
    /**
     * This method will define a custom formatter and return it.
     * Formatter is used to format the way the output content
     * is logged to the sytem, in this case a file.
     */
    public static Formatter getCustomFormatter() {
        return new Formatter() {
            public String format(LogRecord record) {
                String recordStr = "{Date} " + new Date() + " {Log Level} "
                        + record.getLevel() + " {Class} "
                        + record.getSourceClassName() + " {Method} "
                        + record.getSourceMethodName() + " {Message} "
                        + record.getMessage() + "\n";
                return recordStr;
            }
        };
    }
 
    /**
     * Sample method to show the method entry and exit log usage
     */
    public void callMe(Logger logger) {
        /**
         * Getting the current method name.
         * This is an expensive method, but i have presented here for demonstration only.
         */
        String myMethodName = new Exception().getStackTrace()[0]
                .getMethodName();
        String myClassName = this.getClass().getName();
 
        /** Logging the method entry**/
        if (logger.isLoggable(logLevel))
            logger.entering(myClassName, myMethodName);
 
        logger.info("Middle of the method");
 
        /** Logging the method exit**/
        if (logger.isLoggable(logLevel))
            logger.exiting(myClassName, myMethodName);
 
    }
 
}