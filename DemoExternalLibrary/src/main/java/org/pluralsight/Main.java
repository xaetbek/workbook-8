package org.pluralsight;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import java.util.logging.Logger;

import com.github.lalyos.jfiglet.FigletFont;



public class Main {
    final static Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        //logMeLikeYouDo("☕");

        String asciiArt = FigletFont.convertOneLine("HaYaT");
        System.out.println(asciiArt);
    }

    private static void logMeLikeYouDo(String input) {

        if (logger.isDebugEnabled()) {
            logger.debug("This is debug : " + input);
        }
        if (logger.isInfoEnabled()) {
            logger.info("This is info : " + input);
        }
        logger.warn("This is warn : " + input);
        logger.error("This is error : " + input);
        logger.fatal("This is fatal : " + input);
    }
}